package io.cassaundra.rocket

import io.cassaundra.rocket.midi.MidiDeviceConfiguration
import io.cassaundra.rocket.midi.MidiLaunchpad
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.sound.midi.MidiUnavailableException

/**
 * Manages [Launchpad]s and MIDI scanning.
 */
object Rocket {
	/**
	 * The [Launchpad].
	 *
	 * If a MIDI Launchpad has not yet been detected (or if one was lost), this value is still safe to use.
	 */
	var launchpad: Launchpad = Launchpad()
		private set

	private val scanRateSeconds = 3

	private val logger = LoggerFactory.getLogger(Rocket::class.java)

	init {
		setupLaunchpad()
		setupShutdownHook()
	}

	private fun setupLaunchpad() {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			logger.warn("MIDI does not function properly on Mac OS X. [JDK-8139153]")
		}

		scan() // TODO repeatedly scan
//        val executor = Executors.newScheduledThreadPool(1)
//        executor.scheduleAtFixedRate({ scan() }, 0, midiScanRateSeconds.toLong(), TimeUnit.SECONDS)
	}

	private fun setupShutdownHook() {
		Runtime.getRuntime().addShutdownHook(Thread {
			close()
		})
	}

	private fun scan() {
		val config = MidiDeviceConfiguration.autodetect()

		if (config.inputDevice == null || config.outputDevice == null) {
			launchpad.setLaunchpadClient(null)
		} else {
			if (!launchpad.hasClient()) {
				try {
					launchpad.setLaunchpadClient(MidiLaunchpad(config))
				} catch (exc: MidiUnavailableException) {
					logger.error("Could not setup MIDI launchpad", exc)
				}
			}
		}
	}

	private fun close() {
		launchpad.close()
	}
}