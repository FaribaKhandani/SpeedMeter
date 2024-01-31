package com.example.speedmeter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Float.min
import java.lang.Math.cos
import java.lang.Math.sin

class TachMeter (context: Context, attrs: AttributeSet) : View(context, attrs) {


    private var maxRpm = 2000
    private var currentRpm = 0

    private var textColor = Color.BLACK
    private var textSize = 40f
    private var serifsColor = Color.BLACK
    private var serifsLength = 20f

    private var paint = Paint()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TachometerView)

        textColor = typedArray.getColor(R.styleable.TachometerView_tachmeterTextColor, Color.BLACK)
        textSize = typedArray.getDimension(R.styleable.TachometerView_tachmeterTextSize, 40f)
        serifsColor = typedArray.getColor(R.styleable.TachometerView_TachmetertachSerifsColor, Color.BLACK)
        serifsLength = typedArray.getDimension(R.styleable.TachometerView_TachmetertachSerifsLength, 20f)

        typedArray.recycle()

        paint.color = textColor
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
    }

    //Draw the stroke and PRM

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY)


        val backgroundPaint = Paint()
        backgroundPaint.color = Color.GRAY
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)


        val needlePaint = Paint()
        needlePaint.color = Color.RED
        needlePaint.strokeWidth = 8f

        val maxAngle = 2000f


        val currentAngle = maxAngle * (currentRpm.coerceIn(0, maxRpm).toFloat() / maxRpm)
        val endX = centerX + radius * kotlin.math.cos(Math.toRadians(currentAngle.toDouble()))
        val endY = centerY - radius * kotlin.math.sin(Math.toRadians(currentAngle.toDouble()))

        canvas.drawLine(centerX, centerY, endX.toFloat(), endY.toFloat(), needlePaint)


        val textPaint = Paint()
        textPaint.color = textColor
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER

        canvas.drawText("$currentRpm RPM", centerX, centerY + textSize, textPaint)



        for (i in 0 until 6) {
            val angle = Math.toRadians((i * 30).toDouble())
            val startX = centerX + (radius - serifsLength) * kotlin.math.cos(angle)
            val startY = centerY - (radius - serifsLength) * kotlin.math.sin(angle)
            val endX = centerX + radius * kotlin.math.cos(angle)
            val endY = centerY - radius * kotlin.math.sin(angle)

            val serifPaint = Paint()
            serifPaint.color = serifsColor
            canvas.drawLine(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat(), serifPaint)





            val numberPaint = Paint()
            numberPaint.color = textColor
            numberPaint.textSize = textSize
            numberPaint.textAlign = Paint.Align.CENTER

            for (i in 0 until 6) {
                val angle = Math.toRadians(60 * i.toDouble())
                val numberX = centerX + (radius - serifsLength - textSize) * kotlin.math.cos(angle)
                val numberY = centerY - (radius - serifsLength - textSize) * kotlin.math.sin(angle)
                canvas.drawText((maxRpm / 5 * i).toString(), numberX.toFloat(),
                    (numberY + textSize / 2).toFloat(), numberPaint)
            }
        }
    }

    // Update tachmeter based on the received data

    fun updateRpm(newRpm: Int) {
        currentRpm = newRpm
        invalidate()
    }
}








