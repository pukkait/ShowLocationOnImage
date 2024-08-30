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
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import com.pukkait.showlocationonimage.camera.CameraActivity
import com.pukkait.showlocationonimage.gallery.GalleryWrite
import com.pukkait.showlocationonimage.geotag.FetchGeoLocation
import com.pukkait.showlocationonimage.imageConditions.ImageExtensions
import java.io.File


class ShowLocationOnImage {
    companion object {

        internal var textSize: Float = 0f
        internal var printAppName: String = ""
        internal var authorName: String = ""
        internal var prefixToAuthorNameCameraChoice: String = ""
        internal var prefixToAuthorNameGalleryChoice: String = ""
        internal var imagePath: String = ""

        internal var showAppIcon: Boolean = false
        internal var showAppName: Boolean = false
        internal var showLocationAddress: Boolean = true
        internal var showLatLong: Boolean = true
        internal var showDateTime: Boolean = true
        internal var showAuthor: Boolean = true
        internal var showDataToBottom: Boolean = true
        internal var flashLightEnabled: Boolean = false // To be implemented

        internal var imageUri: Uri? = null
        internal var file: File? = null
        internal var imageExtensions: String = ImageExtensions.PNG

        internal var appIcon: Int? = null
        internal var latitude: Double = 0.0
        internal var longitude: Double = 0.0
        internal val printList = ArrayList<String>()

    }

    fun showAppIcon(showAppIcon: Boolean, appIcon: Int?) {
        ShowLocationOnImage.showAppIcon = showAppIcon
        ShowLocationOnImage.appIcon = appIcon
    }

    fun showAppName(appName: Boolean, printAppName: String) {
        showAppName = appName
        ShowLocationOnImage.printAppName = printAppName
    }

    fun showDate(showDateTime: Boolean) {
        ShowLocationOnImage.showDateTime = showDateTime
    }

    fun showAuthor(showAuthor: Boolean) {
        ShowLocationOnImage.showAuthor = showAuthor
    }

    fun showLatLong(showLatLong: Boolean) {
        ShowLocationOnImage.showLatLong = showLatLong
    }

    fun showLocationAddress(showLocationAddress: Boolean) {
        ShowLocationOnImage.showLocationAddress = showLocationAddress
    }

    fun showDataToBottom(showDataToBottom: Boolean) {
        ShowLocationOnImage.showDataToBottom = showDataToBottom
    }

    fun setAuthorName(name: String) {
        authorName = name
    }

    fun setPrefixToAuthorNameCamera(prefixToAuthorName: String) {
        prefixToAuthorNameCameraChoice = prefixToAuthorName
    }

    fun setPrefixToAuthorNameGallery(prefixToAuthorName: String) {
        prefixToAuthorNameGalleryChoice = prefixToAuthorName
    }

    fun setImagePath(imageUri: Uri) {
        ShowLocationOnImage.imageUri = imageUri
    }

    fun setTextSize(size: Float) {
        textSize = size
    }

    // fun setFlashLightEnabled(flashLightEnabled: Boolean) {
    //     ShowLocationOnImage.flashLightEnabled = flashLightEnabled
    // }

    fun setImageExtensions(imageExtensions: String) {
        ShowLocationOnImage.imageExtensions = imageExtensions
    }

    private var resultListener: ImageResultListener? = null
    private var actionCode: Int = 0

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
                0 -> captureImageFromCamera(context, activityResultLauncher, listener, actionCode)
                1 -> pickImageFromGallery(activityResultLauncher, listener,actionCode)
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

     fun pickImageFromGallery(
        activityResultLauncher: ActivityResultLauncher<Intent>,
        listener: ImageResultListener,
        actionCode: Int
    ) {
         this.actionCode = actionCode
         this.resultListener = listener
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(galleryIntent)
    }

    fun captureImageFromCamera(
        context: Context,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        listener: ImageResultListener,
        actionCode: Int
    ) {
        this.actionCode = actionCode
        this.resultListener = listener
        val customCameraIntent = Intent(context, CameraActivity::class.java)
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

