package io.cassaundra.rocket

sealed class Button(coord: Int) {
	val coord: Int = if(coord in 0..7) coord else throw IllegalArgumentException("coord not in 0..7")

	class Top(coord: Int) : Button(coord)
	class Right(coord: Int) : Button(coord)
}