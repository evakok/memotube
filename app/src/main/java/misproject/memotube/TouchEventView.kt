package misproject.memotube

import android.content.Context
import android.graphics.*
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View

// reference: http://www.vogella.com/tutorials/AndroidTouch/article.html
// https://android.jlelse.eu/a-guide-to-drawing-in-android-631237ab6e28
class TouchEventView(internal var context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private val path = Path()

    internal var gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())

        paint.isAntiAlias = true
        paint.strokeWidth = 6f
        paint.color = Color.BLACK

        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
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
            path.reset()
            Log.d("Double Tap", "Tapped at: ($x,$y)")

            return true
        }

    }

    override fun onDraw(canvas: Canvas) {
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

    fun clearCanvas() {
        this.path.reset()
        invalidate()
    }
}