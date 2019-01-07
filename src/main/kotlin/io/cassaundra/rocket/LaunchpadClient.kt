package io.cassaundra.rocket

/**
 * Used internally as a generic way of communicating with a MIDI Launchpad.
 */
interface LaunchpadClient {
	fun setListener(listener: LaunchpadListener)
	fun sendPadColor(pad: Pad, color: Color)
	fun sendButtonColor(button: Button, color: Color)
	fun clear()
	fun sendAllPadColors(color: Color)
	fun displayText(text: String, color: Int, onComplete: Runnable)
	fun close()
}