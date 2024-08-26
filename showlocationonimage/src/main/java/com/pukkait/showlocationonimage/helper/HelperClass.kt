package com.pukkait.showlocationonimage.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.pukkait.showlocationonimage.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object HelperClass {
    fun addDateToPrintList(): String {
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


}
