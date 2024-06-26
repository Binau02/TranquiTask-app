package tranquitaskstudio.project.tranquitaskapp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.data.ItemModel
import tranquitaskstudio.project.tranquitaskapp.fragment.Shop

class ShopAdapter(val data: List<ItemModel>, val fragment : Shop) :
    RecyclerView.Adapter<ShopAdapter.MyViewHolder>() {
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
        Glide.with(fragment)
            .load(data[position].image)
            .into(holder.imageView)
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