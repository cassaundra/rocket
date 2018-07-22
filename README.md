[![Kotlin](https://img.shields.io/badge/kotlin-1.2.51-blue.svg)](http://kotlinlang.org)

<img src="Logo.png" width=192 height=192>

## Table of Contents
- [Setup](#setup)
- [Getting Started](#getting-started)
- [Colors](#colors)
- [Displaying Text](#displaying-text)
  - [Basics](#basics)
  - [Speed](#speed)
  - [Completion](#completion)
- [TODO](#todo)

## Setup

Add this to your pom.xml:

```xml
<dependency>
  <groupId>io.cassaundra</groupId>
  <artifactId>rocket</artifactId>
  <version>1.1.0</version>
</dependency>
```

Alternatively, with Gradle:

```
dependencies {
  compile 'io.cassaundra:rocket:1.1.0'
}
```

## Getting Started

```kotlin
// Allow MIDI scanning to begin
Rocket.connect()

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
// Allow MIDI scanning to begin
Rocket.INSTANCE.connect();

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

Now you're on your way to creating a full Launchpad app!

## Colors

You can use custom colors by specifiying RGB int values between 0 and 63 inclusive.

```kotlin
val color = Color(42, 0, 30)
```

In Java,

```java
Color color = new Color(42, 0, 30);
```

If you want to convert an HSV value to a Launchpad color, use `Color.fromHSV`, with each value a float in the range 0 to 1.

```kotlin
Color.fromHSV(.5f, 1f, 1f)
```

## Displaying Text

### Basics

You can call the Launchpad's built-in MIDI command for displaying text with `Launchpad.displayText`. Due to a Launchpad MIDI issue, changing the color from white does not yet work.

```kotlin
Rocket.launchpad.displayText(
    "Hello world!",
    Color.WHITE
)
```

In Java,

```java
Rocket.INSTANCE.getLaunchpad().displayText(
    "Hello world!",
    Color.WHITE
);
```

### Speed

You can control the text scrolling speed per-character with seven different available speeds in `TextSpeed`.

```kotlin
Rocket.launchpad.displayText(
    "Hello! ${TextSpeed.SPEED_1}Let's take this slower.",
    Color.WHITE
)
```

In Java,

```java
Rocket.INSTANCE.getLaunchpad().displayText(
    "Hello! " + TextSpeed.SPEED_1 + "Let's take this slower.",
    Color.WHITE,
);
```

### Completion

If you need to know when text has finished scrolling, you can use the onComplete argument.

```kotlin
Rocket.launchpad.displayText(
    "Hello world!",
    Color.WHITE,
    Runnable { println("Done!") }
)

```

In Java,

```java
Rocket.INSTANCE.getLaunchpad().displayText(
    "Hello world!",
    Color.WHITE,
    () -> System.out.println("Done!")
);
```

## Utils

`Pad.Util` provides several useful utility functions, like rectangles, line segments, and more.

## TODO
* Text only displays in white (MIDI Launchpad limitation)
* Add contributing documentation
