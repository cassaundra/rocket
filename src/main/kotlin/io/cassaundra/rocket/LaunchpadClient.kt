package io.cassaundra.rocket

/**
 * Used internally as a generic way of communicating with a Launchpad.
 *
 * @see[Rocket.client]
 */
interface LaunchpadClient {
	/**
	 * Set this client's [LaunchpadListener]
	 */
	fun setListener(listener: LaunchpadListener)

	/**
	 * Send a pad color change message to the device.
	 */
	fun sendPadColor(pad: Pad, color: Color)

	/**
	 * Send a button color change message to the device.
	 */
	fun sendButtonColor(button: Button, color: Color)

	/**
	 * Send a clear all message to the Launchpad.
	 */
	fun clear()

	/**
	 * Send a pad color change message to the device for all pads. The Launchpad MK2's MIDI design supports this is one message, so it's included here.
	 */
	fun sendAllPadColors(color: Color)

	/**
	 * Display text on the Launchpad. Call [onComplete] when done.
	 */
	fun displayText(text: String, color: Int, onComplete: Runnable)

	/**
	 * Close this connection.
	 */
	fun close()
}