[![Kotlin](https://img.shields.io/badge/kotlin-1.2.51-blue.svg)](http://kotlinlang.org)

<img src="Logo.png" width=192 height=192>

## Setup

```xml
<dependency>
  <groupId>io.cassaundra</groupId>
  <artifactId>rocket</artifactId>
  <version>1.0.2</version>
</dependency>
```

## Examples

View the [complete example project](https://github.com/actuallycass/rocket-example).

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
        if(button.isTop)
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
        if(button.isTop())
            lp.setButton(button, Color.RED);
        else
            lp.setButton(button, Color.BLUE);
    }

    public void onButtonUp(@NotNull Button button) {
        lp.setButton(button, Color.OFF);
    }
});
```

## TODO
* Automatically try to reconnect to Launchpad after disconnect
* Give more control of MIDI scanning to the user
* Text only displays in white (MIDI limitation)

## Known Issues
* Displaying Launchpad text fails on macOS (external problem)
