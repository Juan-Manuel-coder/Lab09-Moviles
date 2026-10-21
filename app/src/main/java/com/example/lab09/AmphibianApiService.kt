package com.example.lab09

import retrofit2.http.GET

interface AmphibianApiService {
    @GET("amphibians")
    suspend fun getAmphibians(): List<AmphibianModel>
}