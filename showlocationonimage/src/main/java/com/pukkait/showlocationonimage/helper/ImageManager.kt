package com.pukkait.showlocationonimage.helper
/*
 * MIT License
 *
 * Copyright (c) 2024 Pukka-it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import android.app.AlertDialog
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.pukkait.showlocationonimage.camera.CameraActivity
import com.pukkait.showlocationonimage.gallery.GalleryWrite
import com.pukkait.showlocationonimage.geotag.FetchGeoLocation
import com.pukkait.showlocationonimage.imageConditions.ImageExtensions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageManager {
    internal var textSize = 0f
    internal var printAppName = ""
    internal var authorName = ""
    internal var prefixToAuthorNameCameraChoice = ""
    internal var prefixToAuthorNameGalleryChoice = ""
    internal var imagePath = ""

    internal var showAppIcon = false
    internal var showAppName = false
    internal var showLocationAddress = false
    internal var showLatLong = false
    internal var showDateTime = false
    internal var showAuthor = true
    internal var showDataToBottom = false
    internal var flashLightEnabled = false//To be implemented

    internal var imageUri: Uri? = null
    internal var file: File? = null
    internal var imageExtensions: String = ImageExtensions.PNG

    internal var appIcon: Int? = null
    internal var latitude = 0.0
    internal var longitude = 0.0
    internal val printList = ArrayList<String>()


    fun showAppIcon(showAppIcon: Boolean, appIcon: Int?) {
        this.showAppIcon = showAppIcon
        this.appIcon = appIcon
    }

    fun showAppName(appName: Boolean, printAppName: String) {
        this.showAppName = appName
        this.printAppName = printAppName
    }

    fun showDate(showDateTime: Boolean) {
        this.showDateTime = showDateTime
    }
    fun showAuthor(showAuthor: Boolean) {
        this.showAuthor = showAuthor
    }

    fun showLatLong(showLatLong: Boolean) {
        this.showLatLong = showLatLong
    }

    fun showLocationAddress(showLocationAddress: Boolean) {
        this.showLocationAddress = showLocationAddress
    }

    fun showDataToBottom(showDataToBottom: Boolean) {
        this.showDataToBottom = showDataToBottom
    }

    fun setAuthorName(name: String) {
        this.authorName = name
    }

    fun setPrefixToAuthorNameCamera(prefixToAuthorName: String) {
        this.prefixToAuthorNameCameraChoice = prefixToAuthorName
    }

    fun setPrefixToAuthorNameGallery(prefixToAuthorName: String) {
        this.prefixToAuthorNameGalleryChoice = prefixToAuthorName
    }

    fun setImagePath(imageUri: Uri) {
        this.imageUri = imageUri
    }

//    fun setFlashLightEnabled(flashLightEnabled: Boolean) {
//        this.flashLightEnabled = flashLightEnabled
//    }

    fun setImageExtensions(imageExtensions: String) {
        this.imageExtensions = imageExtensions
    }


    private var resultListener: ImageResultListener? = null
    private var actionCode = 0
    fun showImageSourceDialog(
        context: Context,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        listener: ImageResultListener,
        actionCode: Int,
    ) {
        this.actionCode = actionCode
        val options = arrayOf("Camera", "Gallery", "Cancel")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> captureImageFromCamera(
                    context,
                    activityResultLauncher,
                    listener,
                )

                1 -> pickImageFromGallery(context, activityResultLauncher, listener)
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun pickImageFromGallery(
        context: Context,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        listener: ImageResultListener,
    ) {
        resultListener = listener
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(galleryIntent)
    }

    private fun captureImageFromCamera(
        context: Context,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        listener: ImageResultListener,
    ) {
        this.resultListener = listener
        val customCameraIntent = Intent(
            context,
            CameraActivity::class.java
        )
        activityResultLauncher.launch(customCameraIntent)
    }

    fun handleActivityResult(
        context: Context,
        resultCode: Int,
        data: Intent?,
    ) {
        if (resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
                ?: data?.extras?.let { if (it.containsKey("data")) Uri.parse(it.getString("data")) else null }
            if (imageUri != null) {
                val imagePathSel = imageUri.path
                if (imagePathSel != null) {
                    val fetchGeoLocation = FetchGeoLocation(context as Activity)
                    latitude = fetchGeoLocation.getLatitude()
                    longitude = fetchGeoLocation.getLongitude()
                    imagePath = imagePathSel
                    val galleryWrite = GalleryWrite(context)
                    galleryWrite.processCapturedImage(imageUri)
                    resultListener?.onImageProcessed(imagePath, actionCode)
                } else {
                    resultListener?.onError("Failed to process image.", actionCode)
                }
            } else {
                resultListener?.onError("Failed to obtain image.", actionCode)
            }
        } else {
            resultListener?.onError("Image selection failed.", actionCode)
        }
    }


}
