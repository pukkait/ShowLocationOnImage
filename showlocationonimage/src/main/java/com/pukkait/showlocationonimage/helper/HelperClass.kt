package com.pukkait.showlocationonimage.helper

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import com.pukkait.showlocationonimage.R
import com.pukkait.showlocationonimage.gallery.GalleryWrite
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.imagePath
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.imageUri
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.isCameraSelected
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.minimumFileSize
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage.Companion.writeBelowImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object HelperClass {
    fun showCurrentDateTime(): String {
        return SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date())
    }

    fun getPreAuthorText(whichImage: Int, preAuthorText: String): String {
        return preAuthorText.ifBlank {
            if (whichImage == 0) {
                "Captured by "
            } else {
                "Uploaded by "
            }
        }
    }

    fun askPermissionDialog(activity: Activity) {
        try {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle(R.string.runtime_permissions_txt)
            alertDialog.setMessage(R.string.compulsory_accept)
            alertDialog.setPositiveButton(R.string.enable) { dialog, which ->
                val intent = Intent()
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.setData(Uri.parse("package:" + activity.packageName))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                dialog.dismiss()
                activity.startActivity(intent)
            }
            alertDialog.setNegativeButton(R.string.settings) { dialog, _ ->
                val intent = Intent()
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.setData(Uri.parse("package:" + activity.packageName))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                dialog.dismiss()
                activity.startActivity(intent)
            }
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createImageFile(context: Context): File {
        val fileName = "IMG_" + UUID.randomUUID().toString()
        val storageDir = context.getExternalFilesDir(null)
        return File(storageDir, "$fileName${ShowLocationOnImage.imageExtensions}")
    }

    internal fun getValidDrawable(context: Context, resId: Int): Bitmap? {
        return try {
            when (val drawable = AppCompatResources.getDrawable(context, resId)) {
                is BitmapDrawable -> {
                    // It'CropImageActivity.kt a BitmapDrawable, extract the bitmap directly
                    drawable.bitmap
                }

                is VectorDrawable -> {
                    // It'CropImageActivity.kt a VectorDrawable, convert it to a bitmap
                    convertVectorDrawableToBitmap(drawable)
                }

                else -> {
                    // Unsupported drawable type
                    Toast.makeText(context, "Unsupported drawable type.", Toast.LENGTH_SHORT).show()
                    null
                }
            }
        } catch (e: Resources.NotFoundException) {
            // Resource not found
            Toast.makeText(context, "Drawable resource not found.", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun convertVectorDrawableToBitmap(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    internal fun createBackgroundPaint(): Paint {
        val backgroundPaint = Paint()
        backgroundPaint.color = Color.parseColor("#66000000")
        backgroundPaint.style = Paint.Style.FILL
        return backgroundPaint
    }

    fun getImageExtension(imageExtensions: String): Bitmap.CompressFormat {
        return when (imageExtensions) {
            "png" -> Bitmap.CompressFormat.PNG
            "jpg", "jpeg" -> Bitmap.CompressFormat.JPEG
            else -> Bitmap.CompressFormat.JPEG
        }
    }

    fun setDataOnImage(activity: Activity, imageUri: Uri?) {
        val galleryWrite = GalleryWrite(activity)
        if (writeBelowImage) {
            galleryWrite.writeBelowImage(imageUri)
        } else {
            galleryWrite.processCapturedImage(imageUri)
        }
    }

    fun saveImage(bitmap: Bitmap, context: Context) {

        val file = createImageFile(context)
        try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(
                    getImageExtension(ShowLocationOnImage.imageExtensions),
                    100,
                    fos
                )
            }
        } catch (e: IOException) {
            Log.d("aditi ", "error1 : ${e.message}")

            e.printStackTrace()
        }

        imagePath = file.absolutePath
        Log.d("aditi ", "saveImage Final : $imagePath")
        imageUri = Uri.fromFile(file)
    }

    fun getFileFromUri(
        uri: Uri,
        contentResolver: ContentResolver,
        context: Context
    ): File? {
        return try {
            val inputStream = when {
                uri.scheme == "content" -> {
                    // Handle content URIs
                    contentResolver.openInputStream(uri) ?: return null
                }

                uri.scheme == "storage" -> {
                    return File(uri.path)
                }

                uri.scheme == "file" || uri.scheme == null -> {
                    // Handle file URIs or null schemes directly
                    return File(uri.path ?: return null)
                }

                else -> {
                    return null
                }
            } ?: return null
            // Create a temporary file in the cache directory
            val tempFile = File.createTempFile("temp", null, context.cacheDir)

            // Use the input stream to write to the temp file
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}
