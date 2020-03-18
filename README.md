# DPad View [![](https://jitpack.io/v/shripal17/DPadView.svg)](https://jitpack.io/#shripal17/DPadView) ![](https://img.shields.io/badge/SDK-14+-blueviolet)
A simple, but highly customisable DPadView for Android.

100% Kotlin and Kotlin-first approach.

## Screenshots
| With CenterText | With Center Icon | Without Center Text/Icon |
| ---- | ---- | ---- |
| ![With Center Text](/art/CenterText.jpg?raw=true) | ![With Center Icon](/art/CenterIcon.jpg?raw=true) | ![Without Center Text/Icon](/art/NoTextIcon.jpg?raw=true) |

## Full Demo
![Whole Video](/art/DPadView.gif?raw=true)

## Import
##### Through JitPack
```groovy
buildscript {
  //...
}
allProjects {
  repositories {
    //...
    maven { url "https://jitpack.io" }
  }
}
```
2. Add to module-level build.gradle
```groovy
dependencies {
  //...
  implementation 'com.github.shripal17:DPadView:1.0.1'
}
```


## Usage
###### In layout XML
```xml
  <com.codertainment.dpadview.DPadView
    android:id="@+id/dpad"
    android:layout_width="match_parent"
    android:layout_height="256dp"
    android:src="@drawable/ic_gamepad_black" />
```

**Note:** You must provide either fixed height or fixed width, providing both as `wrap_content` will follow `ImageView`'s view behavior and may produce unexpected results

#### OR
###### At runtime

```kotlin
val dpadView = DPadView(this).apply {
  // set attributes here
}

yourViewGroup.addView(dpadView)
```

## Important
`DPadView` extends `androidx.appcompat.widget.AppCompatImageView`, so all attributes for it are also applicable to `DPadView` for the image

## Attributes
All the mentioned attributes are applicable for both Kotlin/Java and XML

| Name | Description | Type |Default Value |
|----|----|----|----|
| `imagePadding` | Padding for the drawable to be displayed passed as `src` | Dimension (px at runtime) | `24dp` |
| `normalColor` | Color of the direction section when it is not pressed | Color Int | Current theme's `textColorSecondary` |
| `pressedColor` | Color of the direction section when it is pressed | Color Int | Current theme's `colorAccent` |
| `centerCircleEnabled` | Whether to enable the center circle | Boolean | `true` |
| `centerCirclePressEnabled` | If `centerCircleEnabled` is true, whether it should detect touch on the center circle | Boolean | `true` |
| `centerCircleNormalColor` | Color of the center circle when it is not pressed | Color Int | Current theme's `colorPrimary` |
| `centerCirclePressedColor` | Color of the center circle when it is pressed | Color Int | Current theme's `colorPrimaryDark` |
| `centerCircleRatio` | The size of the center circle will be the size of DPadView divided by this value | Float | `3.5f` |
| `centerText` | Text to be displayed in the center (e.g. OK) | String | Empty |
| `centerTextSize` | Text size for the center text | Dimension (px at runtime) | `14f` |
| `centerTextColor` | Text color for the center text | Color Int| Automatically picked as Black/White depending on `centerCircleNormalColor` |
| `centerTextStyle` | Text style for the center text | For XML: Combination of any of `bold`, `italic`, `underline` e.g. `bold\|italic\|underline` <br> At runtime: Combination of any of `TextStyle.BOLD`, `TextStyle.ITALIC`, `TextStyle.UNDERLINE` e.g. `TextStyle.BOLD and TextStyle.UNDERLINE and TextStyle.ITALIC` | Normal |
| `centerIcon` | The drawable to be displayed in the center | Drawable/Mipmap Resource Reference| NA |
|`centerIconSizeMode` | Size mode for the icon, whether the size should be fixed or it should wrap the image src | `wrap` or `fixed` for XML, `IconSizeMode.WRAP` or `IconSizeMode.FIXED` for runtime | Wrap |
| `centerIconSize` | Icon size to be used if `centerIconSizeMode` is set to fixed| Dimension (px at runtime) | `24dp` |
| `centerIconTint` | The color to tint the center icon with | Color Int | NA |
|`hapticFeedbackEnabled` | Device will vibrate when user touches any direction | Boolean | False |

### Updating attributes at runtime
1. Using `DPadView.modify`:
```kotlin
dpadView.modify {
  normalColor = Color.GREEN
  // and more
}
```
2. Using `apply`:

```kotlin
 dpadView.apply {
  normalColor = Color.GREEN
  // and more
  
  // CALL THIS AFTER YOU ARE DONE
  reInit()
}
```

## Listening to presses
`DPadView` provides a listener similar to `onTouchListener`, via `onDirectionPressedListener`

It has 2 parameters:
1. `direction` (`Direction.UP`/`Direction.DOWN`/`Direction.LEFT`/`Direction.RIGHT`/`Direction.CENTER`)
2. `action` (`MotionEvent.ACTION_UP`, `MotionEvent.ACTION_DOWN` or `MotionEvent.ACTION_MOVE`
```kotlin
dpadView.onDirectionPressListener = { direction, action ->
  // use direction and action
}
```

## Authors
[Shripal Jain (Me)](https://github.com/shripal17)


## Showcase
Apps using this library

Create a new issue to add your app here
- [Portal Controller](https://play.google.com/store/apps/details?id=com.portalcomputainment.android.controller.client)

## License
--------
    Copyright 2020 Shripal Jain

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
