package com.example.lab09

import retrofit2.http.GET

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(): ProductResponse
}

data class ProductResponse(
    val products: List<ProductModel>
)