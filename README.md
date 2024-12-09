Readme
======


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>ShowLocationOnImage - Android Kotlin</title>
</head>
<body>

    <h1>ShowLocationOnImage (Android Kotlin)</h1>
    <p>A Kotlin-based Android library to overlay geographical coordinates (latitude and longitude) on an image. This library can be used to show location markers on photos directly within your Android app. Perfect for applications such as photo geotagging, travel apps, and location-based image processing.</p>

    <h2>Table of Contents</h2>
    <ul>
        <li><a href="#description">Description</a></li>
        <li><a href="#features">Features</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#usage">Usage</a></li>
        <li><a href="#examples">Examples</a></li>
        <li><a href="#contributing">Contributing</a></li>
        <li><a href="#license">License</a></li>
        <li><a href="#contact">Contact</a></li>
    </ul>

    <h2 id="description">Description</h2>
    <p><code>ShowLocationOnImage</code> is an Android Kotlin library that allows you to add latitude and longitude information as a text overlay on images. You can specify the position, text size, and color of the location text, and the library will handle drawing the location information on the image for display in your Android app.</p>

    <h2 id="features">Features</h2>
    <ul>
        <li>Display latitude and longitude on an image.</li>
        <li>Customizable text size, font, and color.</li>
        <li>Flexible positioning for the location text (top-left, bottom-right, etc.).</li>
        <li>Simple integration with Android's <code>Bitmap</code> and <code>Canvas</code> for image manipulation.</li>
    </ul>

    <h2 id="installation">Installation</h2>
    <h3>Gradle Dependency</h3>
    <p>To include <code>ShowLocationOnImage</code> in your Android project, add the following dependency to your <code>build.gradle</code> file:</p>
    <pre><code>dependencies {
    implementation 'com.github.pukkait:ShowLocationOnImage:1.0.0'
}</code></pre>

    <h3>JitPack Repository</h3>
    <p>Make sure you have the JitPack repository added in your <code>settings.gradle</code> or <code>build.gradle</code>:</p>
    <pre><code>repositories {
    maven { url 'https://jitpack.io' }
}</code></pre>

    <h3>Manual Installation (Optional)</h3>
    <ol>
        <li>Clone the repository:
            <pre><code>git clone https://github.com/pukkait/ShowLocationOnImage.git</code></pre>
        </li>
        <li>Import the relevant classes into your Android project.</li>
    </ol>

    <h2 id="usage">Usage</h2>
    <h3>Basic Example</h3>
    <p>Here’s a simple example of how to overlay latitude and longitude on an image in an Android app:</p>
    <pre><code>import com.pukkait.showlocationonimage.ShowLocationOnImage
import android.graphics.Bitmap
import android.graphics.BitmapFactory

// Example usage
fun addLocationToImage(imagePath: String, latitude: Double, longitude: Double) {
    // Load the image from the resources or file system
    val bitmap: Bitmap = BitmapFactory.decodeFile(imagePath)

    // Create an instance of ShowLocationOnImage
    val showLocation = ShowLocationOnImage(bitmap)

    // Add the location overlay
    val updatedBitmap = showLocation.addLocationOverlay(latitude, longitude)

    // Set the updated bitmap to an ImageView
    imageView.setImageBitmap(updatedBitmap)
}</code></pre>

    <h3>Customizing the Overlay</h3>
    <p>You can customize the appearance of the location text by adjusting the font size, color, and position:</p>
    <pre><code>val updatedBitmap = showLocation.addLocationOverlay(
    latitude = 40.7128,
    longitude = -74.0060,
    textSize = 24f,             // Customize the text size
    textColor = Color.RED,      // Customize the text color
    position = Position.BOTTOM_RIGHT // Customize the position
)</code></pre>
    <p>The available position options are:</p>
    <ul>
        <li><code>Position.TOP_LEFT</code></li>
        <li><code>Position.TOP_RIGHT</code></li>
        <li><code>Position.BOTTOM_LEFT</code></li>
        <li><code>Position.BOTTOM_RIGHT</code></li>
    </ul>

    <h3>Example Usage in an Activity</h3>
    <pre><code>class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example image path
        val imagePath = "path_to_your_image.jpg"

        // Latitude and Longitude for the location overlay
        val latitude = 40.7128
        val longitude = -74.0060

        // Add location overlay to the image
        val updatedBitmap = addLocationToImage(imagePath, latitude, longitude)

        // Set the updated image to the ImageView
        findViewById<ImageView>(R.id.imageView).setImageBitmap(updatedBitmap)
    }

    fun addLocationToImage(imagePath: String, latitude: Double, longitude: Double): Bitmap {
        val bitmap: Bitmap = BitmapFactory.decodeFile(imagePath)
        val showLocation = ShowLocationOnImage(bitmap)
        return showLocation.addLocationOverlay(latitude, longitude)
    }
}</code></pre>

    <h2 id="examples">Examples</h2>
    <p>Here are some example images with location overlays:</p>
    <ul>
        <li><a href="url-to-image1">Image 1</a></li>
        <li><a href="url-to-image2">Image 2</a></li>
    </ul>

    <h2 id="contributing">Contributing</h2>
    <p>We welcome contributions! If you'd like to improve the library or add new features, please follow these steps:</p>
    <ol>
        <li>Fork the repository.</li>
        <li>Create a new branch (<code>git checkout -b feature-name</code>).</li>
        <li>Commit your changes (<code>git commit -am 'Add new feature'</code>).</li>
        <li>Push to the branch (<code>git push origin feature-name</code>).</li>
        <li>Open a Pull Request.</li>
    </ol>
    <p>Please ensure that your contributions follow the coding style and include appropriate tests.</p>

    <h2 id="license">License</h2>
    <p>This project is licensed under the MIT License - see the <a href="LICENSE">LICENSE</a> file for details.</p>

    <h2 id="contact">Contact</h2>
    <p>For questions, issues, or suggestions, feel free to open an issue or contact the author at:</p>
    <ul>
        <li>GitHub: <a href="https://github.com/pukkait">@pukkait</a></li>
        <li>Email: puka.it4u@gmail.com</li>
    </ul>

</body>
</html>
