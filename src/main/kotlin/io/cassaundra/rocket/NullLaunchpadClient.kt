package io.cassaundra.rocket

class NullLaunchpadClient : LaunchpadClient {
	override fun setListener(listener: LaunchpadListener) {}
	override fun setPadColor(pad: Pad, color: Color) {}
	override fun setButtonColor(button: Button, color: Color) {}
	override fun setAllPadColors(color: Color) {}
	override fun displayText(text: String, color: Color, onComplete: Runnable) {}
	override fun close() {}
}
