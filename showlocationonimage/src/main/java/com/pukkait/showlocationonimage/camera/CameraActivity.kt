package com.pukkait.showlocationonimage.camera

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.pukkait.showlocationonimage.R
import com.pukkait.showlocationonimage.geotag.FetchGeoLocation
import com.pukkait.showlocationonimage.helper.HelperClass
import com.pukkait.showlocationonimage.helper.HelperClass.getPreAuthorText
import com.pukkait.showlocationonimage.helper.ImageManager
import com.pukkait.showlocationonimage.helper.ImageManager.showAppIcon
import com.pukkait.showlocationonimage.helper.ImageManager.showAppName
import com.pukkait.showlocationonimage.helper.ImageManager.showDateTime
import com.pukkait.showlocationonimage.helper.ImageManager.showLatLong
import com.pukkait.showlocationonimage.imageConditions.InputTypeSelected
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor
import kotlin.math.min


class CameraActivity : ComponentActivity() {

    //    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var previewView: PreviewView
    private lateinit var overlayView: View
    private var isFrontCamera: Boolean = false
    private lateinit var cameraExecutor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_x)
// Initialize the camera executor
        cameraExecutor = ContextCompat.getMainExecutor(this)

        previewView = findViewById(R.id.preview_view)
        overlayView = findViewById(R.id.overlay_view)
        initializeData()
        val captureButton: ImageView = findViewById(R.id.capture_button)
        val frontButton: ImageView = findViewById(R.id.img_frontView)
        val cross: ImageView = findViewById(R.id.cross)

        startCamera()

        captureButton.setOnClickListener {
            captureImage()
        }
        cross.setOnClickListener {
            finish()
        }
        frontButton.setOnClickListener {
            isFrontCamera = !isFrontCamera
            startCamera()
        }

    }

    private fun initializeData() {
        val lnrHeader = findViewById<LinearLayout>(R.id.lnr_header)
        val txtHeader = findViewById<TextView>(R.id.txt_header)
        val imgHeader = findViewById<ImageView>(R.id.img_header)
        val txtAuthor = findViewById<TextView>(R.id.txt_author)
        val txtTime = findViewById<TextView>(R.id.txt_time)

        if (showAppIcon && showAppName) {
            lnrHeader.visibility = View.VISIBLE
        } else {
            lnrHeader.visibility = View.GONE
        }
        if (showAppIcon) {
            imgHeader.visibility = View.VISIBLE
            imgHeader.setImageResource(ImageManager.appIcon!!)
        } else {
            imgHeader.visibility = View.GONE
        }
        if (showAppName) {
            txtHeader.visibility = View.VISIBLE
            txtHeader.text = ImageManager.printAppName
        } else {
            txtHeader.visibility = View.GONE
        }
        if (ImageManager.showAuthor) {
            txtAuthor.text = String.format(
                "%s: %s",
                getPreAuthorText(
                    InputTypeSelected.CAMERA,
                    ImageManager.prefixToAuthorNameCameraChoice
                ),
                ImageManager.authorName
            )
            txtAuthor.visibility = View.VISIBLE
        } else {
            txtAuthor.visibility = View.GONE
        }


        if (showDateTime) {
            txtTime.visibility = View.VISIBLE
            txtTime.text = HelperClass.showCurrentDateTime()
        } else {
            txtTime.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val txtLatLong = findViewById<TextView>(R.id.txt_lat_long)
        val txtState = findViewById<TextView>(R.id.txt_state)
        val txtAddress = findViewById<TextView>(R.id.txt_address)
        try {
            if (showLatLong || ImageManager.showLocationAddress) {
                val fetchGeoLocation = FetchGeoLocation(this@CameraActivity)
                ImageManager.latitude = fetchGeoLocation.getLatitude()
                ImageManager.longitude = fetchGeoLocation.getLongitude()
                if (showLatLong) {
                    txtLatLong.visibility = View.VISIBLE
                    txtLatLong.text = String.format(
                        "Latitude: %.4f, Longitude: %.4f",
                        ImageManager.latitude,
                        ImageManager.longitude
                    )
                } else {
                    txtLatLong.visibility = View.GONE
                }

                if (ImageManager.showLocationAddress) {
                    val geoLocation = fetchGeoLocation.getGeocoderAddress(this@CameraActivity)
                    if (!geoLocation.isNullOrEmpty()) {
                        val address = geoLocation[0]
                        txtAddress.text = address.getAddressLine(0)
                        txtState.text = String.format(
                            "%s, %s, %s",
                            address.locality,
                            address.adminArea,
                            address.countryName
                        )
                    }
                    txtAddress.visibility = View.VISIBLE
                    txtState.visibility = View.VISIBLE
                } else {
                    txtAddress.visibility = View.GONE
                    txtState.visibility = View.GONE
                }
            }

        } catch (e: Exception) {
            Toast.makeText(this@CameraActivity, "Allow all the permissions.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun startCamera() {
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9) // Adjust based on your requirement
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                imageCapture = ImageCapture.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9) // Adjust based on your requirement
                    .build()

                val cameraSelector =
                    if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                } catch (e: Exception) {
                    Log.e("CameraX", "Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(this))
        } catch (_: Exception) {

        }
    }

    private fun captureImage() {
        try {
            val photoFile = File(getOutputDirectory(), "${System.currentTimeMillis()}.${ImageManager.imageExtensions}")

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.d("CameraX", "Photo capture succeeded: ${photoFile.absolutePath}")
                        addGraphicsToImage(photoFile.absolutePath)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                    }
                })
        } catch (_: Exception) {

        }
    }

    private fun addGraphicsToImage(imagePath: String) {
        // Load the captured image into a mutable bitmap
        val capturedBitmap = BitmapFactory.decodeFile(imagePath)
        val rotatedBitmap = rotateBitmapIfNeeded(capturedBitmap)

        val mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Create a canvas to draw on the mutable bitmap
        val canvas = Canvas(mutableBitmap)

        // Capture the overlay view as a bitmap and adjust it
        val overlayBitmap = getBitmapFromView(overlayView)

        // Calculate scaling factors based on the dimensions
//        val scalingFactor = calculateScalingFactor(previewView, mutableBitmap)

        // Scale the overlay bitmap to fit the final image
//        val scaledOverlayBitmap = Bitmap.createScaledBitmap(overlayBitmap,
//            (overlayBitmap.width * scalingFactor).toInt(),
//            (overlayBitmap.height * scalingFactor).toInt(), true)
        val scaledOverlayBitmap = Bitmap.createScaledBitmap(
            overlayBitmap,
            (mutableBitmap.width).toInt(),
            (mutableBitmap.height).toInt(), true
        )

        // Draw the overlay bitmap onto the captured image
        canvas.drawBitmap(scaledOverlayBitmap, 0f, 0f, null)

        // Save the final image with overlay
        FileOutputStream(imagePath).use { out ->
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        // Clean up
        capturedBitmap.recycle()
        rotatedBitmap.recycle()
        mutableBitmap.recycle()
        overlayBitmap.recycle()
        scaledOverlayBitmap.recycle()
//        startActivity(
//            Intent(this, PreviewImageActivity::class.java).putExtra(
//                "photoPath",
//                imagePath
//            )
//        )
        val resultIntent = Intent().apply {
//            cameraExecutor.execute {
            // Background work

            // Show a Toast on the main thread
            runOnUiThread {
                Toast.makeText(this@CameraActivity, "Image saved $imagePath", Toast.LENGTH_SHORT)
                    .show()
                data = Uri.parse(imagePath)
            }
//            }
//
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        // Create a bitmap with the same size as the view
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun calculateScalingFactor(previewView: PreviewView, bitmap: Bitmap): Float {
        // Calculate scaling factor based on view and image dimensions
        val viewWidth = previewView.width.toFloat()
        val viewHeight = previewView.height.toFloat()
        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()

        // Calculate the scaling factors for width and height
        val scaleX = bitmapWidth / viewWidth
        val scaleY = bitmapHeight / viewHeight

        // Use the smaller scaling factor to maintain aspect ratio
        return min(scaleX, scaleY)
    }

    private fun rotateBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        // Get the rotation of the device's camera
        val rotationDegrees = getRotationDegrees()

//        if (rotationDegrees != 0) {
        val matrix = Matrix()
        if (isFrontCamera) {
            matrix.preScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//        }
//        return bitmap
    }

    private fun getRotationDegrees(): Int {
        // Get the WindowManager instance
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Get the device's current rotation
        val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = display
            display?.rotation ?: Surface.ROTATION_0
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            display.rotation
        }

        // Return the corresponding rotation degrees
        return when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: filesDir
    }

}
