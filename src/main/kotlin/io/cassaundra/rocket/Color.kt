package io.cassaundra.rocket

/**
 *
 */
data class Color(val red: Int, val green: Int, val blue: Int) {
	companion object {
		@JvmField val OFF = Color(0, 0, 0)

		@JvmField val WHITE = Color(63, 63, 63)

		@JvmField val RED = Color(63, 0, 0)
		@JvmField val GREEN = Color(0, 63, 0)
		@JvmField val BLUE = Color(0, 0, 63)
	}
}