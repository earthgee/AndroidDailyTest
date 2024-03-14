package com.earthgee.view.sportview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.Log
import android.view.View
import com.earthgee.ktx.dp2px
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

/**
 *  Created by zhaoruixuan1 on 2024/3/12
 *  CopyRight (c) haodf.com
 *  功能：粒子效果
 */
class FireworksCircleGraphics(context: Context, view: View) {

    companion object {
        //圆环旋转速率
        const val ROTATE_RATE = 3

        const val RADIUS_SCALE = 0.32f

        //半径偏离范围最大值
        const val LINE_MAX_DXY = 4

        //圆心偏离范围最大值
        const val LINE_MAX_DR = 4

        //线条数目
        const val LINE_AMOUNT = 15
        const val LINE_DEGREE = 345
        const val LINE_SIZE = 0.5f

        const val LINE_MAX_CHANGE_RATE = 0.015f
        const val LINE_DECAY_RATE = LINE_MAX_CHANGE_RATE / 180

        //粒子
        const val STAR_AMOUNT = 30
        const val STAR_SIZE = 8f
        const val STAR_MAX_VX = 2.5f
        const val STAR_MAX_VY = 2.5f

        //速度衰减速率
        const val STAR_DECAY_RATE = 0.003f

        //速度衰减常量
        const val STAR_DECAY_RATE_CONST = 0.001f

        //消失临界距离
        const val STAR_DISAPPEAR_DISTANCE = 60f

        //消失临界亮度
        const val STAR_DISAPPEAR_ALPHA = 0.05f
    }

    //半径偏离范围最大值
    private var lineMaxDxy = 0.0f

    //圆心偏离范围最大值
    private var lineMaxDr = 0.0f

    /** 线条弧长变化速率范围（绝对值）px **/
    private var lineMaxChangeRate = 0.0f

    /** 线条弧长变化速率衰减速率 px **/
    private var lineDecayRate = 0.0f

    private val linePaint = Paint()

    private var colorWhite = 0xffffffff.toInt()
    private var colorTransparent = 0x00000000.toInt()

    private var rotateDegree = -90

    private val lineRectF = RectF(0f, 0f, 0f, 0f)

    var mWidth = 0
    var mHeight = 0
    private var circleX = 0.0f
    private var circleY = 0.0f

    private var starMaxVx = 0.0f
    private var starMaxVy = 0.0f
    private var starDecayRate = 0.0f
    private var starDecayRateConst = 0.0f
    private var starDisappearDistance = 0.0f

    private val starPaint = Paint()

    private var lineArgumentList = mutableListOf<LineArgument>()
    private var starArgumentList = mutableListOf<StarArgument>()

    private var isFirst = true

    init {
        view.apply {
            lineMaxDxy = dp2px(LINE_MAX_DXY).toFloat()
            lineMaxDr = dp2px(LINE_MAX_DR).toFloat()
            lineMaxChangeRate = dp2px(LINE_MAX_CHANGE_RATE).toFloat()
            lineDecayRate = dp2px(LINE_DECAY_RATE).toFloat()

            linePaint.style = Paint.Style.STROKE
            linePaint.strokeCap = Paint.Cap.ROUND
            linePaint.strokeWidth = dp2px(LINE_SIZE).toFloat()

            val starSize = dp2px(STAR_SIZE)
            starMaxVx = dp2px(STAR_MAX_VX).toFloat()
            starMaxVy = dp2px(STAR_MAX_VY).toFloat()
            starDecayRate = dp2px(STAR_DECAY_RATE).toFloat()
            starDecayRateConst = dp2px(STAR_DECAY_RATE_CONST).toFloat()
            starDisappearDistance = dp2px(STAR_DISAPPEAR_DISTANCE).toFloat()

            starPaint.strokeWidth = starSize.toFloat()
            starPaint.strokeCap = Paint.Cap.ROUND
            starPaint.isAntiAlias = false

            repeat(LINE_AMOUNT) {
                lineArgumentList.add(
                    LineArgument(
                        lineMaxDr.nextSignedFloat(),
                        lineMaxDr.nextSignedFloat(),
                        lineMaxDxy.nextSignedFloat()
                    )
                )
            }

            repeat(STAR_AMOUNT) {
                starArgumentList.add(
                    StarArgument(
                        0f,
                        0f,
                        starMaxVx.nextSignedFloat(),
                        starMaxVy.nextSignedFloat(),
                        0f,
                        0f,
                        1f,
                        starMaxVx,
                        starMaxVy
                    )
                )
            }
        }
    }

    private fun resetParams() {
        circleX = mWidth * SportView.START_CIRCLE_X_SCALE
        circleY = mHeight * SportView.START_CIRCLE_Y_SCALE
        val lineSweepGradient = SweepGradient(circleX, circleY, colorTransparent, colorWhite)
        linePaint.shader = lineSweepGradient
    }

    fun draw(canvas: Canvas) {
        canvas.save()

        if (isFirst) {
            resetParams()
            isFirst = false
        }

        val radius = mWidth * RADIUS_SCALE
        canvas.rotate(rotateDegree.toFloat(), circleX, circleY)

        lineArgumentList.forEach {
            val dx = it.dx
            val dy = it.dy
            val dr = it.dr

            lineRectF.set(
                circleX - radius - dr - dx,
                circleY - radius - dr - dy,
                circleX + radius + dr + dx,
                circleY + radius + dr + dy
            )
            val dAngle = -Math.min(360 - LINE_DEGREE, dx.toInt())
            canvas.drawArc(
                lineRectF,
                (360 - LINE_DEGREE + dAngle).toFloat(),
                LINE_DEGREE.toFloat(),
                false,
                linePaint
            )
        }

        starArgumentList.forEach {
            val dx = it.dx
            val dy = it.dy
            val alphaMask = (it.alpha * 256).toInt() shl 24
            val transparentColor = (colorWhite and 0x00ffffff) or alphaMask
            starPaint.color = transparentColor
            canvas.drawPoint(circleX + radius + dx, circleY + dy, starPaint)
        }
        canvas.restore()
    }

    fun nextFrame() {
        rotateDegree = (rotateDegree + ROTATE_RATE) % 360
        starArgumentList.forEach {
            it.next(starDecayRate, starDecayRateConst, starDisappearDistance)
        }
    }

    data class LineArgument(
        var dx: Float = 0f,
        var dy: Float = 0f,
        var dr: Float = 0f
    )

    data class StarArgument(
        var dx: Float = 0f, //原点x轴偏移
        var dy: Float = 0f, //原点y轴偏移
        var vx: Float = 0f, //x轴速度
        var vy: Float = 0f, //y轴速度
        var ax: Float = 0f, //x轴加速度
        var ay: Float = 0f, //y轴加速度
        var alpha: Float = 0f,
        var starMaxVx: Float = 0f,
        var starMaxVy: Float = 0f
    ) {
        fun next(starDeacyRate: Float, starDeacyConst: Float, starDisappearDistance: Float) {
            ax += -(vx * abs(vx) * starDeacyRate - starDeacyConst)
            ay += -(vy * abs(vy) * starDeacyRate - starDeacyConst)
            if(ax < 0) {
                ax = 0f
            }
            if(ay < 0) {
                ay = 0f
            }

            dx += vx/2
            vx += ax
            dx += vx/2

            dy += vy/2
            vy += ay
            dy += vy/2

            alpha = 1 - sqrt(dx * dx + dy * dy) / starDisappearDistance

            if(alpha < STAR_DISAPPEAR_ALPHA) {
                reset()
            }
        }

        private fun reset() {
            dx = 0f
            dy = 0f
            vx = starMaxVx.nextSignedFloat()
            vy = -abs(starMaxVy.nextSignedFloat())
            ax = 0f
            ay = 0f
            alpha = 1f
        }

    }

}

internal fun Float.nextSignedFloat(): Float {
    return this * (if (Random.nextBoolean()) 1 else -1) * Random.nextFloat()
}