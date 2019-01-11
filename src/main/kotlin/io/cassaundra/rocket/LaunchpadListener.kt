package io.cassaundra.rocket

/**
 * Used for listening to events from a Launchpad.
 *
 * @see [Rocket.addListener]
 * @see [Rocket.removeListener]
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