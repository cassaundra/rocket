package io.cassaundra.rocket

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Represents a pad on the Launchpad at the coordinates [x], [y]
 *
 * @throws[IllegalArgumentException] if either [x] or [y] is not in the range 0..7.
 *
 * @property[x] The x-coordinate of the pad. Must be in the range 0..7.
 * @property[y] The y-coordinate of the pad, Must be in the range 0..7.
 */
class Pad(x: Int, y: Int) {
	val x: Int = if(x in 0..7) x else throw IllegalArgumentException("x not in 0..7")
	val y: Int = if(y in 0..7) y else throw IllegalArgumentException("y not in 0..7")

	companion object Util {
		@JvmStatic fun rect(p1: Pad, p2: Pad) : Set<Pad> {
			val x1: Int = Math.min(p1.x, p2.x)
			val x2: Int = Math.max(p1.x, p2.x)
			val y1: Int = Math.min(p1.y, p2.y)
			val y2: Int = Math.max(p1.y, p2.y)

			val pads: MutableSet<Pad> = mutableSetOf()

			for(x in x1..x2) {
				for(y in y1..y2) {
					pads.add(Pad(x, y))
				}
			}

			return pads
		}

		@JvmStatic fun rectOutline(p1: Pad, p2: Pad) : Set<Pad> {
			val pads: MutableSet<Pad> = mutableSetOf()

			// add horizontal
			val minX = min(p1.x, p2.x)
			val maxX = max(p1.x, p2.x)
			for(x in minX..maxX) {
				pads.add(Pad(x, p1.y))
				pads.add(Pad(x, p2.y))
			}

			// add vertical
			val minY = min(p1.y, p2.y)
			val maxY = max(p1.y, p2.y)
			for(y in minY..maxY) {
				pads.add(Pad(p1.x, y))
				pads.add(Pad(p2.x, y))
			}

			return pads
		}

		@JvmStatic fun line(p1: Pad, p2: Pad) : Set<Pad> {
			val xDiff = p2.x - p1.x
			val yDiff = p2.y - p1.y

			val iterations = max(abs(xDiff), abs(yDiff))

			val pads: MutableSet<Pad> = mutableSetOf()

			for(i in 0..iterations) {
				val t = i * (1.0 / iterations)

				val x = p1.x + t * xDiff + 0.5
				val y = p1.y + t * yDiff + 0.5

				pads.add(Pad(x.toInt(), y.toInt()))
			}

			return pads
		}

        @JvmStatic fun all() : Set<Pad> {
			val pads = mutableSetOf<Pad>()

			for(y in (0..7)) {
				for(x in (0..7)) {
					pads.add(Pad(x, y))
				}
			}
			return pads
		}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Pad

		if (x != other.x) return false
		if (y != other.y) return false

		return true
	}

	override fun hashCode(): Int {
		var result = x
		result = 31 * result + y
		return result
	}
}