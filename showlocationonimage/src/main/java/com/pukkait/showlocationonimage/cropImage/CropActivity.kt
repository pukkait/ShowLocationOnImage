package com.pukkait.showlocationonimage.cropImage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.pukkait.showlocationonimage.R
import com.pukkait.showlocationonimage.geotag.FetchGeoLocation
import com.pukkait.showlocationonimage.helper.HelperClass
import com.pukkait.showlocationonimage.helper.HelperClass.getFileFromUri
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.imagePath
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.imageUri
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.latitude
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.longitude
import java.io.File
import java.io.FileNotFoundException

class CropActivity : AppCompatActivity() {
    private lateinit var imageView: CropImageView
    private lateinit var textDone: TextView
    private lateinit var imgRotate: ImageView
    private lateinit var croppedImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        imageView = findViewById(R.id.crop_image_view)
        imgRotate = findViewById(R.id.img_rotate)
        textDone = findViewById(R.id.text_done)
        croppedImage = findViewById(R.id.croppedImage)
        findViewById<TextView>(R.id.txt_back).setOnClickListener { finish() }
//        val imgUriString = intent.getStringExtra("imageUri")
//        Log.d("aditi:", imgUriString + "")
//        if (imgUriString != null) {
//            val imgUri = Uri.parse(imgUriString)
//
//            if (imgUri.scheme == "file") {
//                // Handle file URI
//                val file = File(imgUri.path ?: "")
//                imageView.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
//            } else {
//                // Handle content URI
//                imageView.setImage(imgUri)
//            }
//        } else {
//            Toast.makeText(this, "Image URI not found", Toast.LENGTH_SHORT).show()
//        }
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(galleryIntent)

        textDone.setOnClickListener {
            if (textDone.text.toString() == "Crop") {
                val x = imageView.crop()
                croppedImage.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                croppedImage.setImageBitmap(x)
                textDone.text = "Done"
            } else {
                HelperClass.setDataOnImage(this@CropActivity, imageUri)
                val resultIntent = Intent().apply {
                    runOnUiThread {
                        Toast.makeText(
                            this@CropActivity,
                            "Image saved $imagePath",
                            Toast.LENGTH_SHORT
                        ).show()
                        data = Uri.parse(imagePath)
                    }
                }

                setResult(RESULT_OK, resultIntent)
            }

        }
    }

    //
//    private var activityResultLauncher =
//        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                imageUri = result.data?.data
//                    ?: result.data?.extras?.let {
//                        if (it.containsKey("data")) Uri.parse(
//                            it.getString(
//                                "data"
//                            )
//                        ) else null
//                    }
//                if (imageUri != null) {
//                    imagePath = imageUri!!.path!!
//                    if (imageUri != null) {
//                        val fetchGeoLocation = FetchGeoLocation(this@CropActivity)
//                        latitude = fetchGeoLocation.getLatitude()
//                        longitude = fetchGeoLocation.getLongitude()
//                        Log.d("aditi:", imagePath + "")
//                        Log.d("aditi:", imageUri!!.path + "")
////                        if (imageUri!!.scheme == "file") {
////                            // Handle file URI
////                            val file = File(imageUri!!.path ?: "")
////                            imageView.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
////                        } else {
//                            // Handle content URI
//                            imageView.setImage(imageUri!!)
////                        }
//                        //                        processImage(imageUri!!, this)
////                    createImageWithText(File(imagePath), printAppName)
////                        launchCropActivity(imagePathSel, this@CropActivity)
//
////                        resultListener?.onImageProcessed(imagePath, actionCode)
//                    } else {
////                        resultListener?.onError("Failed to process image.", actionCode)
//                    }
//                } else {
////                    resultListener?.onError("Failed to obtain image.", actionCode)
//                }
//            } else {
////                resultListener?.onError("Image selection failed.", actionCode)
//            }
//        }
    private var activityResultLauncher =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val dataIntent = result.data
                // Extract the Uri from Intent data
                imageUri = dataIntent?.data
                    ?: dataIntent?.extras?.let {
                        // If no Uri in the data, check for extras (though it's rare)
                        if (it.containsKey("data")) Uri.parse(it.getString("data")) else null
                    }
//                processImage(imageUri!!,  this@CropActivity)

                if (imageUri != null) {
                    Log.d("aditi imageUri", imageUri.toString())
                    Toast.makeText(this, "WOW Image URI  found", Toast.LENGTH_SHORT).show()
                    // Do something with the URI, like display in ImageView
                    imageView.setImage(
                        imageUri!!
                    )
                } else {
                    Toast.makeText(this, "Image URI not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

}