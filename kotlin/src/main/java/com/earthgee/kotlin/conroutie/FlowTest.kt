package com.earthgee.kotlin.conroutie

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *  Created by zhaoruixuan1 on 2023/9/27
 *  CopyRight (c) haodf.com
 *  功能：
 */
fun foo(): Flow<Int> = flow {
    (1..3).forEach {
        delay(1000)
        emit(it)
    }
}

fun main() = runBlocking {
    launch {
        (1..3).forEach { 
            println("not block $it")
            delay(1000)
        }
    }
    foo().collect { value -> print(value) }
}