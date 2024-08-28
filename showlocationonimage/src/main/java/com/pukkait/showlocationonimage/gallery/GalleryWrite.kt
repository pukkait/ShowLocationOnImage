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
import android.net.Uri
import android.widget.Toast
import com.pukkait.showlocationonimage.R
import com.pukkait.showlocationonimage.geotag.FetchGeoLocation
import com.pukkait.showlocationonimage.helper.HelperClass
import com.pukkait.showlocationonimage.helper.HelperClass.getPreAuthorText
import com.pukkait.showlocationonimage.helper.ShowLocationOnImage
import com.pukkait.showlocationonimage.imageConditions.InputTypeSelected
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min

class GalleryWrite(
    private val context: Activity,
) {

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

            val textPaint = createTextPaint()
            val backgroundPaint = HelperClass.createBackgroundPaint()

            val padding = textSize.toInt()
            val lineHeight = (textPaint.textSize * 1.5).toInt()
            val textAreaHeight = calculateTotalTextHeight(
                ShowLocationOnImage.printList,
                textPaint,
                canvas.width,
                padding
            ) + (2 * padding)

            drawBackground(canvas, backgroundPaint, canvas.width, canvas.height, textAreaHeight)
            drawText(canvas, textPaint, padding, canvas.height, lineHeight)

            drawAppNameAndLogo(
                canvas,
                textPaint,
                backgroundPaint,
                textAreaHeight,
                ShowLocationOnImage.appIcon
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
        logoResId: Int?
    ) {
        val appName = ShowLocationOnImage.printAppName
        textPaint.textSize = calculateTextSize(canvas.width, canvas.height)
        textPaint.color = context.getColor(R.color.white)
        textPaint.isAntiAlias = true

        val textWidth = textPaint.measureText(appName)
        val textHeight = textPaint.textSize
        val padding = 10
        var logoWidth = 0
        var logoHeight = 0
        var logoBitmap: Bitmap? = null

        if (logoResId != null) {
            logoBitmap = HelperClass.getValidDrawable(context, logoResId)
            if (logoBitmap != null) {
                logoHeight = textHeight.toInt()
                logoWidth = (logoHeight * logoBitmap.width / logoBitmap.height)
                logoBitmap = Bitmap.createScaledBitmap(logoBitmap, logoWidth, logoHeight, true)
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
        val appNameY = (canvas.height - textAreaHeight - padding).toFloat()

        canvas.drawRect(
            appNameX - padding,
            appNameY - textHeight - padding,
            appNameX + totalWidth + padding,
            appNameY + padding,
            backgroundPaint
        )

        if (logoBitmap != null) {
            val logoX = appNameX
            val logoY = appNameY - logoHeight
            canvas.drawBitmap(logoBitmap, logoX, logoY, null)
        }

        // Draw the app name next to the logo
        val textX = appNameX + logoWidth + padding
        canvas.drawText(appName, textX, appNameY, textPaint)
    }

    private fun createTextPaint(): Paint {
        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = calculateTextSize(
            context.resources.displayMetrics.widthPixels,
            context.resources.displayMetrics.heightPixels
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
        textAreaHeight: Int
    ) {
        canvas.drawRect(
            0f,
            (canvasHeight - textAreaHeight).toFloat(),
            canvasWidth.toFloat(),
            canvasHeight.toFloat(),
            backgroundPaint
        )
    }

    private fun drawText(
        canvas: Canvas,
        textPaint: Paint,
        padding: Int,
        canvasHeight: Int,
        lineHeight: Int
    ) {
        var startY = canvasHeight - padding
        for (line in wrapText(ShowLocationOnImage.printList, textPaint, canvas.width, padding)) {
            canvas.drawText(line!!, padding.toFloat(), startY.toFloat(), textPaint)
            startY -= lineHeight
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
        wrappedLines.reverse()
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

    private fun calculateTextSize(imageWidth: Int, imageHeight: Int): Float {
        var textSize = (imageWidth / 40).toFloat()
        textSize = min(textSize.toDouble(), (imageHeight / 20).toDouble()).toFloat()
        return textSize
    }
}
