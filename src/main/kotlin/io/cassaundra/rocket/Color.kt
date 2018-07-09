package io.cassaundra.rocket

data class Color private constructor(val midiVelocity: Int, val darkerMidiVelocity: Int = midiVelocity, val channel: Int = 0) {
    fun getSolid(): Color =
            Color(midiVelocity, darkerMidiVelocity, 0)

    fun getFlashing(): Color =
            Color(midiVelocity, darkerMidiVelocity, 1)

    fun getPulsing(): Color =
            Color(midiVelocity, darkerMidiVelocity, 2)

    companion object {
        @JvmField val OFF = Color(0)
        @JvmField val WHITE = Color(3, 1)
        @JvmField val PINK = Color(57, 59)
        @JvmField val RED = Color(5, 7)
        @JvmField val ORANGE = Color(9, 11)
        @JvmField val YELLOW = Color(13, 15)
        @JvmField val GREEN = Color(122, 123)
        @JvmField val TURQOISE = Color(33, 35)
        @JvmField val BLUE = Color(45, 47)
    }
}