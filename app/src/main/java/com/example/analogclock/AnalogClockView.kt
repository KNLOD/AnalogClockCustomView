package com.example.analogclock

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import java.util.*

class AnalogClockView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0 ) : View(context, attrs, defStyleAttr) {

    private var clockHeight: Int = 0
    private var clockWidth: Int = 0
    private var radius: Int = 0
    private var angle : Double = 0.0
    private var centreX: Int = 0
    private var centreY: Int = 0
    private var padding: Int = 0

    private lateinit var paint: Paint
    private lateinit var path: Path
    private lateinit var rect: Rect

    private lateinit var numbers: MutableList<Int>
    private var minimum: Int = 0

    private var hour: Float = 0F
    private var minute: Float = 0F
    private var second: Float = 0F

    private var hourHandSize: Int = 0
    private var handSize: Int = 0
    private var fontSize : Float = 0F

    private var isInit: Boolean = false

    private fun initClock() {
        clockHeight = height
        clockWidth = width
        Log.d("Clock size", "${clockHeight}, ${clockWidth}")
        padding = 50

        centreX = clockWidth/2
        centreY = clockHeight/2

        minimum = Math.min(clockHeight, clockWidth)
        radius = minimum/2 - padding

        fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13F, resources.displayMetrics)

        angle = Math.PI/30 - Math.PI/2

        paint = Paint()
        path = Path()
        rect = Rect()

        hourHandSize = radius - radius/2
        handSize = radius - radius/4

        numbers = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

        isInit = true


    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)

        initClock()

        drawRestrictingPath(canvas)
        drawCircle(canvas)
        drawHands(canvas)
        drawNumerals(canvas)
        drawPoints(canvas)

        postInvalidateDelayed(500)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clockHeight = h
        clockWidth = w
    }

    private fun drawRestrictingPath(canvas:Canvas){
        path = Path().apply{
            addCircle(centreX.toFloat(), centreY.toFloat(), radius.toFloat() + padding - 10, Path.Direction.CW)
        }
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, 6F)
        canvas.drawPath(path, paint)
        canvas.clipPath(path)
    }

    private fun drawCircle(canvas: Canvas){
        paint.reset()

        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, 20F)
        canvas.drawCircle(centreX.toFloat(), centreY.toFloat(), radius.toFloat() + padding - 10, paint)

        drawCircleShadow(canvas)
        drawCircleHighlight(canvas)




    }
    private fun drawCircleShadow(canvas: Canvas){
        paint.reset()
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, 60F)
        paint.maskFilter = BlurMaskFilter(50F, BlurMaskFilter.Blur.NORMAL)
        canvas.drawCircle(centreX.toFloat() + 30, centreY.toFloat(), radius.toFloat() + padding+10, paint)

    }

    private fun drawCircleHighlight(canvas: Canvas) {
        paint.reset()
        setPaintAttributes(Color.WHITE, Paint.Style.STROKE, 6F)
        paint.maskFilter = BlurMaskFilter(1F, BlurMaskFilter.Blur.NORMAL)
        canvas.drawCircle(
            centreX.toFloat() + 20,
            centreY.toFloat(),
            radius.toFloat() + padding + 10,
            paint
        )
    }


    private fun setPaintAttributes(color: Int, stroke: Paint.Style, strokeWidth: Float) {
        paint.color = color
        paint.style = stroke
        paint.strokeWidth = strokeWidth
        paint.isAntiAlias = true
    }

    private fun drawHands(canvas: Canvas){
        var calendar: Calendar = Calendar.getInstance()

        hour = calendar.get(Calendar.HOUR_OF_DAY).toFloat()
        hour = if (hour > 24) hour - 24 else hour

        minute = calendar.get(Calendar.MINUTE).toFloat()
        second = calendar.get(Calendar.SECOND).toFloat()

        drawHourHand(canvas, ((hour + (minute / 60F) )* 5F))
        drawMinuteHand(canvas, minute)
        drawSecondsHand(canvas, second)

    }

    private fun drawHourHand(canvas: Canvas, location: Float){
        paint.reset()
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, 10F)

        angle = Math.PI * location/ 30 - Math.PI / 2
        canvas.drawLine(centreX.toFloat(),
                        centreY.toFloat(),
                        (centreX + Math.cos(angle) * hourHandSize).toFloat(),
                        (centreY + Math.sin(angle) * hourHandSize).toFloat(),
                        paint)
        drawHandShadow(canvas, hourHandSize,3F, 10F)


    }
    private fun drawMinuteHand(canvas: Canvas, location: Float){
        paint.reset()
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, 6F)

        angle = Math.PI * location/ 30 - Math.PI / 2
        canvas.drawLine(centreX.toFloat(),
            centreY.toFloat(),
            (centreX + Math.cos(angle) * handSize).toFloat(),
            (centreY + Math.sin(angle) * handSize).toFloat(),
            paint)


        drawHandShadow(canvas, handSize, 3F, 10F)


    }
    private fun drawSecondsHand(canvas: Canvas, location: Float){
        paint.reset()
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, 3F)

        angle = Math.PI * location/ 30 - Math.PI / 2
        canvas.drawLine(centreX.toFloat(),
            centreY.toFloat(),
            (centreX + Math.cos(angle) * handSize).toFloat(),
            (centreY + Math.sin(angle) * handSize).toFloat(),
            paint)
        drawHandShadow(canvas, handSize, 3F, 10F)
    }
    private fun drawHandShadow(canvas: Canvas, handSize: Int, strokeWidth: Float, blurRadius: Float ){
        paint.reset()
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, strokeWidth)
        paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        val shadowDY = 10
        canvas.drawLine(centreX.toFloat(),
            centreY.toFloat() + shadowDY,
            (centreX + Math.cos(angle) * handSize ).toFloat(),
            (centreY + Math.sin(angle) * handSize + shadowDY).toFloat(),
            paint)

    }

    private fun drawNumerals(canvas: Canvas){
        paint.reset()
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, 3F)
        paint.textSize = fontSize
        paint.typeface = Typeface.create("Josefin", Typeface.NORMAL)

        for (number in numbers){
            var numStr: String = number.toString()
            paint.getTextBounds(numStr, 0, numStr.length, rect)

            var angleNum: Double = Math.PI / 6 * (number - 3)
            x = (centreX + Math.cos(angleNum) * (radius - padding/2) - (rect.width() / 2)).toFloat()
            y = (centreY + Math.sin(angleNum) * (radius - padding/2)  + rect.height()/2).toFloat()

            canvas.drawText(numStr, x, y, paint)

        }
    }

    private fun drawPoints(canvas: Canvas){

        paint.reset()
        val numDots = 59

        for (dot in 0..numDots){

            var angleDot: Double = Math.PI * dot/ 30 - Math.PI / 2
            x = (centreX + Math.cos(angleDot) * (radius)).toFloat()
            y = (centreY + Math.sin(angleDot) * (radius)).toFloat()

            setPaintAttributes(Color.BLACK, Paint.Style.FILL, 5f)
            canvas.drawCircle(x, y, 5f, paint)

        }

    }







}