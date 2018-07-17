package io.cassaundra.rocket

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Pad(x: Int, y: Int) {
	val x: Int = if(x in 0..7) x else throw IllegalArgumentException("x not in 0..7")
	val y: Int = if(y in 0..7) y else throw IllegalArgumentException("y not in 0..7")

	companion object Util {
		@JvmStatic fun rect(p1: Pad, p2: Pad) : List<Pad> {
			val x1: Int = Math.min(p1.x, p2.x)
			val x2: Int = Math.max(p1.x, p2.x)
			val y1: Int = Math.min(p1.y, p2.y)
			val y2: Int = Math.max(p1.y, p2.y)

			val pads: ArrayList<Pad> = ArrayList()

			for(x in x1..x2) {
				for(y in y1..y2) {
					pads.add(Pad(x, y))
				}
			}

			return pads
		}

		@JvmStatic fun line(p1: Pad, p2: Pad) : List<Pad> {
			val xDiff = p2.x - p1.x
			val yDiff = p2.y - p1.y

			val iterations = max(abs(xDiff), abs(yDiff))

			val pads: ArrayList<Pad> = ArrayList()

			for(i in (0..iterations)) {
				val t = i * (1.0 / iterations)

				val x = p1.x + t * xDiff + 0.5
				val y = p1.y + t * yDiff + 0.5

				pads.add(Pad(x.toInt(), y.toInt()))
			}

			return pads
		}

        @JvmStatic fun all() : List<Pad> {
			val pads = mutableListOf<Pad>()

			for(y in (0..7)) {
				for(x in (0..7)) {
					pads.add(Pad(x, y))
				}
			}
			return pads
		}
	}
}