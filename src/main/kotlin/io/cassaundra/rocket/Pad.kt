package io.cassaundra.rocket

class Pad(x: Int, y: Int) {
	val x: Int = if(x in 0..7) x else throw IllegalArgumentException("x not in 0..7")
	val y: Int = if(y in 0..7) y else throw IllegalArgumentException("y not in 0..7")

	companion object Util {
		@JvmStatic fun getPadsInRect(p1: Pad, p2: Pad) : List<Pad> {
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

        @JvmStatic fun getAllPads() : List<Pad> {
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