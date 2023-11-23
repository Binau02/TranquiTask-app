package com.example.tranquitaskapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.CategoryModel

class CategoryRowAdapter(val data: List<CategoryModel>) :
    RecyclerView.Adapter<CategoryRowAdapter.MyViewHolder>() {
    class MyViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val imageView = row.findViewById<ImageView>(R.id.logoCategory)
        val nameView = row.findViewById<TextView>(R.id.nameCategory)
        val progressBar = row.findViewById<ProgressBar>(R.id.progressBarFront)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MyViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_row,
                parent, false
            )
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val packageName = holder.itemView.context.packageName // Nom du package de votre application
        val imageResourceName = data[position].logoResId // Exemple de nom de la ressource
        val imageResourceId = holder.itemView.context.resources.getIdentifier(imageResourceName, "drawable", packageName)

        val stringResourceName = data[position].name // Exemple de nom de la ressource
        val stringResourceId = holder.itemView.context.resources.getIdentifier(stringResourceName, "string", packageName)

//        holder.nameView.text = holder.itemView.context.getString(stringResourceId)
        holder.nameView.text = data[position].name
        holder.imageView.setImageResource(imageResourceId)
        holder.imageView.setColorFilter(Color.BLACK)
        holder.progressBar.progress = data[position].progress
        holder.row.setOnClickListener{
            Log.d("TEST","Appuie sur la categorie "+holder.nameView.text)
        }
    }

    override fun getItemCount(): Int = data.size
}