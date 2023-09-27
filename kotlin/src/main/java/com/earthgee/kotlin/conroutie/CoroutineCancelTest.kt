package com.earthgee.kotlin.conroutie

import kotlinx.coroutines.*

/**
 *  Created by zhaoruixuan1 on 2023/9/27
 *  CopyRight (c) haodf.com
 *  功能：
 */
fun main() = testCancelAndCatching()

fun testCancelAndCatching() = runBlocking {
    val job = launch(Dispatchers.Default) {
        println("before")
        suspendRunCatching {
            var i = 0
            (1..10).forEach {
                println("doJob,${i++}")
                delay(500)
            }
        }
        println("after")
    }

    delay(1300L)
    println("quiting")
    job.cancelAndJoin()
    println("quit success")
}

private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Result.failure(exception)
}

fun testCancelCheck() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("loop time,${i++}")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L)
    println("quiting")
    job.cancelAndJoin()
    println("quit success")
}