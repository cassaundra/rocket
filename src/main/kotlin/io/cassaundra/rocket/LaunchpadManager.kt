package io.cassaundra.rocket

import org.slf4j.LoggerFactory
import javax.sound.midi.MidiUnavailableException

object LaunchpadManager {
    var launchpad: Launchpad = Launchpad(NullLaunchpadClient())

    private val scanRateSeconds = 3

    private val logger = LoggerFactory.getLogger(LaunchpadManager::class.java)

    init {
        setupLaunchpad()
        setupShutdownHook()
    }

    private fun setupLaunchpad() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            logger.warn("MIDI does not function properly on Mac OS X. [JDK-8139153]")
        }

        launchpad = Launchpad(NullLaunchpadClient())

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
            launchpad.setLaunchpadClient(NullLaunchpadClient())
        } else {
            if (launchpad.client is NullLaunchpadClient) {
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