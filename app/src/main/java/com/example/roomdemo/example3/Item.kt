package com.example.roomdemo.example3

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Item(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)


@Entity(tableName = "product")
data class Product(
    @PrimaryKey val id: Int,
    val brand: String,
    val category: String,
    val description: String,
    val title: String
)