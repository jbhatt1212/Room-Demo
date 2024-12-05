package com.example.roomdemo.imagestore

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.roomdemo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val database = Room.databaseBuilder(
            applicationContext,
            ImageDatabase::class.java,
            "image_db"
        ).fallbackToDestructiveMigration()
            .build()

        val rvImage: RecyclerView = findViewById(R.id.rvImage)
        val imageAdapter = ImageAdapter()
        rvImage.adapter = imageAdapter

        val delete: Button = findViewById(R.id.btnDelete)
        delete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                database.imageDao.deleteAllImages()
                // Refresh the UI explicitly if necessary
                CoroutineScope(Dispatchers.Main).launch {
                    database.imageDao.getAllImages().observe(this@MainActivity) {
                        imageAdapter.submitList(it)
                    }
                }
            }
        }



        database.imageDao.getAllImages().observe(this) {
            imageAdapter.submitList(it)
        }

        val singleImagePicker: Button = findViewById(R.id.btnSingleIV)
        val singlePhotoPickerLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let { newImageUpload(database, it) }
            }

        singleImagePicker.setOnClickListener {
         singlePhotoPickerLauncher.launch(
             PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
         )
        }
        val multiplePhotoPickLauncher =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()){uris ->
                for (image in uris){
                    // single image  function
                    newImageUpload(database, image)
                }
            }
        val multipleImagePicker :Button = findViewById(R.id.btnMultipleIV)
        multipleImagePicker.setOnClickListener {
            multiplePhotoPickLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    private fun newImageUpload(database: ImageDatabase, imageUri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newImage = contentResolver.openInputStream(imageUri)?.readBytes()?.let {
                    ImageModel(
                        UUID.randomUUID().toString(),
                        it
                    )
                }
                newImage?.let {
                    database.imageDao.insertImage(it)
                }

            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

}