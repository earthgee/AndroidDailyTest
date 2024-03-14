package com.earthgee.view.sportview

import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposePathEffect
import android.graphics.CornerPathEffect
import android.graphics.DashPathEffect
import android.graphics.DiscretePathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.alpha
import com.earthgee.ktx.dp2px
import com.earthgee.view.R
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 *  Created by zhaoruixuan1 on 2024/3/12
 *  CopyRight (c) haodf.com
 *  功能：
 */
class SportView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    companion object {
        const val MAIN_TITLE_FONT_SIZE_DP = 64
        const val SUB_TITLE_FONT_SIZE_DP = 14
        const val SUB_TITLE_FONT_OFFSET_DP = 50

        //圆环宽度
        const val BIG_CIRCLE_SIZE = 16

        //圆环半径比例
        const val BIG_CIRCLE_RADIUS_SCALE = 0.38f

        //光晕大小
        const val CIRCLE_BLUR_SIZE = 16

        //圆环抖动效果半径
        const val BIG_CIRCLE_SHAKE_RADIUS = 20

        //圆环抖动效果偏移
        const val BIG_CIRCLE_SHAKE_OFFSET = 0.4f

        //圆环旋转速度
        const val BIG_CIRCLE_ROTATE_SPEED = 0.5f

        //虚线、实线与Canvas宽度之比
        const val DOTTED_SOLID_CIRCLE_SIZE = 0.32f

        //圆环光晕效果层数
        const val CIRCLE_BLUR_LAYER_AMOUNT = 4

        //虚线画笔大小
        const val DOTTED_CIRCLE_WIDTH = 2f

        //虚线间隔大小
        const val DOTTED_CIRCLE_GAG = 1f

        //实线画笔大小
        const val SOLID_CIRCLE_WIDTH = 2f

        //实线头圆点大小
        const val DOT_SIZE = 8f

        const val START_CIRCLE_X_SCALE = 0.5f
        const val START_CIRCLE_Y_SCALE = 0.5f
    }

    //主标题画笔
    private val mainTitlePaint = Paint()

    //副标题画笔
    private val subTitlePaint = Paint()

    //圆环画笔
    private val bigCirclePaint = Paint()

    //光晕画笔
    private val blurPaint = Paint()

    //虚线画笔
    private val dottedCirclePaint = Paint()

    //实线画笔
    private val solidCirclePaint = Paint()

    //点画笔
    private val dotPaint = Paint()

    //圆心变化控制变量
    private var circleOffsetY = 0.0f

    //圆半径控制变量
    private var circleRadiusIncrement = 0.0f

    //主标题y轴偏移
    private var mainTitleOffsetY = 0.0f

    //副标题x轴偏移
    private var subTitleOffsetX = 0.0f

    //副标题y轴偏移
    private var subTitleOffsetY = 0.0f

    //x轴旋转角度
    private var rotate3Degree = 0.0f
    private var textAlpha = 0xff

    private val camera = Camera()

    //圆环颜色
    private var circleColor = 0
    private var mainTitleString = "8888"
    private var subTitleString = "1.7 km | 34 kcal"
    private var subTitleSeperator = "|"
    private var blurSize = 0.0f

    private val blurOvalRectF = RectF()
    private val solidCircleRectF = RectF()

    //圆环从透明到实体的显示进度
    private var circleAlphaProgress = 0.0f

    //粒子效果
    private var fireworksCircle = FireworksCircleGraphics(context, this)

    //动画线程
    private val animationThread = AnimationThread()

    private var animationState = AnimationState.LOADING

    private var needRefreshText = true

    private var mWidth = 0
    private var mHeight = 0
    private var circleX = 0.0f
    private var circleY = 0.0f
    private var degree = 0.0f

    private var colorWhite = 0xffffffff.toInt()
    private var colorWhiteTransparent = 0x88ffffff.toInt()
    private var colorTransparent = 0x00000000.toInt()

    init {
        //主标题
        mainTitlePaint.apply {
            color = resources.getColor(R.color.color_white)
            //x轴居中
            textAlign = Paint.Align.CENTER
            textSize = dp2px(MAIN_TITLE_FONT_SIZE_DP).toFloat()
            mainTitleOffsetY =
                -(mainTitlePaint.fontMetrics.ascent + mainTitlePaint.fontMetrics.descent) / 2
        }

        //副标题
        subTitlePaint.apply {
            circleColor = 0x88ffffff.toInt()
            color = circleColor
            textSize = dp2px(SUB_TITLE_FONT_SIZE_DP).toFloat()
            subTitleOffsetY = dp2px(SUB_TITLE_FONT_OFFSET_DP).toFloat()
        }

        //圆环及光晕
        bigCirclePaint.apply {
            strokeWidth = dp2px(BIG_CIRCLE_SIZE).toFloat()
            style = Paint.Style.STROKE

            blurSize = dp2px(CIRCLE_BLUR_SIZE).toFloat()

//            val pathEffect1 = CornerPathEffect(dp2px(BIG_CIRCLE_SHAKE_RADIUS).toFloat())
//            val pathEffect2 = DiscretePathEffect(
//                dp2px(BIG_CIRCLE_SHAKE_RADIUS).toFloat(),
//                dp2px(BIG_CIRCLE_SHAKE_OFFSET).toFloat()
//            )
//            val composePathEffect = ComposePathEffect(pathEffect1, pathEffect2)
//            pathEffect = composePathEffect
        }

        blurPaint.apply {
            strokeWidth = dp2px(BIG_CIRCLE_SIZE).toFloat()
            style = Paint.Style.STROKE
        }

        //虚线
        dottedCirclePaint.apply {
            strokeWidth = dp2px(DOTTED_CIRCLE_WIDTH).toFloat()
            color = 0x88ffffff.toInt()
            style = Paint.Style.STROKE
            val gagPx = dp2px(DOTTED_CIRCLE_GAG).toFloat()
            pathEffect = DashPathEffect(floatArrayOf(gagPx, gagPx), 0f)
        }

        //实线
        solidCirclePaint.apply {
            strokeWidth = dp2px(SOLID_CIRCLE_WIDTH).toFloat()
            color = resources.getColor(R.color.color_white)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        //圆点
        dotPaint.apply {
            strokeWidth = dp2px(DOT_SIZE).toFloat()
            strokeCap = Paint.Cap.ROUND
            color = resources.getColor(R.color.color_white)
        }

        camera.setLocation(0f, 0f, resources.displayMetrics.density * 15)

        animationThread.start()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        super.onDraw(canvas)
        canvas.drawColor(Color.BLUE)

        resetDataIfNeeded(canvas)

        canvas.save()
        canvas.translate(0f, mWidth * circleOffsetY)
        when (animationState) {
            AnimationState.LOADING -> {
                drawFireworks(canvas)
                drawText(canvas)
            }

            AnimationState.UP1, AnimationState.DOWN1 -> {
                drawText(canvas)
                drawBigCircle(canvas)
            }

            AnimationState.ROTATE -> {
                canvas.save()
                rotateView(canvas)
                drawText(canvas)
                drawBigCircle(canvas)
                canvas.restore()
            }

            AnimationState.FINISH, AnimationState.DISCONNECT -> {
                drawText(canvas)
                drawBigCircle(canvas)
                drawProgressCircle(canvas)
            }
        }
        canvas.restore()

        invalidate()
    }

    //粒子圆环
    private fun drawFireworks(canvas: Canvas) {
        fireworksCircle.draw(canvas)
    }

    //文本
    private fun drawText(canvas: Canvas) {
        mainTitlePaint.alpha = textAlpha
        subTitlePaint.alpha = textAlpha
        canvas.drawText(mainTitleString, circleX, circleY + mainTitleOffsetY, mainTitlePaint)
        canvas.drawText(
            subTitleString,
            circleX + subTitleOffsetX,
            circleY + subTitleOffsetY,
            subTitlePaint
        )
    }

    //大圆环
    private fun drawBigCircle(canvas: Canvas) {
        val bigCircleRadius = width * BIG_CIRCLE_RADIUS_SCALE
        canvas.save()
        canvas.scale(
            1 + circleRadiusIncrement / BIG_CIRCLE_RADIUS_SCALE,
            1 + circleRadiusIncrement / BIG_CIRCLE_RADIUS_SCALE,
            circleX,
            circleY
        )
        canvas.rotate(degree, circleX, circleY)

        //光晕
//        blurPaint.alpha = (circleColor.alpha * circleAlphaProgress).toInt()
        (0 until CIRCLE_BLUR_LAYER_AMOUNT).forEach { index ->
            blurPaint.alpha =
                (0xff * (CIRCLE_BLUR_LAYER_AMOUNT - index) / (CIRCLE_BLUR_LAYER_AMOUNT * 3))
            blurOvalRectF.set(
                circleX - bigCircleRadius,
                circleY - bigCircleRadius,
                circleX + bigCircleRadius + index * blurSize / CIRCLE_BLUR_LAYER_AMOUNT,
                circleY + bigCircleRadius
            )
            canvas.drawOval(blurOvalRectF, blurPaint)
        }

        bigCirclePaint.alpha = (0xff * circleAlphaProgress).toInt()
        canvas.drawCircle(circleX, circleY, bigCircleRadius, bigCirclePaint)
        canvas.restore()
    }

    //内圆环
    private fun drawProgressCircle(canvas: Canvas) {
        val dottedCircleRadius = width * DOTTED_SOLID_CIRCLE_SIZE
        solidCircleRectF.set(
            circleX - dottedCircleRadius,
            circleY - dottedCircleRadius,
            circleX + dottedCircleRadius,
            circleY + dottedCircleRadius
        )
        canvas.drawCircle(circleX, circleY, dottedCircleRadius, dottedCirclePaint)
        canvas.drawArc(solidCircleRectF, -90f, 270f, false, solidCirclePaint)
    }

    //x轴旋转
    private fun rotateView(canvas: Canvas) {
        camera.save()
        camera.rotateX(rotate3Degree)
        canvas.translate(circleX, circleY)
        camera.applyToCanvas(canvas)
        canvas.translate(-circleX, -circleY)
        camera.restore()
    }

    private fun resetDataIfNeeded(canvas: Canvas) {
        if (needRefreshText) {
            refreshText()
            needRefreshText = false

            //初始化动画参数
            mWidth = measuredWidth
            mHeight = measuredHeight
            fireworksCircle.mWidth = measuredWidth
            fireworksCircle.mHeight = measuredHeight
            circleX = width * START_CIRCLE_X_SCALE
            circleY = height * START_CIRCLE_Y_SCALE

            val bigCircleRadius = width * BIG_CIRCLE_RADIUS_SCALE
            val bigCircleLinearGraident = LinearGradient(
                circleX - bigCircleRadius,
                circleY,
                circleX + bigCircleRadius,
                circleY,
                colorWhiteTransparent, colorWhite,
                Shader.TileMode.CLAMP
            )
            bigCirclePaint.setShader(bigCircleLinearGraident)

            val blurLinearGradient = LinearGradient(
                circleX - bigCircleRadius,
                circleY,
                circleX + bigCircleRadius,
                circleY,
                colorTransparent,
                colorWhite,
                Shader.TileMode.CLAMP
            )
            blurPaint.setShader(blurLinearGradient)
        }
    }

    private fun refreshText() {
        //副标题居中
        val indexBefore =
            subTitlePaint.measureText(subTitleString, 0, subTitleString.indexOf(subTitleSeperator))
        val indexAfter =
            subTitlePaint.measureText(
                subTitleString,
                0,
                subTitleString.indexOf(subTitleSeperator) + 1
            )
        subTitleOffsetX = -(indexBefore + indexAfter) / 2
    }

    enum class AnimationState {
        LOADING, UP1, DOWN1, ROTATE, FINISH, DISCONNECT;

        fun nextState() = when (this) {
            LOADING -> UP1
            UP1 -> DOWN1
            DOWN1 -> ROTATE
            ROTATE -> FINISH
            FINISH -> DISCONNECT
            DISCONNECT -> LOADING
        }

    }

    //动画控制线程
    inner class AnimationThread : Thread() {
        private val INTERNAL_MILL = 17
        private val LOADING_TIME_MILL = 3000
        private val UP1_TIME_MILL = 250
        private val DOWN1_TIME_MILL = UP1_TIME_MILL
        private val ROTATE_TIME_MILL = 5000
        private val FINISH_TIME_MILL = -INTERNAL_MILL
        private val DISCONNECT_TIME_MILL = 200

        //圆环淡入淡出时间
        private val APPEAR_MILLIS = 10 * INTERNAL_MILL

        //up1 阶段高度变化百分比
        private val UP1_SCALE = -0.05f

        //up2 阶段高度变化百分比
        private val DOWN1_SCALE = -UP1_SCALE

        private val animationTimeArray = intArrayOf(
            LOADING_TIME_MILL,
            UP1_TIME_MILL,
            DOWN1_TIME_MILL,
            ROTATE_TIME_MILL,
            FINISH_TIME_MILL,
            DISCONNECT_TIME_MILL
        )

        override fun run() {
            var index = 0
            while (true) {
                val durationTime = animationTimeArray[index]
                index = (index + 1) % animationTimeArray.size

                //当前阶段动画帧数
                val times = durationTime / INTERNAL_MILL
                //当前帧数
                var count = 0

                while (times < 0 || count++ < times) {
                    val startTime = System.currentTimeMillis()
                    when (animationState) {
                        AnimationState.LOADING -> {
                            fireworksCircle.nextFrame()
                        }

                        AnimationState.UP1 -> {
                            if (count <= APPEAR_MILLIS / INTERNAL_MILL) {
                                circleAlphaProgress =
                                    count / (APPEAR_MILLIS.toFloat() / INTERNAL_MILL.toFloat())
                            }
                            circleOffsetY =
                                (UP1_SCALE * sin(count.toFloat() / times * PI / 2)).toFloat()
                            circleRadiusIncrement = -circleOffsetY
                        }

                        AnimationState.DOWN1 -> {
                            circleOffsetY =
                                (DOWN1_SCALE * DOWN1_SCALE * -sin(PI / 2 + count.toFloat() / times * PI / 2)).toFloat()
                            circleRadiusIncrement = -circleOffsetY
                        }

                        AnimationState.ROTATE -> {
                            rotate3Degree = -count.toFloat() / times * 360
                            textAlpha = (0xff * abs(sin(PI /2 + count.toFloat() / times * PI))).toInt()
                        }

                        AnimationState.FINISH -> {
                            //none
                        }

                        else -> {
                            //none
                        }
                    }

                    degree = (degree + BIG_CIRCLE_ROTATE_SPEED) % 360
                    val usedTime = System.currentTimeMillis() - startTime
                    try {
                        if (usedTime > INTERNAL_MILL) {
                            continue
                        }

                        Thread.sleep(INTERNAL_MILL - usedTime)
                    } catch (ex: InterruptedException) {
                        //none
                    }
                }

                animationState = animationState.nextState()
            }
        }

    }

}