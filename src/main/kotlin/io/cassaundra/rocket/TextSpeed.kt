package io.cassaundra.rocket

/**
 * Can be used in [Rocket.displayText] to change the text scrolling speed dynamically.
 *
 * **Example Kotlin usage:**
 * ```
 * Rocket.displayText(
 *     "${TextSpeed.SPEED_7}Fast text and ${TextSpeed.SPEED_2}slow text",
 *     Color.WHITE,
 *     Runnable { println("Done") }
 * )
 * ```
 *
 * **Example Java usage:**
 * ```
 * Rocket.displayText(
 *     TextSpeed.SPEED_7 + "Fast text and " + TextSpeed.SPEED_2 + "slow text",
 *     Color.WHITE,
 *     () -> System.out.println("Done")
 * );
 * ```
 */
@Suppress("unused")
object TextSpeed {
	const val SPEED_0 = 1.toChar()
	const val SPEED_1 = 2.toChar()
	const val SPEED_2 = 3.toChar()
	const val SPEED_3 = 4.toChar()
	const val SPEED_4 = 5.toChar()
	const val SPEED_5 = 6.toChar()
	const val SPEED_6 = 7.toChar()
}
