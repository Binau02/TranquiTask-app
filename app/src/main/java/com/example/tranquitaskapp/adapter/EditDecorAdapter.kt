package com.example.tranquitaskapp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.ItemModel
import com.example.tranquitaskapp.fragment.EditDecor

class EditDecorAdapter(val data: List<ItemModel>, val fragment : EditDecor, val id : Int) :
    RecyclerView.Adapter<EditDecorAdapter.MyViewHolder>() {
    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val imageView: ImageView = item.findViewById(R.id.image)
        val coinsView: RelativeLayout = item.findViewById(R.id.coins)
        val wholeItem: LinearLayout = item.findViewById(R.id.shop_item)
        val frame: RelativeLayout = item.findViewById(R.id.frame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(
                R.layout.shop_item,
                parent, false
            )
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (data[position].image != "") {
            Glide.with(fragment)
                .load(data[position].image)
                .into(holder.imageView)
        }
        else {
            holder.imageView.setImageResource(id)
        }
        if (data[position].bought) {
            holder.coinsView.visibility = View.INVISIBLE
        }
        holder.wholeItem.setOnClickListener {
            fragment.select(data[position], holder.frame)
        }
        holder.frame.setBackgroundResource(R.drawable.empty)
    }

    override fun getItemCount(): Int = data.size
}