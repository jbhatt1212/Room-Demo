package com.example.roomdemo.imagestore

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.R

class ImageAdapter :ListAdapter<ImageModel, ImageAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback :DiffUtil.ItemCallback<ImageModel>(){
        override fun areItemsTheSame(oldItem: ImageModel, newItem: ImageModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageModel, newItem: ImageModel): Boolean {
           return oldItem == newItem
        }
    }

    class ViewHolder(imageView: View) :RecyclerView.ViewHolder(imageView){
        private val image = itemView.findViewById<ImageView>(R.id.ivImage)
        fun bindDate(imageModel: ImageModel){
            val bitmap = BitmapFactory.decodeByteArray(imageModel.imageData,0,imageModel.imageData.size)
            image.setImageBitmap(bitmap)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_image_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageModel = getItem(position)
        holder.bindDate(imageModel)
    }
}