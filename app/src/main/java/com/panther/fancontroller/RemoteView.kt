package com.panther.fancontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

enum class FanSpeed(val labelWord: Int, val labelNumber: Int ) {
    OFF(R.string.fan_off, R.string.fan_zero),
    LOW(R.string.fan_low, R.string.fan_one),
    MEDIUM(R.string.fan_medium, R.string.fan_two),
    HIGH(R.string.fan_high, R.string.fan_three),
    HIGHEST(R.string.fan_highest, R.string.fan_four);

    fun next() = when(this) {

        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> HIGHEST
        HIGHEST -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 25
private const val RADIUS_OFFSET_INDICATION = -15

class RemoteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isTextMode = false
    private var radius = 0.0f // Radius of the fan circle
    private var fanSpeed = FanSpeed.OFF //default fan speed
    private val pointPosition : PointF = PointF(0.0f, 0.0f) //This will be used to draw several of the views element

    private var lowColor = 0
    private var mediumColor = 0
    private var highColor = 0
    private var highestColor = 0

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.RemoteView) {
            lowColor = getColor(R.styleable.RemoteView_color1, 0)
            mediumColor = getColor(R.styleable.RemoteView_color2, 0)
            highColor = getColor(R.styleable.RemoteView_color3, 0)
            highestColor = getColor(R.styleable.RemoteView_color4, 0)
        }
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.labelWord)

        invalidate()
        return true
    }


    override fun onSizeChanged(width: Int, heiht: Int, oldw: Int, oldh: Int) {
        radius = (min(width, heiht) / 2.0 * 0.8).toFloat()
    }

    // A paint object is a graphics tool in android, its basically used for drawing and styling graphics on a canvas

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 16.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    
    private fun PointF.computeXYForSpeed(position: FanSpeed, radius: Float) {

        val startAngle = Math.PI * (9 / 9.0)
        val angle = startAngle + position.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }


    override fun onDraw(canvas: Canvas?) {
        paint.color = when(fanSpeed) {
            FanSpeed.OFF -> Color.CYAN
            FanSpeed.LOW -> lowColor
            FanSpeed.MEDIUM -> mediumColor
            FanSpeed.HIGH -> highColor
            FanSpeed.HIGHEST -> highestColor
        }


        //Draw the circle
        canvas?.drawCircle((width/2).toFloat(), (height/2).toFloat(), radius, paint)

        //draw the indicator
        val markerRadius = radius + RADIUS_OFFSET_INDICATION
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas?.drawCircle(pointPosition.x, pointPosition.y, radius/12, paint)

        //Draw text labels
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)

            val label = if(isTextMode) {
                resources.getString(i.labelWord)
            } else {
                resources.getString(i.labelNumber)
            }

            canvas?.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
        super.onDraw(canvas)
    }
    fun setTextMode(enabled: Boolean) {
        isTextMode = enabled
        invalidate()
    }
    fun getFanSpeed(): FanSpeed {
        return fanSpeed
    }

    fun updateToWords() {
        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.labelWord)

        invalidate()

    }

}   