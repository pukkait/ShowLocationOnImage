package com.pukkait.showlocationonimage.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pukkait.showlocationonimage.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object HelperClass {
    fun showCurrentDateTime(): String {
        return SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date())
    }

    fun getPreAuthorText(whichImage: Int, preAuthorText: String): String {
        return if (preAuthorText.isBlank()) {
            if (whichImage == 0) {
                "Captured by "
            } else {
                "Uploaded by "
            }
        } else {
            preAuthorText

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
            alertDialog.setNegativeButton(R.string.settings) { dialog, which ->
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

    internal fun createImageFile(context: Context): File {
        val fileName = "IMG_" + UUID.randomUUID().toString()
        val storageDir = context.getExternalFilesDir(null)
        return File(storageDir, "$fileName.jpg")
    }

    internal fun getValidDrawable(context: Context, resId: Int): Bitmap? {
        return try {
            val drawable = context.getDrawable(resId)

            when (drawable) {
                is BitmapDrawable -> {
                    // It's a BitmapDrawable, extract the bitmap directly
                    drawable.bitmap
                }

                is VectorDrawable -> {
                    // It's a VectorDrawable, convert it to a bitmap
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



}
