package io.cassaundra.rocket

class NullLaunchpadClient : LaunchpadClient {
	override fun setListener(listener: LaunchpadListener) {}
	override fun sendPadColor(pad: Pad, color: Color) {}
	override fun sendButtonColor(button: Button, color: Color) {}
	override fun clear() {}
	override fun sendAllPadColors(color: Color) {}
	override fun displayText(text: String, color: Color, onComplete: Runnable) {}
	override fun close() {}
}
