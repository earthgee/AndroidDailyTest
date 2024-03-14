package com.earthgee.ktx;

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

/**
 *
 */
internal fun View.updatePadding(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val l = left?.let { dp2px(it) } ?: paddingLeft
    val t = top?.let { dp2px(it) } ?: paddingTop
    val r = right?.let { dp2px(it) } ?: paddingRight
    val b = bottom?.let { dp2px(it) } ?: paddingBottom
    setPadding(l, t, r, b)
}

fun View.setPadding(padding: Int) {
    val p = dp2px(padding)
    setPadding(p, p, p, p)
}

fun View.updateMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    left?.let { lp.leftMargin = dp2px(it) }
    top?.let { lp.topMargin = dp2px(it) }
    right?.let { lp.rightMargin = dp2px(it) }
    bottom?.let { lp.bottomMargin = dp2px(it) }
}

fun View.setMargin(margin: Int) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    val m = dp2px(margin)
    lp.leftMargin = m
    lp.topMargin = m
    lp.rightMargin = m
    lp.bottomMargin = m
}

fun View.dp2px(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics).toInt()
}

fun View.dp2px(dp: Int): Int {
    return dp2px(dp.toFloat())
}
