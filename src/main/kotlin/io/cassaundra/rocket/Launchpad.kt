package io.cassaundra.rocket

class Launchpad(var client: LaunchpadClient) : LaunchpadListener {

	private val listeners: MutableList<LaunchpadListener> = arrayListOf()

	private var padRows = Array(8) { Array(8) { Color.OFF } }
	private var topButtons = Array(8) { Color.OFF }
	private var rightButtons = Array(8) { Color.OFF }

	init {
		setLaunchpadClient(client)

		padRows.forEach { it.fill(Color.OFF) }
		topButtons.fill(Color.OFF)
		rightButtons.fill(Color.OFF)
	}

	fun close() {
		clearLaunchpadClient()
		client.close()
	}

	fun clearAll() {
		setAllPads(Color.OFF)
		setAllTopButtons(Color.OFF)
		setAllRightButtons(Color.OFF)
	}

	fun setPad(pad: Pad, color: Color) {
		val oldColor = padRows[pad.y][pad.x]

		if (oldColor === color) return

		padRows[pad.y][pad.x] = color

		client.setPadColor(pad, color)
	}

	fun setPads(color: Color, vararg pads: Pad) {
		setPads(pads.toList(), color)
	}

	fun setPads(pads: List<Pad>, color: Color) {
		pads.forEach {
			setPad(it, color)
		}
	}

	fun setAllPads(color: Color) {
		padRows.forEach {
			it.fill(color)
		}

		client.setAllPadColors(color)
	}

	fun setButton(button: Button, color: Color) {
		val oldColor: Color

		if (button is Button.Top) {
			oldColor = topButtons[button.coord]
			topButtons[button.coord] = color
		} else {
			oldColor = rightButtons[button.coord]
			rightButtons[button.coord] = color
		}

		if (oldColor === color) return

		client.setButtonColor(button, color)
	}

	fun setAllTopButtons(color: Color) {
		for (i in 0..7) {
			setButton(Button.Top(i), color)
		}
	}

	fun setAllRightButtons(color: Color) {
		for (i in 0..7) {
			setButton(Button.Right(i), color)
		}
	}

	fun getPadColor(pad: Pad) =
		padRows[pad.y][pad.x]

	fun getButtonColor(button: Button): Color {
		return if (button is Button.Top)
			topButtons[button.coord]
		else
			rightButtons[button.coord]
	}

	private fun clearLaunchpadClient() {
		client.setAllPadColors(Color.OFF)

		for (i in 0..7) {
			client.setButtonColor(Button.Top(i), Color.OFF)
			client.setButtonColor(Button.Right(i), Color.OFF)
		}
	}

	fun setLaunchpadClient(client: LaunchpadClient) {
		this.client = client

		client.setListener(this)

		clearLaunchpadClient()

		for (i in 0..7) {
			client.setButtonColor(Button.Top(i), topButtons[i])
			client.setButtonColor(Button.Right(i), rightButtons[i])
		}

		for (y in 0..7) {
			for (x in 0..7) {
				client.setPadColor(Pad(x, y), padRows[y][x])
			}
		}
	}

	fun displayText(text: String, color: Color, onComplete: Runnable) {
		client.displayText(text, color, onComplete)
	}

	fun addListener(listener: LaunchpadListener) {
		listeners.add(listener)
	}

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
	fun setPadColor(pad: Pad, color: Color)
	fun setButtonColor(button: Button, color: Color)
	fun setAllPadColors(color: Color)
	fun displayText(text: String, color: Color, onComplete: Runnable)
	fun close()
}

interface LaunchpadListener {
	fun onPadDown(pad: Pad) {}
	fun onPadUp(pad: Pad) {}
	fun onButtonDown(button: Button) {}
	fun onButtonUp(button: Button) {}
}