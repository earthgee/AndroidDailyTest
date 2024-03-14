package com.earthgee.view.flipboard

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import com.earthgee.view.R

class FlipboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

        private val paint = Paint()
        private var bitmap: Bitmap
        private val camera = Camera()

    private var step1Degree = 0.0f
    private var step2Degree = 0.0f
    private var step3Degree = 0.0f

       init {
           val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlipboardView)
           val drawable = typedArray.getDrawable(R.styleable.FlipboardView_background)
                   as BitmapDrawable
          typedArray.recycle()

            bitmap = drawable.bitmap
           camera.setLocation(0f, 0f, -resources.displayMetrics.density * 15)
       }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


    }

    fun setStep1Degree(step1Degree: Float) {
        this.step1Degree = step1Degree
    }

    fun setStep2Ddegree(step2Degree: Float) {
        this.step2Degree = step2Degree
    }

    fun setStep3Degree(step3Degree: Float) {
        this.step3Degree = step3Degree
    }

    fun startAnimation() {
        val animator1 = ObjectAnimator.ofFloat(this, "step1Degree", 0f, -45f)
        animator1.duration = 1000
        animator1.startDelay = 500

        val animator2 = ObjectAnimator.ofFloat(this, "step2Degree", 0f, 270f)
        animator2.duration = 800
        animator2.startDelay = 500

        val animator3 = ObjectAnimator.ofFloat(this, "step3Degree", 0f, 30f)
        animator3.duration = 500
        animator3.startDelay = 500

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(animator1, animator2, animator3)
        animatorSet.start()
    }

}