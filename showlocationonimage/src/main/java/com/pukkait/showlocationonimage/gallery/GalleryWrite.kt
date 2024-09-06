package com.pukkait.showlocationonimage.gallery
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

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.pukkait.showlocationonimage.geotag.FetchGeoLocation
import com.pukkait.showlocationonimage.helper.HelperClass
import com.pukkait.showlocationonimage.helper.HelperClass.getPreAuthorText
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage
import com.pukkait.showlocationonimage.imageConditions.InputTypeSelected
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

class GalleryWrite(
    private val context: Activity,
) {

    fun writeBelowImage(imageUri: Uri?) {
        try {
            if (imageUri == null) {
                Toast.makeText(context, "File not found.!", Toast.LENGTH_SHORT).show()
                return
            }
            createPrintListing()

            val bitmap =
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))

//            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//                textSize = 50f
//                color = android.graphics.Color.WHITE
//                textAlign = Paint.Align.CENTER
//            }
//            canvas.drawText("Sample Text", (bitmap.width / 2).toFloat(), textY, paint)
//            val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//            val canvas = Canvas(mutableBitmap)

//            val textSize = calculateTextSize(canvas.width, canvas.height)
//
            val textPaint = createTextPaint(bitmap)
            val backgroundPaint = HelperClass.createBackgroundPaint()
//
            val padding = 50f / 2
            val lineHeight = (textPaint.textSize * 1.5).toInt()
            val textAreaHeight = calculateTotalTextHeight(
                ShowLocationOnImage.printList,
                textPaint,
                bitmap.width,
                padding.roundToInt()
            ) + (2 * padding)
            val resultBitmap =
                Bitmap.createBitmap(
                    bitmap.width,
                    (bitmap.height + textAreaHeight).roundToInt(), Bitmap.Config.ARGB_8888
                )
            val canvas = Canvas(resultBitmap)
            val textY = (bitmap.height + textAreaHeight).toFloat()
            canvas.drawBitmap(bitmap, null, Rect(0, 0, bitmap.width, bitmap.height), null)

            drawBackground(
                canvas,
                backgroundPaint,
                canvas.width,
                canvas.height,
                textAreaHeight.roundToInt(),
                ShowLocationOnImage.showDataToBottom
            )
            drawText(
                canvas,
                textPaint,
                padding.roundToInt(),
                canvas.height,
                lineHeight,
                ShowLocationOnImage.showDataToBottom
            )
//
            drawAppNameAndLogo(
                canvas,
                textPaint,
                backgroundPaint,
                textAreaHeight.roundToInt(),
                ShowLocationOnImage.appIcon, ShowLocationOnImage.showDataToBottom
            )

            saveImage(resultBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun processCapturedImage(imageUri: Uri?) {
        try {
            if (imageUri == null) {
                Toast.makeText(context, "File not found.!", Toast.LENGTH_SHORT).show()
                return
            }
            createPrintListing()

            val bitmap =
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
            val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)

            val textSize = calculateTextSize(canvas.width, canvas.height)

            val textPaint = createTextPaint(bitmap)
            val backgroundPaint = HelperClass.createBackgroundPaint()

            val padding = textSize.toInt() / 2
            val lineHeight = (textPaint.textSize * 1.5).toInt()
            val textAreaHeight = calculateTotalTextHeight(
                ShowLocationOnImage.printList,
                textPaint,
                canvas.width,
                padding
            ) + (2 * padding)

            drawBackground(
                canvas,
                backgroundPaint,
                canvas.width,
                canvas.height,
                textAreaHeight,
                ShowLocationOnImage.showDataToBottom
            )
            drawText(
                canvas,
                textPaint,
                padding,
                canvas.height,
                lineHeight,
                ShowLocationOnImage.showDataToBottom
            )

            drawAppNameAndLogo(
                canvas,
                textPaint,
                backgroundPaint,
                textAreaHeight,
                ShowLocationOnImage.appIcon, ShowLocationOnImage.showDataToBottom
            )

            saveImage(mutableBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun drawAppNameAndLogo(
        canvas: Canvas,
        textPaint: Paint,
        backgroundPaint: Paint,
        textAreaHeight: Int,
        logoResId: Int?,
        drawAtTop: Boolean
    ) {
        val appName = ShowLocationOnImage.printAppName

        val textWidth = textPaint.measureText(appName)
        val textHeight = textPaint.textSize
        val padding = textPaint.textSize.toInt() / 2
        var logoWidth = 0
        var logoHeight = 0
        var logoBitmap: Bitmap? = null

        if (logoResId != null) {
            logoBitmap = HelperClass.getValidDrawable(context, logoResId)
            if (logoBitmap != null) {
                val logoSize = textPaint.textSize.toInt()
                logoWidth = logoSize
                logoHeight = logoSize

                logoBitmap = Bitmap.createScaledBitmap(
                    logoBitmap,
                    logoSize / 2, logoSize / 2, true
                )
            } else {
                Toast.makeText(context, "Invalid or unsupported logo resource.", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(context, "Logo resource ID not provided.", Toast.LENGTH_SHORT).show()
        }

        // Calculate positions
        val totalWidth = logoWidth + textWidth + padding
        val appNameX = (canvas.width - totalWidth - padding).toFloat()
        val appNameY = if (drawAtTop) {
            (canvas.height - textAreaHeight - padding).toFloat()
        } else {
            (textAreaHeight + textHeight + padding).toFloat()
        }

        canvas.drawRect(
            appNameX - padding,
            appNameY - textHeight - padding,
            appNameX + totalWidth + padding,
            appNameY + padding,
            backgroundPaint
        )

        if (logoBitmap != null) {
            val logoY = (appNameY - logoHeight)
            canvas.drawBitmap(logoBitmap, appNameX - (padding / 2), logoY, null)
        }

        // Draw the app name next to the logo
        val textX = appNameX + logoWidth + padding
        canvas.drawText(appName, textX, appNameY, textPaint)
    }

    private fun createTextPaint(bitmap: Bitmap): Paint {
        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = calculateTextSize(
            bitmap.width,
            bitmap.height
//            context.resources.displayMetrics.widthPixels,
//            context.resources.displayMetrics.heightPixels
        )
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        return textPaint
    }

    private fun drawBackground(
        canvas: Canvas,
        backgroundPaint: Paint,
        canvasWidth: Int,
        canvasHeight: Int,
        textAreaHeight: Int,
        drawAtTop: Boolean
    ) {
        if (drawAtTop) {
            canvas.drawRect(
                0f,
                (canvasHeight - textAreaHeight).toFloat(),
                canvasWidth.toFloat(),
                canvasHeight.toFloat(),
                backgroundPaint
            )
        } else {
            canvas.drawRect(
                0f,
                0f,
                canvasWidth.toFloat(),
                textAreaHeight.toFloat(),
                backgroundPaint
            )

        }
    }

    private fun drawText(
        canvas: Canvas,
        textPaint: Paint,
        padding: Int,
        canvasHeight: Int,
        lineHeight: Int,
        drawAtTop: Boolean
    ) {
        val startY = if (drawAtTop) canvasHeight - padding else padding + lineHeight

        val lines = wrapText(ShowLocationOnImage.printList, textPaint, canvas.width, padding)
        if (drawAtTop) {
            var currentY = startY
            for (line in lines.reversed()) {
                canvas.drawText(line!!, padding.toFloat(), currentY.toFloat(), textPaint)
                currentY -= lineHeight
            }
        } else {
            var currentY = startY
            for (line in lines) {
                canvas.drawText(line!!, padding.toFloat(), currentY.toFloat(), textPaint)
                currentY += lineHeight
            }
        }
    }

    private fun wrapText(
        textLines: List<String>,
        paint: Paint,
        maxWidth: Int,
        padding: Int
    ): ArrayList<String?> {
        val wrappedLines = ArrayList<String?>()
        for (line in textLines) {
            val words = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var currentLine = StringBuilder()
            for (word in words) {
                val testLine =
                    currentLine.toString() + (if (currentLine.isEmpty()) "" else " ") + word
                if (paint.measureText(testLine) <= maxWidth - 2 * padding) {
                    currentLine.append((if (currentLine.isEmpty()) "" else " ") + word)
                } else {
                    if (currentLine.isNotEmpty()) {
                        wrappedLines.add(currentLine.toString())
                    }
                    currentLine = StringBuilder(word)
                }
            }
            if (currentLine.isNotEmpty()) {
                wrappedLines.add(currentLine.toString())
            }
        }
//        wrappedLines.reverse()
        return wrappedLines
    }

    private fun createPrintListing() {
        ShowLocationOnImage.printList.clear()
        try {
            if (ShowLocationOnImage.showLatLong || ShowLocationOnImage.showLocationAddress) {
                val fetchGeoLocation = FetchGeoLocation(context)
                ShowLocationOnImage.latitude = fetchGeoLocation.getLatitude()
                ShowLocationOnImage.longitude = fetchGeoLocation.getLongitude()
                addLocationToPrintList()
                addAddressToPrintList(fetchGeoLocation)

            }
        } catch (e: Exception) {
            Toast.makeText(context, "Allow all the permissions.", Toast.LENGTH_SHORT)
                .show()
        }
        addDateToPrintList()
        addAuthorNameToPrintList()
    }

    private fun addLocationToPrintList() {
        if (ShowLocationOnImage.showLatLong) {
            ShowLocationOnImage.printList.add("Latitude: ${ShowLocationOnImage.latitude} Longitude: ${ShowLocationOnImage.longitude}")
        }
    }

    private fun addAddressToPrintList(geocoder: FetchGeoLocation) {
        if (ShowLocationOnImage.showLocationAddress) {

            try {
                val addresses = geocoder.getGeocoderAddress(context)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    ShowLocationOnImage.printList.add(address.locality + ", " + address.adminArea + ", " + address.countryName)
                    ShowLocationOnImage.printList.add(address.getAddressLine(0))
                }
            } catch (ignored: Exception) {
            }
        }
    }

    private fun addDateToPrintList() {
        if (ShowLocationOnImage.showDateTime) {
            ShowLocationOnImage.printList.add(HelperClass.showCurrentDateTime())
        }
    }

    private fun addAuthorNameToPrintList() {
        if (ShowLocationOnImage.showAuthor) {
            ShowLocationOnImage.printList.add(
                String.format(
                    "%s: %s",
                    getPreAuthorText(
                        InputTypeSelected.GALLERY,
                        ShowLocationOnImage.prefixToAuthorNameGalleryChoice
                    ),
                    ShowLocationOnImage.authorName
                )
            )
        }
    }

    private fun saveImage(bitmap: Bitmap) {
        val file = HelperClass.createImageFile(context)
        try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(
                    HelperClass.getImageExtension(ShowLocationOnImage.imageExtensions),
                    100,
                    fos
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        ShowLocationOnImage.imagePath = file.absolutePath
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun calculateTotalTextHeight(
        textLines: List<String>,
        paint: Paint,
        maxWidth: Int,
        padding: Int
    ): Int {
        val lineHeight = (paint.textSize * 1.5).toInt()
        var totalHeight = 0
        val wrappedLines: List<String?> = wrapText(textLines, paint, maxWidth, padding)
        totalHeight = wrappedLines.size * lineHeight
        return totalHeight
    }

    //    private fun calculateTextSize(imageWidth: Int, imageHeight: Int): Float {
//        if (ShowLocationOnImage.textSize != 0f) {
//            return ShowLocationOnImage.textSize
//        } else {
//            var textSize = (imageWidth / 20).toFloat()
//            textSize = min(textSize.toDouble(), (imageHeight / 20).toDouble()).toFloat()
//            return textSize
//        }
//    }
    private fun calculateTextSize(imageWidth: Int, imageHeight: Int): Float {
        if (ShowLocationOnImage.textSize != 0f) {
            return ShowLocationOnImage.textSize
        } else {
            // Determine if the image is horizontal or vertical
            val isHorizontal = imageWidth > imageHeight
//            Log.d("imageWidth", imageWidth.toString())
//            Log.d("imageHeight", imageHeight.toString())
//            Log.d("isHorizontal", isHorizontal.toString())
            val textSize: Float = if (isHorizontal) {
                // Calculate text size for horizontal rectangles
                val maxTextSizeBasedOnHeight = imageHeight / 20f
                maxTextSizeBasedOnHeight
            } else {
                // Calculate text size for vertical rectangles
                val maxTextSizeBasedOnWidth = imageWidth / 30f
                maxTextSizeBasedOnWidth
            }

            return textSize
        }
    }
}
