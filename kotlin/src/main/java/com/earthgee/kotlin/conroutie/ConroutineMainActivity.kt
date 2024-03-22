package com.earthgee.kotlin.conroutie

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.earthgee.kotlin.R
import com.earthgee.kotlin.databinding.ConroutineActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.IllegalStateException

class ConroutineMainActivity : AppCompatActivity(){

    private lateinit var binding : ConroutineActivityMainBinding

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        binding.textView.text = "exceptionHandler:$throwable"
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConroutineActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.Main + exceptionHandler) {
            try {
                val repo1 = async { userApi.listReposKt("earthgee") }
                val repo2 = async { userApi.listReposKt("alibaba") }
                binding.textView.text = repo1.await()[0].name + repo2.await()[0].name
            } catch (ex: Exception) {
                binding.textView.text = Log.getStackTraceString(ex)
            }
        }

    }

    //kksk select版
    private fun selectKkSk() {
        lifecycleScope.launch(Dispatchers.Main + exceptionHandler) {
            val localDeferred = getUserFromLocal(this)
            val remoteDeferred = getUserFromApi(this)

            val userResponse = select<List<UserApi.Repo>?> {
                localDeferred.onAwait { it }
                remoteDeferred.onAwait { it }
            }
        }
    }

    private fun getUserFromApi(coroutineScope: CoroutineScope) = coroutineScope.async(Dispatchers.IO) {
        userApi.listReposKt("earthgee")
    }

    private fun getUserFromLocal(coroutineScope: CoroutineScope) = coroutineScope.async(Dispatchers.IO) {
        File(externalCacheDir, "earthgee").takeIf { it.exists() }?.readText()?.let {
            Gson().fromJson(it, List::class.java)
        } as? List<UserApi.Repo>
    }

    //kksk flow版
    private fun flowKksk() {
        lifecycleScope.launch {
            listOf(::getUserFromApi, ::getUserFromLocal)
                .map {  function ->
                    function.call(this)
                }.map { deferred ->
                    flow {
                        emit(deferred.await())
                    }
                }.merge()
                .collect {
                    //get result
                }
        }
        
    }

}