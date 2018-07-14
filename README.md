<img src="https://github.com/actuallycass/rocket/raw/master/Logo.png" width=128 height=128 >

Rocket is a Novation Launchpad MK2 client written in Kotlin and Java.

## Setup

// TODO deploy Maven project...

## Examples

```kotlin
// Obtain the Launchpad MK2 instance
val lp = Rocket.launchpad

// Listen for input events
lp.listener = object : LaunchpadListener {
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
}
```

Similarly, in Java...

```java
// Obtain the Launchpad MK2 instance
final Launchpad lp = Rocket.INSTANCE.getLaunchpad();

// Listen for input events
lp.setListener(new LaunchpadListener() {
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
