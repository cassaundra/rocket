package io.cassaundra.rocket

interface LaunchpadScanner {
	fun beginScan(onSuccess: Runnable)
	fun stopScan()
}