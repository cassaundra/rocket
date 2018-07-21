package io.cassaundra.rocket

/**
 * Can be used in [Launchpad.displayText] to change the text scrolling speed dynamically.
 *
 * **Example Kotlin usage:**
 * ```
 * Rocket.launchpad.displayText(
 *     "${TextSpeed.SPEED_7}Fast text and ${TextSpeed.SPEED_2}slow text",
 *     Color.WHITE,
 *     Runnable { println("Done") }
 * )
 * ```
 *
 * **Example Java usage:**
 * ```
 * Rocket.INSTANCE.getLaunchpad().displayText(
 *     TextSpeed.SPEED_7 + "Fast text and " + TextSpeed.SPEED_2 + "slow text",
 *     Color.WHITE,
 *     () -> System.out.println("Done")
 * );
 * ```
 */
object TextSpeed {
	const val SPEED_1 = 1.toChar()
	const val SPEED_2 = 2.toChar()
	const val SPEED_3 = 3.toChar()
	const val SPEED_4 = 4.toChar()
	const val SPEED_5 = 5.toChar()
	const val SPEED_6 = 6.toChar()
	const val SPEED_7 = 7.toChar()
}
