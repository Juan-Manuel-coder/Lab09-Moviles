package com.example.lab09

import com.google.gson.annotations.SerializedName
data class ProductModel(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("brand") val brand: String? = null, // Nuevo
    @SerializedName("category") val category: String? = null // Nuevo
)