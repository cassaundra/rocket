package io.cassaundra.rocket

import io.cassaundra.rocket.midi.MidiDeviceConfiguration
import io.cassaundra.rocket.midi.MidiLaunchpad
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.sound.midi.MidiUnavailableException

/**
 * Manages [Launchpad]s and MIDI scanning.
 *
 * If the MIDI device has not been found, or if it was disconnected, pad/button colors and listeners are retained.
 */
object Rocket : LaunchpadListener {
	private val listeners: MutableList<LaunchpadListener> = arrayListOf()

	private var padRows = Array(8) { Array(8) { Color.OFF } }
	private var topButtons = Array(8) { Color.OFF }
	private var rightButtons = Array(8) { Color.OFF }

	private var client: LaunchpadClient? = null

	private val logger = LoggerFactory.getLogger(Rocket::class.java)

	init {
		setupShutdownHook()

		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)
	}

	@JvmOverloads @JvmStatic fun connect(scanRateSeconds: Long = 3) {
		val executor = Executors.newScheduledThreadPool(1)
		executor.scheduleAtFixedRate({ scan() }, 0, scanRateSeconds, TimeUnit.SECONDS)
	}

	private fun scan() {
		val config = MidiDeviceConfiguration.autodetect()

		if (config.inputDevice == null || config.outputDevice == null) {
			setLaunchpadClient(null)
		} else {
			if (!hasClient()) {
				try {
					setLaunchpadClient(MidiLaunchpad(config))
				} catch (exc: MidiUnavailableException) {
					logger.error("Could not setup MIDI launchpad", exc)
				}
			}
		}
	}

	@JvmStatic fun setLaunchpadClient(client: LaunchpadClient?) {
		this.client = client

		if(client == null)
			return

		client.setListener(this)

		client.clear()

		for (i in 0..7) {
			client.sendButtonColor(Button(i, isTop = true), topButtons[i])
			client.sendButtonColor(Button(i, isTop = false), rightButtons[i])
		}

		for (y in 0..7) {
			for (x in 0..7) {
				client.sendPadColor(Pad(x, y), padRows[y][x])
			}
		}
	}

	private fun setupShutdownHook() {
		Runtime.getRuntime().addShutdownHook(Thread {
			close()
		})
	}

	private fun close() {
		if(client == null)
			return
		
		client!!.clear()
		client!!.close()
	}

	/**
	 * Sets all pads and buttons to [Color.OFF].
	 */
	@JvmStatic fun clearAll() {
		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)

		if(client != null)
			client!!.clear()
	}

	/**
	 * Sets the color of [pad] to [color].
	 */
	@JvmStatic fun setPad(pad: Pad, color: Color) {
		val oldColor = padRows[pad.y][pad.x]

		if (oldColor === color) return

		padRows[pad.y][pad.x] = color

		if(client != null)
			client!!.sendPadColor(pad, color)
	}

	/**
	 * Sets the color of [pads] to [color].
	 */
	@JvmStatic fun setPads(pads: Set<Pad>, color: Color) {
		pads.forEach {
			setPad(it, color)
		}
	}

	/**
	 * Sets all pad colors (not buttons) to [color].
	 */
	@JvmStatic fun setAllPads(color: Color) {
		padRows.forEach {
			it.fill(color)
		}

		if(client != null)
			client!!.sendAllPadColors(color)
	}

	/**
	 * Sets a specific button's color to [color].
	 */
	@JvmStatic fun setButton(button: Button, color: Color) {
		val oldColor: Color

		if (button.isTop) {
			oldColor = topButtons[button.coord]
			topButtons[button.coord] = color
		} else {
			oldColor = rightButtons[button.coord]
			rightButtons[button.coord] = color
		}

		if (oldColor === color) return

		if(client != null)
			client!!.sendButtonColor(button, color)
	}

	/**
	 * Sets the color of all top buttons to [color].
	 */
	@JvmStatic fun setAllTopButtons(color: Color) {
		for (i in 0..7) {
			setButton(Button(i, isTop = true), color)
		}
	}

	/**
	 * Sets the color of all right buttons to [color].
	 */
	@JvmStatic fun setAllRightButtons(color: Color) {
		for (i in 0..7) {
			setButton(Button(i, isTop = false), color)
		}
	}

	/**
	 * Retrieves the color of [pad].
	 *
	 * If the MIDI Launchpad was disconnected, pad color information is retained.
	 */
	@JvmStatic fun getPadColor(pad: Pad) =
		padRows[pad.y][pad.x]

	/**
	 * Retrieves the color of [button].
	 *
	 * If the MIDI Launchpad was disconnected, button color information is retained.
	 */
	@JvmStatic fun getButtonColor(button: Button): Color {
		return if (button.isTop)
			topButtons[button.coord]
		else
			rightButtons[button.coord]
	}

	/**
	 * Whether or not the [LaunchpadClient] associated with this [Launchpad] is not null.
	 */
	@JvmStatic fun hasClient() : Boolean =
			client != null

	/**
	 * Display [text] in color [color] on the Launchpad. When the text has finished displaying, [onComplete] is run.
	 */
	@JvmOverloads @JvmStatic fun displayText(text: String, color: Color, onComplete: Runnable = Runnable {}) {
		if(client != null)
			client!!.displayText(text, color, onComplete)
	}

	/**
	 * Adds [listener] to the list of listeners.
	 */
	@JvmStatic fun addListener(listener: LaunchpadListener) {
		listeners.add(listener)
	}

	/**
	 * Removes [listener] from the list of listeners.
	 */
	@JvmStatic fun removeListener(listener: LaunchpadListener) {
		listeners.remove(listener)
	}

	override fun onPadDown(pad: Pad) {
		listeners.forEach { it.onPadDown(pad) }
	}

	override fun onPadUp(pad: Pad) {
		listeners.forEach { it.onPadUp(pad) }
	}

	override fun onButtonDown(button: Button) {
		listeners.forEach { it.onButtonDown(button) }
	}

	override fun onButtonUp(button: Button) {
		listeners.forEach { it.onButtonUp(button) }
	}
}