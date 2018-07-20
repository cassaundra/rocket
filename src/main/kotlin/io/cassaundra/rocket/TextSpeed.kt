package io.cassaundra.rocket

/**
 * Can be used in [Launchpad.displayText] to change the text scrolling speed dynamically.
 *
 * Example Kotlin usage:
 * ```
 * Rocket.launchpad.displayText(
 *     "${TextSpeed.SPEED_7} Fast text and ${TextSpeed.SPEED_2} slow text",
 *     Color.WHITE,
 *     Runnable { println("Done") }
 * )
 * ```
 */
object TextSpeed {
	val SPEED_1 = 1.toChar()
	val SPEED_2 = 2.toChar()
	val SPEED_3 = 3.toChar()
	val SPEED_4 = 4.toChar()
	val SPEED_5 = 5.toChar()
	val SPEED_6 = 6.toChar()
	val SPEED_7 = 7.toChar()
}
