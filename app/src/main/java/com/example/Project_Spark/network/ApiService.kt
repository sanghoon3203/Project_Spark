package com.example.Project_Spark.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    fun register(@Body request: RegisterRequest): Call<Void>
}

data class RegisterRequest(
    val email: String,
    val password: String
)
