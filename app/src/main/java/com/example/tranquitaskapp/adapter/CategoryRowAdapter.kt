package com.example.tranquitaskapp.adapter

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
        holder.nameView.text = data[position].name
        holder.imageView.setImageResource(data[position].logoResId)
        holder.progressBar.progress = data[position].progress
    }

    override fun getItemCount(): Int = data.size
}