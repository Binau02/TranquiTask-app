package com.example.tranquitaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.TacheModel
import com.example.tranquitaskapp.fragment.ListTaches
import com.example.tranquitaskapp.interfaces.TaskButtonClickListener

class ListeTachesRowAdapter(
    val data: List<TacheModel>,
    private val buttonClickListener: TaskButtonClickListener
) :
    RecyclerView.Adapter<ListeTachesRowAdapter.MyViewHolder>() {
    class MyViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val imageView = row.findViewById<ImageView>(R.id.logoImageView)
        val pseudoView = row.findViewById<TextView>(R.id.nameTextView)
        val progressBar = row.findViewById<ProgressBar>(R.id.progressBarFront)

        val cardView = row.findViewById<CardView>(R.id.task_card_view)
        val taskDuration = row.findViewById<TextView>(R.id.task_duration)
        val taskDeadline = row.findViewById<TextView>(R.id.task_deadline)
        val taskPriority = row.findViewById<TextView>(R.id.task_priority)
        val taskCategory = row.findViewById<TextView>(R.id.task_category)
        val buttonDevelop = row.findViewById<Button>(R.id.button_develop)
        val buttonStart = row.findViewById<Button>(R.id.buttonStart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MyViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(
                R.layout.liste_tache_row,
                parent, false
            )
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.buttonStart.setOnClickListener {
            buttonClickListener.onStartButtonClick(position)
        }
        holder.buttonDevelop.setOnClickListener {
            data[position].isDetail = !data[position].isDetail
            if (holder.buttonDevelop.text == "v") {
                holder.buttonDevelop.text = "^"
            } else {
                holder.buttonDevelop.text = "v"
            }
            notifyItemChanged(position) // Informer l'adaptateur du changement
        }
        holder.pseudoView.text = data[position].name
        holder.imageView.setImageResource(data[position].logoResId)
        holder.progressBar.progress = data[position].progress

        if (data[position].isDetail) {
            holder.cardView.visibility = View.VISIBLE
        } else {
            holder.cardView.visibility = View.GONE
        }
        holder.cardView.requestLayout()
        holder.taskDuration.text = convertMinutesToTimeString(data[position].duration)
        holder.taskDeadline.text = data[position].deadline
        holder.taskPriority.text = data[position].priority
        holder.taskCategory.text = data[position].category
    }

    override fun getItemCount(): Int = data.size

    fun convertMinutesToTimeString(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        val hoursString = if (hours < 10) "0$hours" else "$hours"
        val minutesString = if (minutes < 10) "0$minutes" else "$minutes"

        return "$hoursString:$minutesString"
    }
}