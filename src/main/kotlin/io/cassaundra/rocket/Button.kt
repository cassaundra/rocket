package io.cassaundra.rocket

class Button(coord: Int, val isTop: Boolean) {
	val coord: Int = if(coord in 0..7) coord else throw IllegalArgumentException("coord not in 0..7")

	companion object Util
}