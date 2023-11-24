package com.example.tranquitaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.LeaderboardModel

class ShopAdapter(val data: List<LeaderboardModel>, val fragment : Fragment) :
    RecyclerView.Adapter<ShopAdapter.MyViewHolder>() {
    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val imageView: ImageView = item.findViewById(R.id.image)
        val pseudoView: TextView = item.findViewById(R.id.tvcoin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MyViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(
                R.layout.shop_item,
                parent, false
            )
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.pseudoView.text = data[position].pseudo
        if (data[position].avatar != "") {
            Glide.with(fragment)
                .load(data[position].avatar)
                .into(holder.imageView)
        }
        else {
            holder.imageView.setImageResource(R.drawable.default_profil_picture)
        }
    }

    override fun getItemCount(): Int = data.size
}