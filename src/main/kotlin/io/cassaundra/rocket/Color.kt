package io.cassaundra.rocket

import kotlin.math.max
import kotlin.math.min

/**
 * A color to be used on the Launchpad. Each RGB value should be between 0 and 63 inclusive.
 */
data class Color(val red: Int, val green: Int, val blue: Int) {
	companion object {
		@JvmField val OFF = Color(0, 0, 0)

		@JvmField val WHITE = Color(63, 63, 63)

		@JvmField val RED = Color(63, 0, 0)
		@JvmField val GREEN = Color(0, 63, 0)
		@JvmField val BLUE = Color(0, 0, 63)

		/**
		 * Converts an HSV color ([hue], [saturation], [value]) to a [Color] to be used on the Launchpad.
		 *
		 * [hue] will loop when outside the range (negative or positive) of 0 and 1.
		 * [saturation] and [value] will clamp between 0 and 1.
		 *
		 * Each HSV value should be in the 0..1 float range.
		 */
		@JvmStatic fun fromHSV(hue: Float, saturation: Float, value: Float): Color {
			var h = hue % 1f
			if (h < 0) {
				h += 1
			}

			val s = max(0f, min(1f, saturation))
			val v = max(0f, min(1f, value))

			var r = 0f
			var g = 0f
			var b = 0f

			val i : Int = (h * 6).toInt()
			val f = h * 6 - i
			val p = v * (1 - s)
			val q = v * (1 - f * s)
			val t = v * (1 - (1 - f) * s)

			when(i % 6) {
				0 -> {
					r = v
					g = t
					b = p
				}
				1 -> {
					r = q
					g = v
					b = p
				}
				2 -> {
					r = p
					g = v
					b = t
				}
				3 -> {
					r = p
					g = q
					b = v
				}
				4 -> {
					r = t
					g = p
					b = v
				}
				5 -> {
					r = v
					g = p
					b = q
				}
			}

			return Color((r * 63).toInt(), (g * 63).toInt(), (b * 63).toInt())
		}
	}
}