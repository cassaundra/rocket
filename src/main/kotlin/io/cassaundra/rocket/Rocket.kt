package io.cassaundra.rocket

import io.cassaundra.rocket.midi.MidiLaunchpadClient
import io.cassaundra.rocket.midi.MidiLaunchpadScanner
import kotlinx.coroutines.Runnable
import org.slf4j.LoggerFactory

/**
 * Manages Launchpads and MIDI scanning.
 *
 * If the MIDI device has not been found, or if it was disconnected, pad/button colors and listeners are retained.
 */
@Suppress("unused")
class Rocket : LaunchpadListener {
	private val listeners: MutableList<LaunchpadListener> = arrayListOf()

	private var padRows = Array(8) { Array(8) { Color.OFF } }
	private var topButtons = Array(8) { Color.OFF }
	private var rightButtons = Array(8) { Color.OFF }

	private var scanner: LaunchpadScanner? = null
		set(value) {
			field?.stopScan()
			field = value
		}

	var client: LaunchpadClient? = null
		set(value) {
			setLaunchpadClient(value)
			field = value
		}

	private val logger = LoggerFactory.getLogger(Rocket::class.java)

	private var isClosed = false

	init {
		setupShutdownHook()

		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)
	}

	/**
	 * Begins scanning using a [MidiLaunchpadScanner] with a scan rate (in millis) of [scanRateMillis]
	 *
	 * Runnable is used for cleaner interop with Java.
	 *
	 * @param[onSuccess] Runnable to be called when successfully found a [MidiLaunchpadClient]
	 */
	@JvmOverloads
	fun beginMidiScan(scanRateMillis: Long = 1000, onSuccess: Runnable = Runnable {}) {
		beginScan(MidiLaunchpadScanner(this, scanRateMillis), onSuccess)
	}

	/**
	 * Begins scanning using the configured [scanner], if available.
	 *
	 * @param[onSuccess] Runnable to be called when successfully found a [LaunchpadClient]
	 *
	 * @throws[IllegalStateException] if called when [scanner] is null
	 */
	@JvmOverloads
	fun beginScan(scanner: MidiLaunchpadScanner, onSuccess: Runnable = Runnable { }) {
		this.scanner = scanner
		scanner.beginScan(onSuccess)
	}

	/**
	 * Stops scanning for the MIDI device.
	 */
	fun stopScan() {
		if(scanner != null)
			scanner?.stopScan()
	}

	/**
	 * Whether or not a MIDI Launchpad is connected.
	 */
	fun midiClientIsAvailable(): Boolean {
		if (isClosed) return false

		scanner?.quickScan(Runnable {})

		return client != null
	}

	private fun setLaunchpadClient(client: LaunchpadClient?) {
		if (client == null)
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

	/**
	 * Closes MIDI device if open and stops scanning
	 */
	fun close() {
		if (midiClientIsAvailable()) {
			client?.clear()
			client?.close()
		}
		isClosed = true
		stopScan()
	}

	/**
	 * Sets all pads and buttons to [Color.OFF]. Thread-safe.
	 */
	@Synchronized
	fun clearAll() {
		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)

		client?.clear()
	}

	/**
	 * Sets the color of [pad] to [color]. Thread-safe.
	 */
	@Synchronized
	fun setPad(pad: Pad, color: Color) {
		val oldColor = padRows[pad.y][pad.x]

		if (oldColor === color) return

		padRows[pad.y][pad.x] = color

		client?.sendPadColor(pad, color)
	}

	/**
	 * Sets the color of [pads] to [color]. Thread-safe.
	 */
	@Synchronized
	fun setPads(pads: Set<Pad>, color: Color) {
		pads.forEach {
			setPad(it, color)
		}
	}

	/**
	 * Sets all pad colors (not buttons) to [color]. Thread-safe.
	 */
	fun setAllPads(color: Color) {
		padRows.forEach {
			it.fill(color)
		}

		client?.sendAllPadColors(color)
	}

	/**
	 * Sets a specific button's color to [color]. Thread-safe.
	 */
	@Synchronized
	fun setButton(button: Button, color: Color) {
		val oldColor: Color

		if (button.isTop) {
			oldColor = topButtons[button.coord]
			topButtons[button.coord] = color
		} else {
			oldColor = rightButtons[button.coord]
			rightButtons[button.coord] = color
		}

		if (oldColor == color) return

		client?.sendButtonColor(button, color)
	}

	/**
	 * Sets the color of [buttons] to [color]. Thread-safe.
	 */
	@Synchronized
	fun setButtons(buttons: Set<Button>, color: Color) {
		buttons.forEach {
			setButton(it, color)
		}
	}

	/**
	 * Sets the color of all top buttons to [color]. Thread-safe.
	 */
	fun setAllTopButtons(color: Color) {
		for (i in 0..7) {
			setButton(Button(i, isTop = true), color)
		}
	}

	/**
	 * Sets the color of all right buttons to [color]. Thread-safe.
	 */
	fun setAllRightButtons(color: Color) {
		for (i in 0..7) {
			setButton(Button(i, isTop = false), color)
		}
	}

	/**
	 * Retrieves the color of [pad]. Thread-safe.
	 *
	 * If the MIDI Launchpad was disconnected, pad color information is retained.
	 */
	@Synchronized
	fun getPadColor(pad: Pad) =
			padRows[pad.y][pad.x]

	/**
	 * Retrieves the color of [button]. Thread-safe.
	 *
	 * If the MIDI Launchpad was disconnected, button color information is retained.
	 */
	@Synchronized
	fun getButtonColor(button: Button): Color {
		return if (button.isTop)
			topButtons[button.coord]
		else
			rightButtons[button.coord]
	}

	/**
	 * Display [text] in color [color] on the Launchpad. When the text has finished displaying, [onComplete] is run. Thread-safe.
	 *
	 * See colors: https://customer.novationmusic.com/sites/customer/files/novation/downloads/10529/launchpad-mk2-programmers-reference-guide-v1-02.pdf
	 *
	 * @param[color] The color to use
	 */
	@Synchronized
	@JvmOverloads
	fun displayText(text: String, color: Color = Color.WHITE, onComplete: Runnable = Runnable {}) {
		client?.displayText(text, color, onComplete)
	}

	/**
	 * Adds [listener] to the list of listeners. Thread-safe.
	 */
	@Synchronized
	fun addListener(listener: LaunchpadListener) {
		listeners.add(listener)
	}

	/**
	 * Removes [listener] from the list of listeners. Thread-safe.
	 */
	@Synchronized
	fun removeListener(listener: LaunchpadListener) {
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