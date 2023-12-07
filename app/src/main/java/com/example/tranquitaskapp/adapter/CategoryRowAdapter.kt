package com.example.tranquitaskapp.adapter

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
import com.example.tranquitaskapp.data.CategoryDictionary
import com.example.tranquitaskapp.data.CategoryModel
import com.example.tranquitaskapp.data.ListTaskFilter
import com.example.tranquitaskapp.data.Period

class CategoryRowAdapter(
    val data: List<CategoryModel>,
    private val onCategoryClick: () -> Unit,
    private val isDay: Boolean
) :
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

        holder.nameView.text = data[position].name
        holder.imageView.setImageResource(imageResourceId)
        holder.imageView.setColorFilter(Color.BLACK)
        holder.progressBar.progress = data[position].progress
        holder.row.setOnClickListener{
            ListTaskFilter.category = CategoryDictionary.nameToDocumentReference[holder.nameView.text]
            if(isDay){
                ListTaskFilter.period = Period.DAY
            }else{
                ListTaskFilter.period = Period.WEEK
            }
            onCategoryClick()
            Log.d("TEST","Appuie sur la categorie "+holder.nameView.text)
        }
    }

    override fun getItemCount(): Int = data.size
}