package tranquitaskstudio.project.tranquitaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.data.ListTask
import tranquitaskstudio.project.tranquitaskapp.data.TacheModel
import tranquitaskstudio.project.tranquitaskapp.data.User
import tranquitaskstudio.project.tranquitaskapp.firebase.MyFirebase
import tranquitaskstudio.project.tranquitaskapp.ui.CustomPopup
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import tranquitaskstudio.project.tranquitaskapp.data.MainActivityVariables.context

class ListeTachesRowAdapter(
    val data: MutableList<TacheModel>,
    private val onStartButtonClick: (position: Int) -> Unit,
    private val taskEditCallBack: (position: Int) -> Unit,
    private val db: FirebaseFirestore = MyFirebase.getFirestoreInstance()

) :
    RecyclerView.Adapter<ListeTachesRowAdapter.MyViewHolder>() {
    class MyViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val imageView: ImageView = row.findViewById(R.id.logoImageView)
        val pseudoView: TextView = row.findViewById(R.id.nameTextView)
        val progressBar: ProgressBar = row.findViewById(R.id.progressBarFront)

        val cardView: CardView = row.findViewById(R.id.task_card_view)
        val taskDuration: TextView = row.findViewById(R.id.task_duration)
        val taskDeadline: TextView = row.findViewById(R.id.task_deadline)
        val taskPriority: TextView = row.findViewById(R.id.task_priority)
        val taskCategory: TextView = row.findViewById(R.id.task_category)
        val taskConcentration: TextView = row.findViewById(R.id.task_concentration)
        val taskDivisible: TextView = row.findViewById(R.id.task_divisible)

        val imageDevelop: ImageView = row.findViewById(R.id.image_develop)
        val buttonStart: Button = row.findViewById(R.id.buttonStart)
        val modify: ImageView = row.findViewById(R.id.edit)
        val delete: ImageView = row.findViewById(R.id.delete)
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
            onStartButtonClick(position)
        }
        holder.imageDevelop.setOnClickListener {
            if (data[position].isDetail) {
                holder.imageDevelop.setImageResource(R.drawable.arrow_up)
                data[position].isDetail = false
            } else {
                holder.imageDevelop.setImageResource(R.drawable.arrow_down)
                data[position].isDetail = true
            }
            notifyItemChanged(position) // Informer l'adaptateur du changement
        }
        holder.pseudoView.text = data[position].name
        holder.imageView.setImageResource(data[position].logoResId)
        holder.progressBar.progress = data[position].progress
        holder.modify.setOnClickListener {
            taskEditCallBack(position)
        }
        holder.delete.setOnClickListener {
            CustomPopup.showPopup(
                context = holder.row.context,
                holder.row.context.getString(R.string.delete_task_pop_up),
                object : CustomPopup.PopupClickListener {
                    override fun onPopupButtonClick() {
                        val collection = MyFirebase.getFirestoreInstance().collection("tache")
                        User.ref?.id?.let { userId ->
                            val userDocReference = db.collection("user").document(userId)
                            val taskReference = collection.document(data[position].id)
                            taskReference.delete()
                                .addOnSuccessListener {
                                    userDocReference.update("taches",FieldValue.arrayRemove(data[position].ref))
                                        .addOnSuccessListener {
                                            ListTask.list.removeIf { it.ref == data[position].ref }
                                            data.removeAt(position)
                                            notifyDataSetChanged()
                                        }
                                    notifyItemRemoved(position)
                                }
                        }
                    }
                }
            )
        }


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
        if (data[position].concentration) {
            holder.taskConcentration.text = context.getString(R.string.yes)
        }
        else{
            holder.taskConcentration.text = context.getString(R.string.no)
        }
        if (data[position].isDivisible) {
            holder.taskDivisible.text = context.getString(R.string.yes)
        }
        else{
            holder.taskDivisible.text = context.getString(R.string.no)
        }
    }

    override fun getItemCount(): Int = data.size

    private fun convertMinutesToTimeString(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        val hoursString = if (hours < 10) "0$hours" else "$hours"
        val minutesString = if (minutes < 10) "0$minutes" else "$minutes"

        return "$hoursString:$minutesString"
    }
}