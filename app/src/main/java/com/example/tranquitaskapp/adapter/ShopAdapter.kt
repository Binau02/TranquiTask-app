package com.example.tranquitaskapp.adapter

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.ItemModel
import com.example.tranquitaskapp.data.LeaderboardModel
import com.bumptech.glide.request.target.Target
import java.io.File

class ShopAdapter(val data: List<ItemModel>, val fragment : Fragment) :
    RecyclerView.Adapter<ShopAdapter.MyViewHolder>() {
    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val imageView: ImageView = item.findViewById(R.id.image)
        val coinsView: RelativeLayout = item.findViewById(R.id.coins)
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
        Glide.with(fragment)
//            .load(data[position].image)
            .load(data[position].image)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .into(holder.imageView)
        if (data[position].bought) {
            holder.coinsView.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int = data.size
}