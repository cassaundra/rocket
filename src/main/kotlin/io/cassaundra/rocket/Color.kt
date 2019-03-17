package io.cassaundra.rocket

import kotlin.math.max
import kotlin.math.min

/**
 * A color to be used on the Launchpad. Each RGB value should be between 0 and 255 inclusive.
 */
data class Color(val red: Int, val green: Int, val blue: Int) {
	/**
	 * A color from a greyscale value.
	 */
	constructor(brightness: Int) : this(brightness, brightness, brightness)

	@Suppress("unused")
	companion object Util {
		@JvmField val OFF = Color(0)
		@JvmField val GRAY = Color(127)
		@JvmField val WHITE = Color(255)

		@JvmField val RED = Color(255, 0, 0)
		@JvmField val GREEN = Color(0, 255, 0)
		@JvmField val BLUE = Color(0, 0, 255)

		/**
		 * Converts an HSV color ([hue], [saturation], [value]) to a [Color] to be used on the Launchpad.
		 *
		 * [hue] will loop when outside the range (negative or positive) of 0 and 1.
		 * [saturation] and [value] will clamp between 0 and 1.
		 *
		 * Each HSV value should be in the 0..1 float range.
		 */
		@JvmStatic fun fromHSV(hue: Float, saturation: Float = 1f, value: Float = 1f): Color {
			var h = hue % 1f
			if(h < 0) {
				h += 1
			}

			val s = max(0f, min(1f, saturation))
			val v = max(0f, min(1f, value))

			var r = 0f
			var g = 0f
			var b = 0f

			val i: Int = (h * 6).toInt()
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

			return Color((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
		}
	}
}