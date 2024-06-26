package tranquitaskstudio.project.tranquitaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.data.FriendsModel
import tranquitaskstudio.project.tranquitaskapp.fragment.Friends
import com.google.firebase.firestore.DocumentReference

class FriendsRowAdapter(val data: List<FriendsModel>, val fragment : Friends, val isFriend : Boolean = true,val goToFriendProfile : (DocumentReference) -> Unit) :
    RecyclerView.Adapter<FriendsRowAdapter.MyViewHolder>() {
    class MyViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val imageView: ImageView = row.findViewById(R.id.avatar)
        val pseudoView: TextView = row.findViewById(R.id.pseudo_row)
        val button1: ImageView = row.findViewById(R.id.yes)
        val button2: ImageView = row.findViewById(R.id.no)
        val button3: ImageView = row.findViewById(R.id.supprimer)
        val card = row.findViewById<RelativeLayout>(R.id.displayed_profile)
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
        holder.card.setOnClickListener{
            data[position].ref?.let { it1 -> goToFriendProfile(it1) }
        }
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
            holder.button1.setOnClickListener{fragment.acceptNewFriend(position)}
            holder.button2.setOnClickListener{fragment.denyNewFriend(position)}
        } else {
            // C'est déjà un ami, afficher le bouton "Supprimer"
            holder.button3.visibility = View.VISIBLE
            holder.button3.setOnClickListener { fragment.removeFriend(position) }
        }
    }

    override fun getItemCount(): Int = data.size
}