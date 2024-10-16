package  com.pukkait.showlocationonimage.cropper


import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.pukkait.showlocationonimage.R

internal class CropImageIntentChooser(
  private val activity: ComponentActivity,
  private val callback: ResultCallback,
) {
  internal interface ResultCallback {
    fun onSuccess(uri: Uri?)
    fun onCancelled()
  }

  internal companion object {
    const val GOOGLE_PHOTOS = "com.google.android.apps.photos"
    const val GOOGLE_PHOTOS_GO = "com.google.android.apps.photosgo"
    const val SAMSUNG_GALLERY = "com.sec.android.gallery3d"
    const val ONEPLUS_GALLERY = "com.oneplus.gallery"
    const val MIUI_GALLERY = "com.miui.gallery"
  }

  private var title: String = activity.getString(R.string.pick_image_chooser_title)
  private var priorityIntentList = listOf(
    GOOGLE_PHOTOS,
    GOOGLE_PHOTOS_GO,
    SAMSUNG_GALLERY,
    ONEPLUS_GALLERY,
    MIUI_GALLERY,
  )
  private val intentChooser = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityRes ->
    if (activityRes.resultCode == Activity.RESULT_OK) {
      /**
       * Here we don't know whether a gallery app or the camera app is selected
       * via the intent chooser. If a gallery app is selected and an image is
       * chosen then we get the result from activityRes.
       * If a camera app is selected we take the uri we passed to the camera
       * app for storing the captured image
       */
      (activityRes.data?.data).let { uri ->
        callback.onSuccess(uri)
      }
    } else {
      callback.onCancelled()
    }
  }

  fun showChooserIntent(
    includeGallery: Boolean,
  ) {
    val allIntents: MutableList<Intent> = ArrayList()
    val packageManager = activity.packageManager

    if (includeGallery) {
      var galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT)
      if (galleryIntents.isEmpty()) {
        // if no intents found for get-content try to pick intent action (Huawei P9).
        galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK)
      }
      allIntents.addAll(galleryIntents)
    }

    val target = if (allIntents.isEmpty()) {
      Intent()
    } else {
      Intent(Intent.ACTION_CHOOSER, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
        if (includeGallery) {
          action = Intent.ACTION_PICK
          type = "image/*"
        }
      }
    }
    // Create a chooser from the main intent
    val chooserIntent = Intent.createChooser(target, title)
    // Add all other intents
    chooserIntent.putExtra(
      Intent.EXTRA_INITIAL_INTENTS,
      allIntents.toTypedArray<Parcelable>(),
    )
    intentChooser.launch(chooserIntent)
  }

  /**
   * Get all Gallery intents for getting image from one of the apps of the device that handle
   * images.
   */
  private fun getGalleryIntents(packageManager: PackageManager, action: String): List<Intent> {
    val intents: MutableList<Intent> = ArrayList()
    val galleryIntent = if (action == Intent.ACTION_GET_CONTENT) {
      Intent(action)
    } else {
      Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }
    galleryIntent.type = "image/*"

    val flags = 0
    val listGallery = when {
      SDK_INT >= 33 -> packageManager.queryIntentActivities(galleryIntent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
      else -> @Suppress("DEPRECATION") packageManager.queryIntentActivities(galleryIntent, flags)
    }
    for (res in listGallery) {
      val intent = Intent(galleryIntent)
      intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
      intent.setPackage(res.activityInfo.packageName)
      intents.add(intent)
    }
    // sort intents
    val priorityIntents = mutableListOf<Intent>()
    for (pkgName in priorityIntentList) {
      intents.firstOrNull { it.`package` == pkgName }?.let {
        intents.remove(it)
        priorityIntents.add(it)
      }
    }
    intents.addAll(0, priorityIntents)
    return intents
  }

  fun setupPriorityAppsList(appsList: List<String>): CropImageIntentChooser = apply {
    priorityIntentList = appsList
  }

  fun setIntentChooserTitle(title: String): CropImageIntentChooser = apply {
    this.title = title
  }
}
