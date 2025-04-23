package com.example.madlab_exam3.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.madlab_exam3.R

class GaugeView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var progress = 0
    private val maxProgress = 100

    private val backgroundArcColor = ContextCompat.getColor(context, R.color.progress_background)
    private val progressArcColor = ContextCompat.getColor(context, R.color.progress)
    private val textColor = ContextCompat.getColor(context, R.color.homeText)

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundArcColor
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.gauge_stroke_width)
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = progressArcColor
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = resources.getDimension(R.dimen.gauge_stroke_width)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = resources.getDimension(R.dimen.gauge_text_size)
        textAlign = Paint.Align.CENTER
    }

    private val oval = RectF()

    fun setProgress(value: Int) {
        progress = value.coerceIn(0, maxProgress)
        invalidate() // Request a redraw of the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val strokeWidth = backgroundPaint.strokeWidth
        val padding = strokeWidth / 2f

        oval.set(padding, padding, width - padding, height - padding)

        // Draw background arc
        canvas.drawArc(oval, 135f, 270f, false, backgroundPaint)

        // Calculate progress angle
        val progressAngle = (progress.toFloat() / maxProgress) * 270f

        // Draw progress arc
        canvas.drawArc(oval, 135f, progressAngle, false, progressPaint)

        // Draw progress text
        val text = "$progress%"
        val textX = width / 2f
        val textY = height / 2f + (textPaint.textSize - textPaint.descent()) / 2f // Center vertically
        canvas.drawText(text, textX, textY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)

        // Make it a circle
        val size = measuredWidth.coerceAtMost(measuredHeight)
        setMeasuredDimension(size, size)
    }
}