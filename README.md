[![Kotlin](https://img.shields.io/badge/kotlin-1.2.51-blue.svg)](http://kotlinlang.org)

<img src="Logo.png" width=192 height=192>

## Setup

// TODO deploy Maven project...

## Examples

```kotlin
// Obtain the Launchpad MK2 instance
val lp = Rocket.launchpad

// Listen for input events
lp.addListener(object : LaunchpadListener {
    override fun onPadDown(pad: Pad) {
        lp.setPad(pad, Color.WHITE)
    }

    override fun onPadUp(pad: Pad) {
        lp.setPad(pad, Color.OFF)
    }

    override fun onButtonDown(button: Button) {
        if(button is Button.Top)
            lp.setButton(button, Color.RED)
        else
            lp.setButton(button, Color.BLUE)
    }

    override fun onButtonUp(button: Button) {
        lp.setButton(button, Color.OFF)
    }
})
```

Similarly, in Java...

```java
// Obtain the Launchpad MK2 instance
final Launchpad lp = Rocket.INSTANCE.getLaunchpad();

// Listen for input events
lp.addListener(new LaunchpadListener() {
    public void onPadDown(@NotNull Pad pad) {
        lp.setPad(pad, Color.WHITE);
    }

    public void onPadUp(@NotNull Pad pad) {
        lp.setPad(pad, Color.OFF);
    }

    public void onButtonDown(@NotNull Button button) {
        if(button instanceof Button.Top)
            lp.setButton(button, Color.RED);
        else
            lp.setButton(button, Color.BLUE);
    }

    public void onButtonUp(@NotNull Button button) {
        lp.setButton(button, Color.OFF);
    }
});
```

## Known Issues
* Displaying Launchpad text fails on macOS (external problem)
