package com.earthgee.kotlin.conroutie

import kotlinx.coroutines.*

/**
 *  Created by zhaoruixuan1 on 2023/9/25
 *  CopyRight (c) haodf.com
 *  功能：
 */
class KotlinLittleTrash {

    var counter = 0
        set(value) {
            if(value >= 3) {
                field = value
            }
        }

    fun main() = testAsync()

    private fun testJobJoin() = runBlocking {
        val job = GlobalScope.launch(Dispatchers.Main) {
            delay(1000L)
            println("World,${Thread.currentThread().name}")
        }
        println("Hello,${Thread.currentThread().name}")
        job.join()
    }

    private fun testAsync() = runBlocking {
        val deferred = async {
            loadData()
        }
        val result = deferred.await()
    }

    private suspend fun loadData(): Int {
        delay(1000L)
        println("loaded!${Thread.currentThread().name}")
        return 42
    }

}