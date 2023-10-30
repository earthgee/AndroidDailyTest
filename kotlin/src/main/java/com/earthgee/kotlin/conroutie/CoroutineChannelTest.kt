package com.earthgee.kotlin.conroutie

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *  Created by zhaoruixuan1 on 2023/9/26
 *  功能：
 */
fun main(vararg: Array<String>) = runBlocking {
    println("thread:${Thread.currentThread().name}")
    val channel = Channel<Int>()
    launch {
        println("thread:${Thread.currentThread().name}")
        for (x in 1..5) channel.send(x)
        channel.close()
    }
    for(y in channel) println(y)
    println("done!thread=${Thread.currentThread().name}")
}