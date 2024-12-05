package com.example.roomdemo.imagestore

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ImageModel::class], version = 1, exportSchema = false)
abstract class ImageDatabase : RoomDatabase() {

    abstract val imageDao: ImageDao

}