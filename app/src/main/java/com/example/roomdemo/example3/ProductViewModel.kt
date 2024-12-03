package com.example.roomdemo.example3

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ProductViewModel"
    }
    fun fetchProducts() {
        viewModelScope.launch {
            try {
                repository.fetchAndStoreProducts()
                Log.d(TAG, "Products fetched and stored successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching products: ${e.message}", e)
            }
        }
    }

    suspend fun getProducts() = repository.getAllProducts()
}
