# ShowLocationOnImage - Android Library

[![Download](https://img.shields.io/github/release/pukkait/ShowLocationOnImage.svg?style=flat)](https://github.com/pukkait/ShowLocationOnImage/releases)
[![Build Status](https://github.com/pukkait/ShowLocationOnImage/actions/workflows/build.yml/badge.svg)](https://github.com/pukkait/ShowLocationOnImage/actions)
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
![Language](https://img.shields.io/badge/language-Kotlin-orange.svg)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Overview

**ShowLocationOnImage** is a lightweight Android library that enables you to easily add geolocation (latitude and longitude) metadata onto an image. This feature can be useful in apps that involve location-based images, such as photography or geotagging applications.

With this library, you can retrieve the location from an image's EXIF data and display it in your app, or even overlay it directly onto the image itself. 

## Features

- Extract the location from the EXIF data of an image (JPEG format).
- Display the location on the image as a text label.
- Support for both image files from gallery and images captured by camera.
- Easily customizable UI for displaying the location on the image.

## Preview

![ShowLocationOnImage Preview](https://github.com/pukkait/ShowLocationOnImage/blob/main/preview.png?raw=true)

## Installation

### Step 1: Add JitPack Repository

In your root `build.gradle` file, add the following JitPack repository:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the Dependency

In your app-level `build.gradle` file, add the following dependency:

```groovy
dependencies {
    implementation 'com.github.pukkait:ShowLocationOnImage:v1.0'
}
```

If you're using Kotlin and prefer the AndroidX-based libraries, the dependency will work out-of-the-box.

## Usage

### Basic Example

Here’s a simple example on how to use this library to display location data on an image.

```kotlin
import com.pukkait.showlocationonimage.ShowLocationOnImage

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sample image Uri (You may use your own image file URI)
        val imageUri: Uri = Uri.parse("android.resource://com.example.app/drawable/sample_image")

        // Show location on image
        ShowLocationOnImage.with(this)
            .setImageUri(imageUri)                // Set the image URI
            .setShowLocation(true)                 // Whether to show location
            .setLocationTextColor(Color.RED)       // Customize text color (Optional)
            .setFontSize(20f)                      // Customize font size (Optional)
            .setLocationText("Latitude: 40.7128° N, Longitude: 74.0060° W") // Custom location (Optional)
            .start()                               // Start displaying the location
    }
}
```

### Customization

You can easily customize the location label by modifying its position, font size, and color using the available methods.

```kotlin
ShowLocationOnImage.with(this)
    .setImageUri(imageUri)            // Set image URI
    .setShowLocation(true)             // Show location label on the image
    .setLocationText("Your Custom Text")  // Custom location text
    .setLocationTextColor(Color.BLUE)   // Custom text color
    .setFontSize(18f)                  // Custom font size
    .start()                           // Start processing
```

### Handling Permission

Ensure you have the necessary permissions in your `AndroidManifest.xml` to read images from the gallery or capture images using the camera.

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

Additionally, ensure that your app is requesting runtime permissions for external storage access if targeting Android 6.0 (API 23) or higher.

## License

```
Apache License 2.0
```

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

Feel free to adjust the text and sections as per your repository needs. You can add or remove sections based on your project's specifics, such as troubleshooting, contribution guidelines, etc.
