package com.pukkait.showlocationonimage.cropImage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.pukkait.showlocationonimage.R
import java.io.FileNotFoundException
import kotlin.math.max
import kotlin.math.min

class CropImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("aditi", "onMeasure: ")
//        Log.d("aditi", "onMeasure: ")
        screenWidth = MeasureSpec.getSize(widthMeasureSpec)
        imageWidth = screenWidth
        imageHeight= MeasureSpec.getSize(heightMeasureSpec)
//        imageHeight = ((screenWidth.toFloat() / src.width) * src.height).toInt()
        setMeasuredDimension(screenWidth, imageHeight)
//        screenWidth = MeasureSpec.getSize(widthMeasureSpec)
//        imageWidth = screenWidth
//        imageHeight = MeasureSpec.getSize(heightMeasureSpec)
//        setMeasuredDimension(imageWidth, imageHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("aditi", "onSizeChanged: ")

//        val desiredWidthInPx = imageWidth
//        val derivedHeightInPx = (desiredWidthInPx / aspectRatio).toInt()
//
//        output = Bitmap.createScaledBitmap(src, desiredWidthInPx, derivedHeightInPx, true)
//        rectF2 = RectF(0f, 0f, imageWidth.toFloat(), derivedHeightInPx.toFloat())
        // Update dimensions based on the new size
        val desiredWidthInPx = w // Use the full width
        val desiredHeightInPx = src.height // Use the full height
        imageHeight = src.height
        output = Bitmap.createScaledBitmap(src, desiredWidthInPx, desiredHeightInPx, true)
        rectF2 = RectF(0f, 0f, imageWidth.toFloat(), desiredHeightInPx.toFloat())
    }

    private fun blurBitmap(context: Context, bitmap: Bitmap): Bitmap {
        Log.d("aditi", "blurBitmap: ")

        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val renderScript = RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(
            renderScript,
            android.renderscript.Element.U8_4(renderScript)
        )
        val inputAllocation = android.renderscript.Allocation.createFromBitmap(renderScript, bitmap)
        val outputAllocation =
            android.renderscript.Allocation.createFromBitmap(renderScript, outputBitmap)

        blurScript.setRadius(25f)
        blurScript.setInput(inputAllocation)
        blurScript.forEach(outputAllocation)
        outputAllocation.copyTo(outputBitmap)

        return outputBitmap
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("aditi", "onDraw: ")

        parent.requestDisallowInterceptTouchEvent(true)
        canvas.apply {
            val blurredBitmap = blurBitmap(context, output) // Adjust radius as needed

            // background image under overlay
            drawBitmap(output, 0f, 0f, null)

            // dark overlay
//            drawRect(rectF2, rectPaint2)
            drawBitmap(
                blurredBitmap,
                null,
                rectF2,
                null
            ) // rectF2 is the area where the blurred image will be drawn

            // clip rect same as movable rect. This will hide everything outside
            clipRect(rectF)

            // visible clear image covered by clip rect
            drawBitmap(output, 0f, 0f, null)

            // movable rect
            drawRoundRect(rectF, 10f, 10f, rectPaint)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("aditi", "onTouchEvent: ")

        if (event != null) {
            motionX = event.x
            motionY = event.y
            when (event.action) {
                MotionEvent.ACTION_MOVE -> moveMove()
                MotionEvent.ACTION_DOWN -> moveDown()
                MotionEvent.ACTION_UP -> moveUp()
            }
        }

        return true

    }

    private fun moveMove() {
        Log.d("aditi", "moveMove: ")

        //moving the whole rect
        if (c5) {

            if (pr < screenWidth && motionX > plx) {
                pr = min(r + (motionX - plx), screenWidth.toFloat())
                pl = min(l + (motionX - plx), screenWidth.toFloat() - (r - l))
            }
            if (pl > 0 && motionX < plx) {
                pr = max(r - (plx - motionX), 0f + (r - l))
                pl = max(l - (plx - motionX), 0f)
            }
            if (pb < imageHeight && motionY > pty) {
                pb = min(b + (motionY - pty), imageHeight.toFloat())
                pt = min(t + (motionY - pty), imageHeight.toFloat() - (b - t))
            }
            if (pt > 0 && motionY < pty) {
                pb = max(b - (pty - motionY), 0f + (b - t))
                pt = max(t - (pty - motionY), 0f)
            }
            rectF.set(pl + 5, pt + 5, pr - 5, pb - 5)
            invalidate()
        }

        // moving while holding corners
        if (c6) {
            if (motionX > 0 && motionX < (pr - 100)) pl = motionX
            if (motionY > 0 && motionY < (pb - 100)) pt = motionY
        }
        if (c7) {
            if (motionY > 0 && motionY < (pb - 100)) pt = motionY
            if (motionX > (pl + 100) && motionX < screenWidth) pr = motionX
        }
        if (c8) {
            if (motionX > (pl + 100) && motionX < screenWidth) pr = motionX
            if (motionY > (pt + 100) && motionY < imageHeight) pb = motionY
        }
        if (c9) {
            if (motionX > 0 && motionX < (pr - 100)) pl = motionX
            if (motionY > (pt + 100) && motionY < imageHeight) pb = motionY
        }

        // For moving the edge
        if (c1) if (motionX > 0 && motionX < (pr - 100)) pl = motionX
        if (c2) if (motionY > 0 && motionY < (pb - 100)) pt = motionY
        if (c3) if (motionX > (pl + 100) && motionX < screenWidth) pr = motionX
        if (c4) if (motionY > (pt + 100) && motionY < imageHeight) pb = motionY


        rectF.set(pl + 5, pt + 5, pr - 5, pb - 5)
        invalidate()

    }

    private fun moveDown() {
        Log.d("aditi", "moveDown: ")

        if (motionX > (pl + rng) && motionX < (pr - rng) && motionY > (pt + rng) && motionY < (pb - rng)) {

            c5 = true
            l = pl
            t = pt
            r = pr
            b = pb

            if (motionY >= 0 && motionY <= imageHeight) pty = motionY
            if (motionX >= 0 && motionX <= screenWidth) plx = motionX

            invalidate()
            return
        }

        if (motionX in pl - rng..pl + rng && motionY in pt - rng..pt + rng) {
            c6 = true
            invalidate()
            return
        }
        if (motionY in pt - rng..pt + rng && motionX in pr - rng..pr + rng) {
            c7 = true
            invalidate()
            return
        }
        if (motionX in pr - rng..pr + rng && motionY in pb - rng..pb + rng) {
            c8 = true
            invalidate()
            return
        }
        if (motionY in pb - rng..pb + rng && motionX in pl - rng..pl + rng) {
            c9 = true
            invalidate()
            return
        }




        if (motionX > (pl - rng) && motionX < (pl + rng) && motionY > pt && motionY < pb) {
            c1 = true
            invalidate()
            return
        }
        if (motionY > (pt - rng) && motionY < (pt + rng) && motionX > pl && motionX < pr) {
            c2 = true
            invalidate()
            return
        }
        if (motionX > (pr - rng) && motionX < (pr + rng) && motionY > pt && motionY < pb) {
            c3 = true
            invalidate()
            return
        }
        if (motionY > (pb - rng) && motionY < (pb + rng) && motionX > pl && motionX < pr) {
            c4 = true
            invalidate()
            return
        }


        invalidate()

    }

    private fun moveUp() {
        Log.d("aditi", "moveUp: ")

        c1 = false
        c2 = false
        c3 = false
        c4 = false
        c5 = false

        c6 = false
        c7 = false
        c8 = false
        c9 = false

        invalidate()
    }


    // pass bitmap image


    private var screenWidth = 0
    private var imageWidth = 0
    private var imageHeight = 0
    private var src: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.mountain)
    val aspectRatio: Float = src.width / src.height.toFloat()

    fun setImage(imageUri: Uri) {
        try {
            src = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
            if(src==null){
                Log.d("aditi", "src null $imageUri")
            }
        } catch (e: FileNotFoundException) {
            Log.d("aditi", "File not found: ${e.message}")
        } catch (e: Exception) {
            Log.d("aditi", "Error opening image: ${e.message}")
        }

    }

    fun setImageBitmap(bitmap: Bitmap) {
        src = bitmap
    }

    //get the bitmap from test():Bitmap? function
    private lateinit var output: Bitmap

    public fun crop(): Bitmap {
        Log.d("aditi", "crop: ")

        val cropWidth: Float = (pr - pl)
        val cropHeight: Float = (pb - pt)

        invalidate()
        //returning the bitmap
        return Bitmap.createBitmap(
            output,
            pl.toInt(),
            pt.toInt(),
            cropWidth.toInt(),
            cropHeight.toInt()
        )
    }


    private var motionX = 0f
    private var motionY = 0f
    private var rng = 40f // Touch range for the 4 side and 4 corners of the rect

    // p for point and l=left t=top r=right b=bottom
    private var pl = 100f
    private var plx = 100f //hold motionX value from moveDown() function

    private var pt = 100f
    private var pty = 100f //hold motionY value from moveDown() function

    private var pr = 300f
    private var pb = 400f

    //hold left,top,right,bottom value from moveDown() function
    private var l = 0f
    private var t = 0f
    private var r = 0f
    private var b = 0f

    // check user touch Down on rect edges, corners or inside

    //edges point
    private var c1 = false
    private var c2 = false
    private var c3 = false
    private var c4 = false

    private var c5 = false  // for inside selection to move the whole rect

    //corners point
    private var c6 = false
    private var c7 = false
    private var c8 = false
    private var c9 = false


    // resizable rect
    private var rectF = RectF(pl + 5, pt + 5, pr - 5, pb - 5)
    private val rectPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 15f
        color = Color.YELLOW

    }

    // dark overlay rect
    private val foregroundArcColor =
        context.resources?.getColor(R.color.purple_200, null) ?: Color.GRAY
    private var rectF2 = RectF(0f, 0f, screenWidth.toFloat(), imageHeight.toFloat())
    private val rectPaint2 = Paint().apply {
        style = Paint.Style.FILL
        color = foregroundArcColor
    }

}
