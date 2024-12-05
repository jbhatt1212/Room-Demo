package com.example.roomdemo.imagestore

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageModel(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "imageData", typeAffinity = ColumnInfo.BLOB)
    val imageData: ByteArray
)
