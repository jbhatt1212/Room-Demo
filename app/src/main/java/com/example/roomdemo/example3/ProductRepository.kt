package com.example.roomdemo.example3

import android.util.Log

class ProductRepository(
    private val apiService: ProductApiService,
    private val productDao: ProductDao
) {
    companion object {
        private const val TAG = "ProductRepository"
    }
    suspend fun fetchAndStoreProducts() {
        try {
            val response = apiService.getSmartphones()

            Log.d(TAG, "API Response: ${response.products}")

            productDao.insertProducts(response.products)


            Log.d(TAG, "Products succe ssfully saved to database")
        } catch (e: Exception) {

            Log.e(TAG, "Error fetching products: ${e.message}", e)
        }
    }


    suspend fun getAllProducts(): List<Product> = productDao.getAllProducts()
}
