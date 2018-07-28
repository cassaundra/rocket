package io.cassaundra.rocket

/**
 * Represents a button on the Launchpad.
 * @throws[IllegalArgumentException] if [coord] is not in the range 0..7.
 *
 * @property[coord] The button's coordinate, in the range 0..7.
 *
 * @property[isTop] Whether or not a button is located on the top of the Launchpad. If false, it's located on the right side.
 */
class Button(coord: Int, val isTop: Boolean) {
	val coord: Int = if (coord in 0..7) coord else throw IllegalArgumentException("coord not in 0..7")

	companion object Util {
		/**
		 * Gets all buttons on the top.
		 */
		@JvmStatic val allTop = (0..7).map {
			Button(it, true)
		}.toSet()

		/**
		 * Gets all buttons on the right.
		 */
		@JvmStatic val allRight = (0..7).map {
			Button(it, false)
		}.toSet()

		/**
		 * Gets all buttons on the top and the right.
		 */
		@JvmStatic val all = allTop + allRight
	}
}