package com.example.roomdemo.example3

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1)
abstract class AppDatabase :RoomDatabase() {
abstract fun productDao() :ProductDao
}