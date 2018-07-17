package io.cassaundra.rocket.midi

import io.cassaundra.rocket.Button
import io.cassaundra.rocket.Pad

fun Pad.Util.fromMidi(note: Int): Pad? {
	var note = note
	note -= 11
	val x = note % 10
	val y = (note - x) / 10
	return if (x !in 0..7 || y !in 0..7) null else Pad(x, y)
}

fun Button.Util.fromMidiTop(note: Int): Button {
	var note = note
	note -= 104
	return Button(note, isTop = true)
}

fun Button.Util.fromMidiRight(note: Int): Button? {
	var note = note
	note -= 19
	note /= 10
	return if (note !in 0..7) null else Button(7 - note, isTop = false)
}