package io.cassaundra.rocket.midi

import io.cassaundra.rocket.Button
import io.cassaundra.rocket.Color
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

/**
 * Hardcoded Launchpad MK2 palette lookup
 *
 * Source: http://launchpaddr.com/mk2palette/
 */
val colorPalette = arrayOf(
		Color(0, 0, 0), // row 0
		Color(28, 28, 28),
		Color(124, 124, 124),
		Color(252, 252, 252),
		Color(255, 78, 72),
		Color(254, 10, 0),
		Color(90, 0, 0),
		Color(24, 0, 2),
		Color(255, 188, 99), // row 1
		Color(255, 87, 0),
		Color(90, 29, 0),
		Color(36, 24, 2),
		Color(253, 253, 33),
		Color(253, 253, 0),
		Color(88, 88, 0),
		Color(24, 24, 0),
		Color(129, 253, 43), // row 2
		Color(64, 253, 1),
		Color(22, 88, 0),
		Color(19, 40, 1),
		Color(53, 253, 43),
		Color(0, 254, 0),
		Color(0, 88, 1),
		Color(0, 24, 0),
		Color(53, 253, 43),  // row 3
		Color(0, 254, 0),
		Color(0, 88, 1),
		Color(0, 24, 0),
		Color(50, 253, 127),
		Color(0, 253, 58),
		Color(1, 88, 20),
		Color(0, 28, 14),
		Color(47, 252, 177), // row 4
		Color(0, 251, 145),
		Color(1, 87, 50),
		Color(1, 24, 16),
		Color(57, 190, 255),
		Color(0, 167, 255),
		Color(1, 64, 81),
		Color(0, 16, 24),
		Color(65, 134, 255), // row 5
		Color(0, 80, 255),
		Color(1, 26, 90),
		Color(1, 6, 25),
		Color(71, 71, 255),
		Color(0, 0, 254),
		Color(0, 0, 90),
		Color(0, 0, 24),
		Color(131, 71, 255), // row 6
		Color(80, 0, 255),
		Color(22, 0, 103),
		Color(10, 0, 50),
		Color(255, 72, 254),
		Color(255, 0, 254),
		Color(90, 0, 90),
		Color(24, 0, 24),
		Color(251, 78, 131), // row 7
		Color(255, 7, 83),
		Color(90, 2, 27),
		Color(33, 1, 16),
		Color(255, 25, 1),
		Color(154, 53, 0),
		Color(122, 81, 1),
		Color(62, 101, 0),
		Color(1, 56, 0), // row 8
		Color(0, 84, 50),
		Color(0, 83, 127),
		Color(0, 0, 254),
		Color(1, 68, 77),
		Color(26, 0, 209),
		Color(124, 124, 124),
		Color(32, 32, 32),
		Color(255, 10, 0), // row 9
		Color(186, 253, 0),
		Color(172, 136, 0),
		Color(86, 253, 0),
		Color(0, 136, 0),
		Color(1, 252, 123),
		Color(0, 167, 255),
		Color(2, 26, 255),
		Color(53, 0, 255), // row 10
		Color(120, 0, 255),
		Color(180, 23, 126),
		Color(65, 32, 0),
		Color(255, 75, 1),
		Color(130, 225, 0),
		Color(102, 253, 0),
		Color(0, 254, 0),
		Color(0, 254, 0), // row 11
		Color(69, 253, 97),
		Color(1, 251, 203),
		Color(80, 134, 255),
		Color(39, 77, 200),
		Color(132, 122, 237),
		Color(211, 12, 255),
		Color(255, 6, 90),
		Color(255, 125, 1), // row 12
		Color(184, 177, 0),
		Color(138, 253, 0),
		Color(129, 93, 0),
		Color(58, 40, 2),
		Color(13, 76, 5),
		Color(0, 80, 55),
		Color(19, 20, 41),
		Color(16, 31, 90), // row 13
		Color(106, 60, 24),
		Color(172, 4, 1),
		Color(225, 81, 54),
		Color(220, 105, 0),
		Color(254, 225, 0),
		Color(153, 225, 1),
		Color(906, 181, 0),
		Color(27, 28, 49), // row 14
		Color(220, 253, 84),
		Color(118, 251, 185),
		Color(150, 152, 255),
		Color(139, 98, 255),
		Color(64, 64, 64),
		Color(116, 116, 116),
		Color(222, 252, 252),
		Color(162, 4, 1), // row 15
		Color(52, 1, 0),
		Color(0, 210, 1),
		Color(0, 65, 1),
		Color(184, 177, 0),
		Color(60, 48, 0),
		Color(180, 93, 0),
		Color(76, 19, 0)
)

/**
 * Use the 3D nearest neighbor to determine an approximation for a given RGB color
 *
 * Each value is in 0-255
 *
 * @return the MIDI velocity corresponding to the RGB values
 */
fun nearestMidiColor(color: Color): Byte {
	var furthestIndex = 0
	var furthest = 3 * (255 * 255) + 1
	colorPalette.forEachIndexed { i, c ->
		if (c.red == color.red && c.green == color.green && c.blue == color.blue) {
			return i.toByte()
		}

		// sqr distance
		val distance = (color.red - c.red) * (color.red - c.red) + (color.green - c.green) * (color.green - c.green) + (color.blue - c.blue) * (color.blue - c.blue)

		if (distance < furthest) {
			furthest = distance
			furthestIndex = i
		}
	}
	return furthestIndex.toByte()
}