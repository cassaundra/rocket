package io.cassaundra.rocket.midi

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException

internal class MidiDeviceConfiguration(val inputDevice: MidiDevice?, val outputDevice: MidiDevice?) {

	companion object {
		const val DEVICE_SIGNATURE = "Launchpad MK2"

		@Throws(MidiUnavailableException::class)
		fun autodetect(): MidiDeviceConfiguration {
			val inputDevice = autodetectInputDevice()
			val outputDevice = autodetectOutputDevice()
			return MidiDeviceConfiguration(inputDevice, outputDevice)
		}

		@Throws(MidiUnavailableException::class)
		private fun autodetectOutputDevice(): MidiDevice? {
			val midiDeviceInfo = MidiSystem.getMidiDeviceInfo()
			for (info in midiDeviceInfo) {
				if (info.description.contains(DEVICE_SIGNATURE) || info.name.contains(DEVICE_SIGNATURE)) {
					val device = MidiSystem.getMidiDevice(info)
					if (device.maxReceivers == -1) {
						return device
					}
				}
			}
			return null
		}

		@Throws(MidiUnavailableException::class)
		private fun autodetectInputDevice(): MidiDevice? {
			val midiDeviceInfo = MidiSystem.getMidiDeviceInfo()
			for (info in midiDeviceInfo) {
				if (info.description.contains(DEVICE_SIGNATURE)) {
					val device = MidiSystem.getMidiDevice(info)
					if (device.maxTransmitters == -1) {
						return device
					}
					device.close()
				}
			}
			return null
		}
	}

}
