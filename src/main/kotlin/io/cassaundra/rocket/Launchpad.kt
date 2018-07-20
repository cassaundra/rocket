package io.cassaundra.rocket

import com.sun.org.apache.xpath.internal.operations.Bool

/**
 * Represents the MIDI Launchpad.
 *
 * If the MIDI device has not been found, or if it was disconnected, pad/button colors and listeners are retained.
 */
class Launchpad(private var client: LaunchpadClient? = null) : LaunchpadListener {

	private val listeners: MutableList<LaunchpadListener> = arrayListOf()

	private var padRows = Array(8) { Array(8) { Color.OFF } }
	private var topButtons = Array(8) { Color.OFF }
	private var rightButtons = Array(8) { Color.OFF }

	init {
		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)
	}

	internal fun close() {
		client!!.clear()
		client!!.close()
	}

	/**
	 * Sets all pads and buttons to [Color.OFF].
	 */
	fun clearAll() {
		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)

		client!!.clear()
	}

	/**
	 * Sets the color of [pad] to [color].
	 */
	fun setPad(pad: Pad, color: Color) {
		val oldColor = padRows[pad.y][pad.x]

		if (oldColor === color) return

		padRows[pad.y][pad.x] = color

		client!!.sendPadColor(pad, color)
	}

	/**
	 * Sets the color of [pads] to [color].
	 */
	fun setPads(pads: Set<Pad>, color: Color) {
		pads.forEach {
			setPad(it, color)
		}
	}

	/**
	 * Sets all pad colors (not buttons) to [color].
	 */
	fun setAllPads(color: Color) {
		padRows.forEach {
			it.fill(color)
		}

		client!!.sendAllPadColors(color)
	}

	/**
	 * Sets a specific button's color to [color].
	 */
	fun setButton(button: Button, color: Color) {
		val oldColor: Color

		if (button.isTop) {
			oldColor = topButtons[button.coord]
			topButtons[button.coord] = color
		} else {
			oldColor = rightButtons[button.coord]
			rightButtons[button.coord] = color
		}

		if (oldColor === color) return

		client!!.sendButtonColor(button, color)
	}

	/**
	 * Sets the color of all top buttons to [color].
	 */
	fun setAllTopButtons(color: Color) {
		for (i in 0..7) {
			setButton(Button(i, isTop = true), color)
		}
	}

	/**
	 * Sets the color of all right buttons to [color].
	 */
	fun setAllRightButtons(color: Color) {
		for (i in 0..7) {
			setButton(Button(i, isTop = false), color)
		}
	}

	/**
	 * Retrieves the color of [pad].
	 *
	 * If the MIDI Launchpad was disconnected, pad color information is retained.
	 */
	fun getPadColor(pad: Pad) =
		padRows[pad.y][pad.x]

	/**
	 * Retrieves the color of [button].
	 *
	 * If the MIDI Launchpad was disconnected, button color information is retained.
	 */
	fun getButtonColor(button: Button): Color {
		return if (button.isTop)
			topButtons[button.coord]
		else
			rightButtons[button.coord]
	}

	/**
	 * Sets the LaunchpadClient and sends all pad/button colors.
	 */
	fun setLaunchpadClient(client: LaunchpadClient?) {
		this.client = client

		if(client == null) return;

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

	/**
	 * Whether or not the [LaunchpadClient] associated with this [Launchpad] is not null.
	 */
	fun hasClient() : Boolean =
			client != null

	/**
	 * Display [text] in color [color] on the Launchpad. When the text has finished displaying, [onComplete] is run.
	 */
	fun displayText(text: String, color: Color, onComplete: Runnable = Runnable {}) {
		client!!.displayText(text, color, onComplete)
	}

	/**
	 * Adds [listener] to the list of listeners.
	 */
	fun addListener(listener: LaunchpadListener) {
		listeners.add(listener)
	}

	/**
	 * Removes [listener] from the list of listeners.
	 */
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


interface LaunchpadClient {
	fun setListener(listener: LaunchpadListener)
	fun sendPadColor(pad: Pad, color: Color)
	fun sendButtonColor(button: Button, color: Color)
	fun clear()
	fun sendAllPadColors(color: Color)
	fun displayText(text: String, color: Color, onComplete: Runnable)
	fun close()
}

/**
 * Used for listening to events from a [Launchpad].
 *
 * @see [Launchpad.addListener]
 * @see [Launchpad.removeListener]
 */
interface LaunchpadListener {
	/**
	 * Called when [pad] is pressed.
	 */
	fun onPadDown(pad: Pad) {}

	/**
	 * Called when [pad] is released.
	 */
	fun onPadUp(pad: Pad) {}

	/**
	 * Called when [button] is pressed.
	 */
	fun onButtonDown(button: Button) {}

	/**
	 * Called when [button] is released.
	 */
	fun onButtonUp(button: Button) {}
}