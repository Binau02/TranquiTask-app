package com.example.tranquitaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.LeaderboardModel
import com.example.tranquitaskapp.firebase.MyFirebase
import com.google.firebase.firestore.DocumentReference

val db = MyFirebase.getFirestoreInstance()

class LeaderboardRowAdapter(val data: List<LeaderboardModel>, val fragment : Fragment, val goToUserRef: (DocumentReference) -> Unit) :
    RecyclerView.Adapter<LeaderboardRowAdapter.MyViewHolder>() {

    class MyViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val imageView: ImageView = row.findViewById(R.id.avatar)
        val pseudoView: TextView = row.findViewById(R.id.pseudo)
        val coinView: TextView = row.findViewById(R.id.coin_amount)
        val rankView: TextView = row.findViewById(R.id.rank)
        val card = row.findViewById<RelativeLayout>(R.id.displayed_profile)
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
        holder.card.setOnClickListener{
            db.collection("user").whereEqualTo("username", data[position].pseudo)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty){
                        for (document in documents){
                            goToUserRef(document.reference)
                        }
                    }
                }
        }
        holder.pseudoView.text = data[position].pseudo
        if (data[position].avatar != "") {
            Glide.with(fragment)
                .load(data[position].avatar)
                .into(holder.imageView)
        }
        else {
            holder.imageView.setImageResource(R.drawable.default_profil_picture)
        }
        holder.coinView.text = data[position].coin.toString()
        holder.rankView.text = data[position].rank
    }

    override fun getItemCount(): Int = data.size
}