package io.cassaundra.rocket

interface LaunchpadClient {
	fun setListener(listener: LaunchpadListener)
	fun sendPadColor(pad: Pad, color: Color)
	fun sendButtonColor(button: Button, color: Color)
	fun clear()
	fun sendAllPadColors(color: Color)
	fun displayText(text: String, color: Color, onComplete: Runnable)
	fun close()
}