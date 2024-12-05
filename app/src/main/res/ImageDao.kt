package com.example.roomdemo.imagestore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageModel: ImageModel)

    @Query("SELECT *FROM images")
    fun getAllImages() :LiveData<List<ImageModel>>

    @Query("DELETE FROM images") // Replace `image_table` with your actual table name
    suspend fun deleteAllImages()

}