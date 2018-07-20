package io.cassaundra.rocket.midi

import io.cassaundra.rocket.*
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import javax.sound.midi.*

class MidiLaunchpad @Throws(MidiUnavailableException::class)
constructor(private val configuration: MidiDeviceConfiguration?) : LaunchpadClient {
	private val logger = LoggerFactory.getLogger(MidiLaunchpad::class.java)

	private val receiver: Receiver? // LP -> Rocket
	private val transmitter: Transmitter? // Rocket -> LP

	private var openedOutputDevice = false
	private var openedInputDevice = false

	private var onTextComplete: Runnable = Runnable {  }

	init {
		val outputDevice = configuration!!.outputDevice
		if (outputDevice != null) {
			if (!outputDevice.isOpen) {
				outputDevice.open()
			}
			openedOutputDevice = true
			receiver = outputDevice.receiver
		} else
			receiver = null

		val inputDevice = configuration.inputDevice
		if (inputDevice != null) {
			if (!inputDevice.isOpen) {
				inputDevice.open()
			}
			openedInputDevice = true
			transmitter = inputDevice.transmitter
		} else
			transmitter = null
	}

	override fun close() {
		if (configuration == null) {
			return
		}
		if (openedOutputDevice) {
			val outputDevice = configuration.outputDevice
			if (outputDevice != null && outputDevice.isOpen)
				outputDevice.close()
		}
		if (openedInputDevice) {
			val inputDevice = configuration.inputDevice
			if (inputDevice != null && inputDevice.isOpen)
				inputDevice.close()
		}
	}

	override fun setListener(listener: LaunchpadListener) {
		transmitter!!.receiver = MidiLaunchpadReceiver(listener)
	}

	override fun setPadColor(pad: Pad, color: Color) {
		try {
			sendLEDChange(getNote(pad), color)
		} catch (e: InvalidMidiDataException) {
			e.printStackTrace()
		}

	}

	override fun setButtonColor(button: Button, color: Color) {
		try {
			if (button.isTop) {
				sendLEDChange(button.coord + 104, color)
			} else {
				sendLEDChange((7 - button.coord) * 10 + 19, color)
			}
		} catch (e: InvalidMidiDataException) {
			logger.error("Invalid MIDI data", e)
		}
	}

	override fun setAllPadColors(color: Color) {
		for (y in 0..7) {
			for (x in 0..7) {
				setPadColor(Pad(x, y), color)
			}
		}
	}

	private fun sendLEDChange(note: Int, color: Color) {
		sendSysExMessage(byteArrayOf(240.toByte(), 0.toByte(), 32.toByte(), 41.toByte(), 2.toByte(), 24.toByte(), 11.toByte(), note.toByte(), color.red.toByte(), color.green.toByte(), color.blue.toByte(), 247.toByte()))
	}

	override fun displayText(text: String, color: Color, onComplete: Runnable) {
		onTextComplete = onComplete
		var bytes = byteArrayOf(240.toByte(), 0.toByte(), 32.toByte(), 41.toByte(), 2.toByte(), 24.toByte(), 20.toByte(), 3.toByte(), 0.toByte())
		bytes += text.toByteArray(StandardCharsets.US_ASCII)
		bytes += 247.toByte()

		sendSysExMessage(bytes)
	}

	// midi utils

	@Throws(InvalidMidiDataException::class)
	private fun sendShortMessage(command: Int, channel: Int, controller: Int, data: Int) {
		val message = ShortMessage()
		message.setMessage(command, channel, controller, data)
		send(message)
	}

	@Throws(InvalidMidiDataException::class)
	private fun sendSysExMessage(data: ByteArray) {
		val message = SysexMessage()
		message.setMessage(data, data.size)
		send(message)
	}


	private fun send(message: MidiMessage) {
		receiver!!.send(message, -1)
	}

	private fun getNote(pad: Pad): Int {
		return 11 + pad.x + pad.y * 10
	}

	// listening

	inner class MidiLaunchpadReceiver(private var launchpadListener: LaunchpadListener) : Receiver {

		override fun send(message: MidiMessage, timestamp: Long) = when (message) {
			is ShortMessage ->
				handleShortMessage(message)
			is SysexMessage -> {
				onTextComplete.run()
				onTextComplete = Runnable {  }
			}
			else -> throw RuntimeException("Unknown event: $message")
		}

		private fun handleShortMessage(message: ShortMessage) {
			val status = message.status
			val note = message.data1
			val velocity = message.data2

			when (status) {
				ShortMessage.NOTE_ON -> handleNoteOnMessage(note, velocity)
				ShortMessage.CONTROL_CHANGE -> handleControlChangeMessage(note, velocity)
				else -> throw RuntimeException("Unknown event: $message")
			}
		}

		private fun handleNoteOnMessage(note: Int, velocity: Int) {
			val pad: Pad? = Pad.fromMidi(note)
			if (pad != null) {
				if (velocity == 0)
					launchpadListener.onPadUp(pad)
				else
					launchpadListener.onPadDown(pad)
			} else {
				val button = Button.fromMidiRight(note)!!
				if (velocity == 0)
					launchpadListener.onButtonUp(button)
				else
					launchpadListener.onButtonDown(button)
			}
		}

		private fun handleControlChangeMessage(note: Int, velocity: Int) {
			val button = Button.fromMidiTop(note)
			if (velocity == 0) {
				launchpadListener.onButtonUp(button)
			} else {
				launchpadListener.onButtonDown(button)
			}
		}

		override fun close() {}
	}
}