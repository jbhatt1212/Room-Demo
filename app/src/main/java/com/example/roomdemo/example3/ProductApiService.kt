package com.example.roomdemo.example3

import retrofit2.http.GET

interface ProductApiService {
    @GET("products/category/smartphones")
    suspend fun getSmartphones(): ProductResponse
}

data class ProductResponse(
    val products: List<Product>
)


