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
			sendShortMessage(ShortMessage.NOTE_ON, color.channel, getNote(pad), color.midiVelocity)
		} catch (e: InvalidMidiDataException) {
			e.printStackTrace()
		}

	}

	override fun setButtonColor(button: Button, color: Color) {
		try {
			if (button is Button.Top) {
				sendShortMessage(ShortMessage.CONTROL_CHANGE, color.channel, button.coord + 104, color.midiVelocity)
			} else {
				sendShortMessage(ShortMessage.NOTE_ON, color.channel, (7 - button.coord) * 10 + 19, color.midiVelocity)
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

	override fun displayText(text: String, color: Color, onComplete: Runnable) {
		onTextComplete = onComplete
		var bytes = byteArrayOf(240.toByte(), 0.toByte(), 32.toByte(), 41.toByte(), 2.toByte(), 24.toByte(), 20.toByte(), color.midiVelocity.toByte(), 0.toByte())
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
			val pad: Pad? = getPad(note)
			if (pad != null) {
				if (velocity == 0)
					launchpadListener.onPadUp(pad)
				else
					launchpadListener.onPadDown(pad)
			} else {
				val button = getRightButton(note)
				if (velocity == 0)
					launchpadListener.onButtonUp(button!!)
				else
					launchpadListener.onButtonDown(button!!)
			}
		}

		private fun handleControlChangeMessage(note: Int, velocity: Int) {
			if (velocity == 0) {
				launchpadListener.onButtonUp(getTopButton(note))
			} else {
				launchpadListener.onButtonDown(getTopButton(note))
			}
		}

		override fun close() {}

		// util

		private fun getPad(note: Int): Pad? {
			var note = note
			note -= 11
			val x = note % 10
			val y = (note - x) / 10
			return if (x < 0 || x > 7 || y < 0 || y > 7) null else Pad(x, y)
		}

		private fun getRightButton(note: Int): Button? {
			var note = note
			note -= 19
			note /= 10
			return if (note < 0 || note > 7) null else Button.Right(7 - note)
		}

		private fun getTopButton(note: Int): Button {
			var note = note
			note -= 104
			return Button.Top(note)
		}
	}
}