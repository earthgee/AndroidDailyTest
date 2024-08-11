package com.earthgee.kotlin.delegate

class DelegateWrapped(var x: Boolean) {
    val z = 10L
    fun setY(y: Int) {

    }

    fun getY() = 12
}

class DelegateWrapper {

    private val wrapped = DelegateWrapped(false)

    var x by wrapped::x.delegator(false)
    val z by wrapped::z.delegator()
    val zz by DelegateWrapped::z.delegator(wrapped)
    val xx by DelegateWrapped::x.delegatorLazy {
        wrapped
    }

    val yGetter by wrapped::getY.delegator()

}