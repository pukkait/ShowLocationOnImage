package com.pukkait.showlocationonimage.gallery

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pukkait.showlocationonimage.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CropActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var cropArea: View
    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        imageView = findViewById(R.id.image_view)
        cropArea = findViewById(R.id.crop_area)
        val cropButton = findViewById<Button>(R.id.btn_crop)

        imageUri = intent.getParcelableExtra("imageUri")

        try {
            val inputStream = contentResolver.openInputStream(imageUri!!)
            bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        cropButton.setOnClickListener { v: View? -> cropImage() }

        // Make the crop area draggable and resizable
        cropArea.setOnTouchListener(CropAreaTouchListener())
    }

    private fun cropImage() {
        // Get the dimensions of the crop area
        val left = cropArea.left
        val top = cropArea.top
        val right = cropArea.right
        val bottom = cropArea.bottom

        // Create a bitmap with the cropped area
        val croppedBitmap = Bitmap.createBitmap(
            bitmap!!,
            left, top, right - left, bottom - top
        )

        // Save the cropped bitmap
        val croppedImageUri = saveCroppedImage(croppedBitmap)

        val resultIntent = Intent()
        resultIntent.setData(croppedImageUri)
        Toast.makeText(this,"$croppedImageUri",Toast.LENGTH_LONG).show()
        setResult(RESULT_OK, resultIntent)
        finish()
    }


    private fun saveCroppedImage(croppedBitmap: Bitmap): Uri {
        // Save the cropped bitmap to file and return its Uri
        val file = File(externalCacheDir, "cropped_image.jpg")
        try {
            FileOutputStream(file).use { out ->
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.fromFile(file)
    }

    private inner class CropAreaTouchListener : OnTouchListener {
        private var initialX = 0f
        private var initialY = 0f
        private var initialLeft = 0f
        private var initialTop = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = event.rawX
                    initialY = event.rawY
                    initialLeft = v.x
                    initialTop = v.y
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - initialX
                    val deltaY = event.rawY - initialY
                    v.x = initialLeft + deltaX
                    v.y = initialTop + deltaY
                }
            }
            return true
        }
    }
}

