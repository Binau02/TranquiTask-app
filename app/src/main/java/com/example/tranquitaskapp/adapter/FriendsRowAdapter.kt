package com.example.tranquitaskapp.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.FriendsModel

class FriendsRowAdapter(val data: List<FriendsModel>, val fragment : Fragment, val isFriend : Boolean = true) :
    RecyclerView.Adapter<FriendsRowAdapter.MyViewHolder>() {
    class MyViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val imageView: ImageView = row.findViewById(R.id.avatar)
        val pseudoView: TextView = row.findViewById(R.id.pseudo_row)
        val button1: TextView = row.findViewById(R.id.yes)
        val button2: TextView = row.findViewById(R.id.no)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MyViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(
                R.layout.friends_row,
                parent, false
            )
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.pseudoView.text = data[position].pseudo
//        holder.imageView.setImageResource(data[position].avatar)
        if (data[position].avatar != "") {
            Glide.with(fragment)
                .load(data[position].avatar)
                .into(holder.imageView)
        }
        else {
            holder.imageView.setImageResource(R.drawable.default_profil_picture)
        }
        if (!isFriend) {
            holder.button1.visibility = View.VISIBLE
            holder.button2.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = data.size
}