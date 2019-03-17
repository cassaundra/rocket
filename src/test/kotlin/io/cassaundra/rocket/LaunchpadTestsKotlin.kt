package io.cassaundra.rocket

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.assertFailsWith

class LaunchpadTestKotlin {
	private val launchpadClientMock = mock(LaunchpadClient::class.java)
	private val rocket = Rocket()

	@Test
	fun `setting pads should call client`() {
		// given
		val pads = setOf(Pad(3, 7), Pad(2, 4))

		rocket.client = launchpadClientMock

		// when
		rocket.setPads(pads, Color.WHITE)

		// then
		pads.forEach {
			verify(launchpadClientMock).sendPadColor(it, Color.WHITE)
		}
	}

	@Test(expected = IllegalArgumentException::class)
	fun `creating an invalid pad should throw exception`() {
		Pad(8, -1)
	}

	@Test
	fun `setting buttons should call client`() {
		// given
		val buttons = setOf(Button(3, true), Button(2, false))

		rocket.client = launchpadClientMock

		// when
		rocket.setButtons(buttons, Color.WHITE)

		// then
		buttons.forEach {
			verify(launchpadClientMock).sendButtonColor(it, Color.WHITE)
		}
	}

	@Test
	fun `creating an invalid button should throw exception`() {
		assertFailsWith<IllegalArgumentException> {
			Button(8, true)
		}
		assertFailsWith<IllegalArgumentException> {
			Button(-1, false)
		}
	}
}