package misproject.memotube

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.graphics.Bitmap
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.support.constraint.solver.widgets.Rectangle
import java.io.File
import java.io.FileOutputStream
import android.util.DisplayMetrics




// reference: http://www.vogella.com/tutorials/AndroidTouch/article.html
// https://android.jlelse.eu/a-guide-to-drawing-in-android-631237ab6e28
class MyDrawView(internal var context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var canvas: Canvas? = null
    private lateinit var bitmap : Bitmap
    private val paint = Paint()
    private val bPaint = Paint(Paint.DITHER_FLAG)
    private val path = Path()

    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()


    internal var gestureDetector: GestureDetector

    init {
        val metrics = resources.displayMetrics
        val density = (metrics.xdpi + metrics.ydpi) / 2f;

        gestureDetector = GestureDetector(context, GestureListener())

        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.color = Color.BLACK

        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND

        val w = metrics.widthPixels
        val h = metrics.heightPixels

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    }

    fun setColor(r: Int, g: Int, b: Int) {
        val rgb = Color.rgb(r, g, b)
        paint.color = rgb
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        // event when double tap occurs
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val x = e.x
            val y = e.y

            // clean drawing area on double tap
            clearCanvas()
            Log.d("Double Tap", "Tapped at: ($x,$y)")

            return true
        }

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, Rect(0, 0, 500, 500), Rect(0, 0, 500, 500), bPaint)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val eventX = event.getX(0)
        val eventY = event.getY(0)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(eventX, eventY)
                return true
            }
            MotionEvent.ACTION_MOVE ->

                path.lineTo(eventX, eventY)
            MotionEvent.ACTION_UP -> {
            }
            else -> return false
        }

        // for demostraction purposes
        gestureDetector.onTouchEvent(event)
        // Schedules a repaint.
        invalidate()
        return true
    }

    fun saveBitmap() {
        val fileName = "Memotube/" + (System.currentTimeMillis()/1000).toString() + ".png"
        val file = File(Environment.getExternalStorageDirectory(), fileName)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
    }

    fun clearCanvas() {

        bitmap.recycle()
        //bitmap!!.eraseColor(Color.TRANSPARENT)
        this.path.reset()
        invalidate()

        //System.gc()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }
}