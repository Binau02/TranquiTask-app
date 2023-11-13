package com.example.tranquitaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.FriendsModel
import com.example.tranquitaskapp.data.LeaderboardModel

class LeaderboardRowAdapter(val data: List<LeaderboardModel>) :
    RecyclerView.Adapter<LeaderboardRowAdapter.MyViewHolder>() {
    class MyViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val imageView = row.findViewById<ImageView>(R.id.avatar)
        val pseudoView = row.findViewById<TextView>(R.id.pseudo)
        val coinView = row.findViewById<TextView>(R.id.coin_amount)
        val rankView = row.findViewById<TextView>(R.id.rank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MyViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(
                R.layout.leaderboard_row,
                parent, false
            )
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.pseudoView.text = data[position].pseudo
        holder.imageView.setImageResource(data[position].avatar)
        holder.coinView.text = data[position].coin
        holder.rankView.text = data[position].rank
    }

    override fun getItemCount(): Int = data.size
}