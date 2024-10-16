package com.pukkait.showlocationonimage.compress

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.pukkait.showlocationonimage.helper.HelperClass.getFileFromUri
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.imagePath
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.isImageCompress
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.minimumFileSize
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

fun compressImage(context: Context, uri: Uri?): Boolean {
    if (uri == null) {
        return false
    } else {
        val pd = ProgressDialog.show(context, "Loading", "Wait while processing...")

        val contentResolver = context.contentResolver
        val file: File? = getFileFromUri(uri, contentResolver, context)
        if (isImageCompress) {
            file?.let {
                val fileSizeInMB = it.length() / (1024 * 1024)
                if (fileSizeInMB > minimumFileSize) {
                    val finalFile = reduceImageSize(it, context)
                    finalFile?.let { processedFile ->
                        imagePath = processedFile.absolutePath
                        ShowLocationOnImage.imageUri = Uri.fromFile(processedFile)
                        pd.hide()
                        return true
                    } ?: run {
                        pd.hide()
                        return false
                    }
                } else {
                    pd.hide()
                    imagePath = uri.path.toString()
                    return true
                }

            } ?: run {
                pd.hide()
                imagePath = uri.path.toString()
                return false
            }
        } else {
            pd.hide()
            return true
        }

    }
}

fun reduceImageSize(file: File, context: Context): File? {
    var quality = 100 // Start with the highest quality
    var bitmap: Bitmap? = null
//    val minSize = 1.5
//    Log.d("aditi minSize", minSize.toString())
    return try {
        // Decode the image file into a Bitmap
        bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null
        // Create a new file in the cache directory for the compressed image
        val fileName = "IMG_" + UUID.randomUUID().toString()
        val compressedFile = File(context.cacheDir, "$fileName.jpg")
        // Clear the previous compressed file if it exists
        if (compressedFile.exists()) {
            compressedFile.delete()
        }
        do {
            Log.d("aditi size", compressedFile.length().toString())

            // Create output stream for the compressed file
            val outputStream = FileOutputStream(compressedFile)

            // Compress the bitmap into the output stream with current quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            // Flush and close the output stream
            outputStream.flush()
            outputStream.close()

            // Check the size of the compressed file
            val fileSizeInMB = compressedFile.length() / (1024 * 1024)
            if (fileSizeInMB > minimumFileSize) {
                quality -= 10
            }

        } while (quality > 0 && compressedFile.length() / (1024 * 1024) > minimumFileSize) // Repeat until the size is ≤ 2MB
//        val file: File = File(selectedPath)
//        val file_size = (file.length() / 1024).toString().toInt()

        // Recycle the bitmap to free up memory
        bitmap.recycle()

        compressedFile // Return the compressed file

    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        bitmap?.recycle() // Ensure the bitmap is recycled
    }
}
