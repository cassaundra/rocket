package io.cassaundra.rocket

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class RocketTest {
	private val launchpadClientMock = mock(LaunchpadClient::class.java)

	@Test
	fun `set pads`() {
		// given
		val pads = setOf(Pad(3, 7), Pad(2, 4))

		Rocket.client = launchpadClientMock

		// when
		Rocket.setPads(pads, Color.WHITE)

		// then
		pads.forEach {
			verify(launchpadClientMock).sendPadColor(it, Color.WHITE)
		}
	}

	@Test(expected = IllegalArgumentException::class)
	fun `set invalid pad`() {
		Pad(8, -1)
	}

	@Test
	fun `set buttons`() {
		// given
		val buttons = setOf(Button(3, true), Button(2, false))

		Rocket.client = launchpadClientMock

		// when
		Rocket.setButtons(buttons, Color.WHITE)

		// then
		buttons.forEach {
			verify(launchpadClientMock).sendButtonColor(it, Color.WHITE)
		}
	}

	@Test(expected = IllegalArgumentException::class)
	fun `set invalid button`() {
		Button(8, true)
	}
}