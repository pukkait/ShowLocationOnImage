📍 Show Location On Image for Android


Show a location marker on an image based on the geographic coordinates (latitude and longitude). The library allows you to add a location pin directly to any image by embedding geolocation information as a marker.

Perfect for apps that need to visualize geographic data, such as photo-sharing apps, travel apps, or social media platforms with location-based content.

🛠 Features
Display a Location Marker: Display a location marker based on geographic coordinates (latitude and longitude).
Customizable Marker: You can customize the location marker icon and style.
Supports Geolocation Data: Works with EXIF data or manually provided latitude and longitude.
Supports Image Scaling: The marker position adjusts even when the image is zoomed or resized.
Easy Integration: Simple setup and minimal configuration required.
🎨 Preview


💻 Usage
Installation

Add the following dependency to your build.gradle file:

groovy
Copy code
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
Then, add the library to your app's dependencies:

groovy
Copy code
implementation 'com.github.pukkait:ShowLocationOnImage:1.0'
Basic Usage

To display the location marker on an image, simply call the ShowLocationOnImage utility class:

Kotlin:

kotlin
Copy code
ShowLocationOnImage.with(this)
    .imageUri(imageUri) // Image URI to display
    .latitude(28.7041)   // Latitude of the location
    .longitude(77.1025)  // Longitude of the location
    .markerIcon(R.drawable.ic_location_pin) // Optional: Marker icon
    .addMarker()         // Add the location marker to the image
    .into(imageView)     // Set the image with location marker into an ImageView
Java:

java
Copy code
ShowLocationOnImage.with(this)
    .imageUri(imageUri) // Image URI to display
    .latitude(28.7041)   // Latitude of the location
    .longitude(77.1025)  // Longitude of the location
    .markerIcon(R.drawable.ic_location_pin) // Optional: Marker icon
    .addMarker()         // Add the location marker to the image
    .into(imageView);    // Set the image with location marker into an ImageView
Handling Image with EXIF Geolocation

If the image already contains geolocation data (EXIF), you can directly use it without manually providing latitude and longitude.

kotlin
Copy code
ShowLocationOnImage.with(this)
    .imageUri(imageUri) // Image URI containing EXIF geolocation data
    .addMarker()         // Automatically use EXIF data to display location marker
    .into(imageView)     // Set the image with location marker into an ImageView
Customizing Marker

You can customize the appearance of the marker, such as the color and icon:

kotlin
Copy code
ShowLocationOnImage.with(this)
    .imageUri(imageUri)
    .latitude(28.7041)
    .longitude(77.1025)
    .markerIcon(R.drawable.ic_custom_marker)  // Custom marker
    .markerSize(50)                          // Marker size (optional)
    .addMarker()
    .into(imageView)
🔧 Customization
You can customize the following options:

Marker Icon: Use your own icon for the location marker.

kotlin
Copy code
.markerIcon(R.drawable.custom_location_marker)
Marker Size: You can specify the size of the marker to better fit your image.

kotlin
Copy code
.markerSize(40) // Change size to fit your image
Marker Position: The default marker is placed at the exact location specified by latitude and longitude. You can adjust the position if necessary (relative to the image size).

Zoom Support: The marker will automatically adjust to zoom levels when interacting with the image (if you’re using zoomable views).

📦 Additional Features
EXIF Data Support: Automatically fetches geolocation from EXIF data embedded in images.
Support for Multiple Locations: Display multiple markers on a single image by calling the addMarker() method multiple times.
🏗 Compatibility
Android 5.0 (Lollipop) and above (API 21+)
Supports Kotlin and Java development
📑 Changelog
Version 1.0
Initial release with basic functionality to display location markers on images based on geolocation (latitude and longitude).
Added support for custom marker icons and adjustable marker size.
🔗 Libraries Used
Glide (for image loading and caching): https://github.com/bumptech/glide
EXIFInterface: Used for reading EXIF data from images.
🎨 License
This project is licensed under the Apache License 2.0. See the LICENSE file for more details.
