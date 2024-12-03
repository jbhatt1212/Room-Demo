package com.example.roomdemo.example3

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.roomdemo.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val database= RetrofitInstance.provideDatabase(this)
        val apiService = RetrofitInstance.provideRetrofit()
        val repository = ProductRepository(apiService ,database.productDao())
        productViewModel = ProductViewModel(repository)
        productViewModel.fetchProducts()

        lifecycleScope.launch {
            val products = productViewModel.getProducts()
            Log.e("success","$products")
        }
    }
}

