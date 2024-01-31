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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Float.min
import java.lang.Math.cos
import java.lang.StrictMath.sin

class SpeedMeter (context: Context, attrs: AttributeSet) : View(context,attrs) {



    private var rimColor: Int = Color.GRAY
    private var textColor: Int = Color.BLACK
    private var needleColor: Int = Color.RED
    private var textIndent: Float = 30f
    private var typeface: Typeface = Typeface.DEFAULT

    private var currentSpeed = 0

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpeedometerView)
        rimColor = typedArray.getColor(R.styleable.SpeedometerView_speedmeterTextColor, Color.GRAY)
        textColor = typedArray.getColor(R.styleable.SpeedometerView_speedmeterTextColor, Color.BLACK)
        needleColor = typedArray.getColor(R.styleable.SpeedometerView_SpeedmeterneedleColor, Color.RED)
        textIndent = typedArray.getDimension(R.styleable.SpeedometerView_speedmeterTextSize, 30f)
      /*  val fontFamily = typedArray.getString(R.styleable.SpeedMeter_fontFamily)
        if (!fontFamily.isNullOrBlank()) {
            typeface = Typeface.create(fontFamily, Typeface.DEFAULT)
        }

       */

        typedArray.recycle()
    }

    private val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY)
        rimPaint.color = rimColor
        rimPaint.style = Paint.Style.STROKE
        rimPaint.strokeWidth = 20f
        canvas.drawCircle(centerX, centerY, radius - rimPaint.strokeWidth / 2, rimPaint)


        val speed = currentSpeed
        val needleAngle = mapSpeedToAngle(speed, radius)
        val needleLength = radius - 40
        val needleX = centerX + needleLength * kotlin.math.cos(needleAngle)
        val needleY = centerY + needleLength * sin(needleAngle)
        needlePaint.color = needleColor
        needlePaint.style = Paint.Style.FILL
        canvas.drawLine(centerX, centerY, needleX.toFloat(), needleY.toFloat(), needlePaint)


        textPaint.color = textColor
        textPaint.textSize = 40f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = typeface
        for (i in 0 until 5) {
            val angle = Math.toRadians((i * 20).toDouble())
            val indicatorX = centerX + (radius - textIndent) * kotlin.math.cos(angle)
            val indicatorY = centerY + (radius - textIndent) * sin(angle)
            canvas.drawText((i * 20).toString(), indicatorX.toFloat(), indicatorY.toFloat(), textPaint)
        }
    }

    private fun mapSpeedToAngle(speed: Int, radius: Float): Double {
        return Math.toRadians(360 + speed.toDouble() * 3.0 / 10.0)
    }
    // Update speedmeter based on the received data
    fun updateData(newData: Int) {

        currentSpeed = newData


        invalidate()
    }
}
