package io.cassaundra.rocket.midi

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException

internal class MidiDeviceConfiguration(val inputDevice: MidiDevice?, val outputDevice: MidiDevice?) {
	companion object {
		const val DEVICE_SIGNATURE = "Launchpad MK2"

		@Throws(MidiUnavailableException::class)
		fun autodetect(): MidiDeviceConfiguration {
			var inputDevice: MidiDevice? = null
			var outputDevice: MidiDevice? = null

			val midiDeviceInfo = MidiSystem.getMidiDeviceInfo()
			for(info in midiDeviceInfo) {
				if(info.description.contains(DEVICE_SIGNATURE) || info.name.contains(DEVICE_SIGNATURE)) {
					val device = MidiSystem.getMidiDevice(info)
					when {
						device.maxTransmitters == -1 && inputDevice == null -> inputDevice = device
						device.maxReceivers == -1 && outputDevice == null -> outputDevice = device
						else -> device.close()
					}
				}
			}

			return MidiDeviceConfiguration(inputDevice, outputDevice)
		}
	}

}
