package com.earthgee.kotlin.conroutie

import kotlinx.coroutines.*

/**
 *  Created by zhaoruixuan1 on 2023/9/27
 *  test
 *  功能：
 */
fun main() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("CoroutineExceptionHandler get $throwable")
    }
    val scope = CoroutineScope(Job() + exceptionHandler)

    scope.launch {
        delay(1000)
    }.invokeOnCompletion { cause: Throwable? ->
        if(cause is CancellationException) {
            println("Coroutine 1 get cancelled")
        }
    }

    scope.launch {
        delay(100)
        throw RuntimeException()
    }

    Thread.sleep(2000)
}