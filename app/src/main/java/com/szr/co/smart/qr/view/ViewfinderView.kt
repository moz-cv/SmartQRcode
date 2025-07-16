package com.szr.co.smart.qr.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.szr.co.smart.qr.utils.dpToPx

class ViewfinderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val maskColor = Color.parseColor("#181818")
    private val frameColor = Color.parseColor("#3478F6")
    private val lineColor = Color.parseColor("#3478F6")
    private val frameStroke = 10f
    private val lineStroke = 6f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 可调参数
    private var frameWidthRatio = 0.5f // 扫码框宽度占View宽度比例
    private var frameHeightRatio = 0.5f // 扫码框高度占宽度比例
    private var frameCornerRadius = 10f.dpToPx() // 圆角半径
    private var frameLLength = 48f.dpToPx() // L型线条总长（含圆弧）

    // 动画相关
    private var scanLinePosition: Float = 0f
    private var animator: ValueAnimator? = null
    private var frameTop = 0f
    private var frameBottom = 0f
    private var frameLeft = 0f
    private var frameRight = 0f

    // 提供外部设置方法
    fun setFrameWidthRatio(ratio: Float) {
        frameWidthRatio = ratio
        invalidate()
    }
    fun setFrameHeightRatio(ratio: Float) {
        frameHeightRatio = ratio
        invalidate()
    }
    fun setFrameCornerRadius(radius: Float) {
        frameCornerRadius = radius
        invalidate()
    }
    fun setFrameLLength(length: Float) {
        frameLLength = length
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()

        // 背景
        canvas.drawColor(maskColor)

        // 扫码框区域（可调比例）
        val frameW = width * frameWidthRatio
        val frameH = frameW * frameHeightRatio
        frameLeft = (width - frameW) / 2
        frameTop = (height - frameH) / 2
        frameRight = frameLeft + frameW
        frameBottom = frameTop + frameH

        paint.color = frameColor
        paint.strokeWidth = frameStroke
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        val r = frameCornerRadius
        val l = frameLLength

        // 左上
        canvas.drawArc(
            frameLeft, frameTop, frameLeft + 2 * r, frameTop + 2 * r,
            180f, 90f, false, paint
        )
        canvas.drawLine(frameLeft + r, frameTop, frameLeft + l, frameTop, paint)
        canvas.drawLine(frameLeft, frameTop + r, frameLeft, frameTop + l, paint)

        // 右上
        canvas.drawArc(
            frameRight - 2 * r, frameTop, frameRight, frameTop + 2 * r,
            270f, 90f, false, paint
        )
        canvas.drawLine(frameRight - r, frameTop, frameRight - l, frameTop, paint)
        canvas.drawLine(frameRight, frameTop + r, frameRight, frameTop + l, paint)

        // 左下
        canvas.drawArc(
            frameLeft, frameBottom - 2 * r, frameLeft + 2 * r, frameBottom,
            90f, 90f, false, paint
        )
        canvas.drawLine(frameLeft + r, frameBottom, frameLeft + l, frameBottom, paint)
        canvas.drawLine(frameLeft, frameBottom - r, frameLeft, frameBottom - l, paint)

        // 右下
        canvas.drawArc(
            frameRight - 2 * r, frameBottom - 2 * r, frameRight, frameBottom,
            0f, 90f, false, paint
        )
        canvas.drawLine(frameRight - r, frameBottom, frameRight - l, frameBottom, paint)
        canvas.drawLine(frameRight, frameBottom - r, frameRight, frameBottom - l, paint)

        // 中间横线（动画）
        paint.color = lineColor
        paint.strokeWidth = lineStroke
        paint.style = Paint.Style.STROKE
        val scanY = scanLinePosition
        canvas.drawLine(frameLeft + 8, scanY, frameRight - 8, scanY, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startScanAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    private fun startScanAnimation() {
        val startY = frameTop
        val endY = frameBottom
        animator?.cancel()
        animator = ValueAnimator.ofFloat(startY, endY).apply {
            duration = 1800L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                scanLinePosition = it.animatedValue as Float
                postInvalidateOnAnimation()
            }
        }
        // 立即启动动画（如果frameTop==frameBottom==0，等onDraw后再启动）
        if (startY != endY) {
            animator?.start()
        } else {
            // 延迟启动，等onDraw后frameTop/bottom有值
            postDelayed({ startScanAnimation() }, 100)
        }
    }
}