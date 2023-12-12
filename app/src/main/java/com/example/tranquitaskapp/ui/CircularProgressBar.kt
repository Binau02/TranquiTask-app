package com.example.tranquitaskapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.tranquitaskapp.R
import kotlin.math.sin
import kotlin.math.cos

class CircularProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private var _percentage: Float = 100f
    private var percentage: Float
        get() = _percentage
        set(value) {
            _percentage = value
            invalidate()
        }

    fun setPercentageExternal(newPercentage: Float) {
        percentage = newPercentage
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar)
        percentage = typedArray.getFloat(R.styleable.CircularProgressBar_percentage, 0f)
        typedArray.recycle()
    }
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val my_dark = ContextCompat.getColor(context, R.color.my_dark)
        val my_primary_light = ContextCompat.getColor(context, R.color.my_primary_light)

        val centerX = width / 2f
        val centerY = height / 2f

        val padding = 50f

        val radius = ((Math.min(centerX, centerY) - padding) * 0.9).toFloat()

        if(percentage<=100f) {
            paint.color = my_dark
        }else{
            paint.color = my_primary_light
        }
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 50f

        val startAngle = -180f
        val sweepAngle = (180*(percentage/100.0)).toFloat()
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, startAngle, sweepAngle, false, paint)

        if(percentage<=100f){

            val circleRadius = 70f
            val circleX = centerX + radius * cos(Math.toRadians(180 + sweepAngle.toDouble()))
                .toFloat()
            val circleY = centerY + radius * sin(Math.toRadians(180 + sweepAngle.toDouble()))
                .toFloat()
            paint.style = Paint.Style.FILL
            canvas.drawCircle(circleX, circleY, circleRadius, paint)

            val text = "$percentage%"
            paint.color = Color.WHITE
            paint.textSize = 37f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(text, circleX, circleY + paint.textSize / 2, paint)
        }

    }
}
