package com.example.tranquitaskapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.tranquitaskapp.R
import kotlin.math.sin
import kotlin.math.cos

class CircularProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private var _percentage: Float = 100f // Attribut pour stocker le pourcentage de progression
    var percentage: Float
        get() = _percentage
        set(value) {
            _percentage = value
            invalidate() // Trigger redraw when the percentage is set
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

//        val centerX = width / 2f
//        val centerY = height / 2f
//        val radius = (Math.min(centerX, centerY) * 0.9).toFloat()

        val padding = 50f // Set the padding you want

        val centerX = (width - padding * 2) / 2f + padding
        val centerY = (height - padding * 2) / 2f + padding
        val radius = ((Math.min(centerX, centerY) - padding) * 0.9).toFloat()

        if(percentage<=100f) {
            paint.color = Color.BLACK // Couleur de la barre de progression
        }else{
            paint.color = Color.LTGRAY // Couleur de la barre de progression
        }
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 50f // Largeur de la barre

        // Dessiner une portion de cercle progressif de -π à 0
        val startAngle = -180f
        val sweepAngle = (180*(percentage/100.0)).toFloat()
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, startAngle, sweepAngle, false, paint)

        if(percentage<=100f){
            // Dessiner le cercle à la fin de la barre de progression
            val circleRadius = 70f // Taille du cercle
            val circleX = centerX + radius * cos(Math.toRadians(180 + sweepAngle.toDouble()))
                .toFloat()
            val circleY = centerY + radius * sin(Math.toRadians(180 + sweepAngle.toDouble()))
                .toFloat()
            paint.style = Paint.Style.FILL
            canvas.drawCircle(circleX, circleY, circleRadius, paint)

            val text = "$percentage%" // Texte à afficher
            paint.color = Color.WHITE // Couleur du texte
            paint.textSize = 40f // Taille du texte
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(text, circleX, circleY + paint.textSize / 2, paint)
        }

    }
}
