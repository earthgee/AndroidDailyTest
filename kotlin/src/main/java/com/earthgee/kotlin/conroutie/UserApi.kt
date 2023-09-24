package com.earthgee.kotlin.conroutie

import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("users/{user}/repos")
    suspend fun listReposKt(@Path("user") user: String) : List<Repo>

    data class Repo(val name: String = "")

}