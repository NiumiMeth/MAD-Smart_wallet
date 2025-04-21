package com.example.madlab_exam3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class ExpensePieChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var budget: Double = 0.0
    private var spent: Double = 0.0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private val backgroundColor = Color.LTGRAY
    private var expenseColor = Color.parseColor("#FF4081") // Default expense color
    private val overBudgetColor = Color.RED // Color when exceeding 80%

    fun setData(budget: Double, spent: Double) {
        this.budget = budget
        this.spent = spent
        // Update expense color based on the percentage
        expenseColor = if (budget > 0 && (spent / budget) > 0.8) {
            overBudgetColor
        } else {
            Color.parseColor("#FF4081") // Revert to default if not exceeded
        }
        invalidate() // Request a redraw of the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) / 2f * 0.8f // Adjust for padding

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Draw background circle (remaining budget)
        paint.color = backgroundColor
        canvas.drawOval(rectF, paint)

        if (budget > 0) {
            val sweepAngle = (spent / budget * 360f).toFloat()

            // Draw expense arc with dynamic color
            paint.color = expenseColor
            canvas.drawArc(rectF, -90f, sweepAngle, true, paint) // Start from top (-90 degrees)
        }
    }
}