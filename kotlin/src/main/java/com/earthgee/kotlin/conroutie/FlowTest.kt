package com.earthgee.kotlin.conroutie

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 *  Created by zhaoruixuan1 on 2023/9/27
 *  test
 *  功能：
 */
fun foo(): Flow<Int> = flow {
    (1..3).forEach {
        delay(1000)
        emit(it)
    }
}

//fun main() = runBlocking {
//    launch {
//        (1..3).forEach {
//            println("not block $it")
//            delay(1000)
//        }
//    }
//    foo().collect { value -> print(value) }
//}

fun testCancel() = runBlocking {
    val cancelJob = launch {
        repeat(1000) {
            if(!isActive) {
                return@repeat
            }

            println("running")
        }
    }

    delay(1)
    println("Cancel")
    cancelJob.cancel()
    println("Done")
}

//suspend fun testCatchException() = CoroutineScope() {
//
//}