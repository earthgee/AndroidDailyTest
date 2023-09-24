package com.earthgee.kotlin.conroutie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.earthgee.kotlin.R
import com.earthgee.kotlin.databinding.ConroutineActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConroutineMainActivity : AppCompatActivity(){

    private lateinit var binding : ConroutineActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConroutineActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit=Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api=retrofit.create(UserApi::class.java)

        lifecycleScope.launch(Dispatchers.Main) {
            val repo1 = async { api.listReposKt("earthgee") }
            val repo2 = async { api.listReposKt("alibaba") }
            binding.textView.text = repo1.await()[0].name + repo2.await()[0].name
        }

    }



}