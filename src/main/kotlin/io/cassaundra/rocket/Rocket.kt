package io.cassaundra.rocket

import com.sun.org.apache.xpath.internal.operations.Bool
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

	private var isClosed = false

	init {
		setupShutdownHook()

		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)
	}

	private fun scan() {
		val config = MidiDeviceConfiguration.autodetect()

		if (config.inputDevice == null || config.outputDevice == null) {
			setLaunchpadClient(null)
		} else {
			if (client == null) {
				try {
					setLaunchpadClient(MidiLaunchpad(config))
				} catch (exc: MidiUnavailableException) {
					logger.error("Could not setup MIDI launchpad", exc)
				}
			}
		}
	}

	/**
	 * Whether or not a MIDI Launchpad is connected.
	 */
	@JvmStatic fun clientIsAvailable() : Boolean {
		if(isClosed) return false

		scan()
		return client != null
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
		if(clientIsAvailable()) {
			client!!.clear()
			client!!.close()
		}
		isClosed = true
	}

	/**
	 * Sets all pads and buttons to [Color.OFF].
	 */
	@JvmStatic fun clearAll() {
		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)

		if(clientIsAvailable())
			client!!.clear()
	}

	/**
	 * Sets the color of [pad] to [color].
	 */
	@JvmStatic fun setPad(pad: Pad, color: Color) {
		val oldColor = padRows[pad.y][pad.x]

		if (oldColor === color) return

		padRows[pad.y][pad.x] = color

		if(clientIsAvailable())
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

		if(clientIsAvailable())
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

		if(clientIsAvailable())
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
	 * Display [text] in color [color] on the Launchpad. When the text has finished displaying, [onComplete] is run.
	 */
	@JvmOverloads @JvmStatic fun displayText(text: String, color: Color, onComplete: Runnable = Runnable {}) {
		if(clientIsAvailable())
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