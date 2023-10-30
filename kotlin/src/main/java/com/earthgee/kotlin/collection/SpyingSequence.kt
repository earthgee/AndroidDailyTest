package com.earthgee.kotlin.collection

import android.util.Log

/**
 *  Created by zhaoruixuan1 on 2023/9/22
 *  test
 *  功能：
 */
class SpyingSequence<T>(private val underlyingSequence: Sequence<T>) : Sequence<T>{

    override fun iterator() = object : Iterator<T> {
        val underlyingIterator = underlyingSequence.iterator()

        override fun hasNext(): Boolean = underlyingIterator.hasNext()

        override fun next(): T {
            val item = underlyingIterator.next()
            Log.d("earthgee", "spy: $item")
            return item
        }

    }

}

fun <T> Sequence<T>.spy() = SpyingSequence(this)