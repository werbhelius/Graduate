package com.werb.graduate.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import ja.burhanrashid52.photoeditor.LinePath
import java.util.*

/**
 * Created by wanbo on 2020/6/15.
 */
class EraserImageView: AppCompatImageView {

    val DEFAULT_BRUSH_SIZE = 25.0f
    val DEFAULT_ERASER_SIZE = 50.0f
    val DEFAULT_OPACITY = 255

    private var mBrushSize = DEFAULT_BRUSH_SIZE
    private var mOpacity = DEFAULT_OPACITY

    private val mDrawnPaths = Stack<LinePath>()
    private val mRedoPaths = Stack<LinePath>()
    private val mDrawPaint = Paint()

    private var mDrawCanvas: Canvas? = null
    private var mBrushDrawMode = false

    private var mPath: Path? = null
    private var mTouchX = 0f
    private  var mTouchY:kotlin.Float = 0f
    private val TOUCH_TOLERANCE = 4f


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupBrushDrawing()
    }

    private fun setupBrushDrawing() {
        //Caution: This line is to disable hardware acceleration to make eraser feature work properly
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        mDrawPaint.color = Color.TRANSPARENT
        setupPathAndPaint()
    }

    private fun setupPathAndPaint() {
        mPath = Path()
        mDrawPaint.isAntiAlias = true
        mDrawPaint.isDither = true
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND
        mDrawPaint.strokeWidth = mBrushSize
        mDrawPaint.alpha = mOpacity
        mDrawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private fun refreshBrushDrawing() {
        mBrushDrawMode = true
        setupPathAndPaint()
    }

    fun setBrushDrawingMode(brushDrawMode: Boolean) {
        mBrushDrawMode = brushDrawMode
        if (brushDrawMode) {
            this.visibility = View.VISIBLE
            refreshBrushDrawing()
        }
    }

    fun getBrushDrawingMode(): Boolean {
        return mBrushDrawMode
    }

    fun setBrushSize(size: Float) {
        mBrushSize = size
        setBrushDrawingMode(true)
    }

    fun setBrushColor(@ColorInt color: Int) {
        mDrawPaint.color = color
        setBrushDrawingMode(true)
    }

    fun getBrushSize(): Float {
        return mBrushSize
    }

    fun getBrushColor(): Int {
        return mDrawPaint.color
    }

    fun clearAll() {
        mDrawnPaths.clear()
        mRedoPaths.clear()
        if (mDrawCanvas != null) {
            mDrawCanvas!!.drawColor(0, PorterDuff.Mode.CLEAR)
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mDrawCanvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (linePath in mDrawnPaths) {
            canvas.drawPath(linePath.getDrawPath(), linePath.getDrawPaint())
        }
        canvas.drawPath(mPath!!, mDrawPaint)
    }

    /**
     * Handle touch event to draw paint on canvas i.e brush drawing
     *
     * @param event points having touch info
     * @return true if handling touch events
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mBrushDrawMode) {
            val touchX = event.x
            val touchY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> touchStart(touchX, touchY)
                MotionEvent.ACTION_MOVE -> touchMove(touchX, touchY)
                MotionEvent.ACTION_UP -> touchUp()
            }
            invalidate()
            true
        } else {
            false
        }
    }

    fun undo(): Boolean {
        if (!mDrawnPaths.empty()) {
            mRedoPaths.push(mDrawnPaths.pop())
            invalidate()
        }
        return !mDrawnPaths.empty()
    }

    fun redo(): Boolean {
        if (!mRedoPaths.empty()) {
            mDrawnPaths.push(mRedoPaths.pop())
            invalidate()
        }
        return !mRedoPaths.empty()
    }


    private fun touchStart(x: Float, y: Float) {
        mRedoPaths.clear()
        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mTouchX = x
        mTouchY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mTouchX)
        val dy = Math.abs(y - mTouchY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mTouchX, mTouchY, (x + mTouchX) / 2, (y + mTouchY) / 2)
            mTouchX = x
            mTouchY = y
        }
    }

    private fun touchUp() {
        mPath!!.lineTo(mTouchX, mTouchY)
        // Commit the path to our offscreen
        mDrawCanvas!!.drawPath(mPath!!, mDrawPaint)
        // kill this so we don't double draw
        mDrawnPaths.push(LinePath(mPath, mDrawPaint))
        mPath = Path()
    }

}