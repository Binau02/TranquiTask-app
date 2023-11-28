package com.example.tranquitaskapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.CategoryDictionary
import com.example.tranquitaskapp.data.Task
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.tranquitaskapp.data.Priorities
import com.google.firebase.firestore.DocumentReference


class ModifyTask(private val task: Task) : Fragment() {
    private lateinit var taskName : TextView
    private lateinit var taskCategory : Spinner
    private lateinit var taskDeadline : TextView
    private lateinit var taskDuration : TextView
    private lateinit var taskDivisible : CheckBox
    private lateinit var taskConcentration: CheckBox
    private lateinit var taskPriority : Spinner
    private val listCategory = HashMap<String, DocumentReference>()
    private val listPriority = HashMap<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_modify_task, container, false)
        taskName = view.findViewById(R.id.editNameTask)
        taskCategory = view.findViewById(R.id.spinnerCategory)
        taskDeadline = view.findViewById(R.id.tvSelectedDate)
        taskDuration = view.findViewById(R.id.tvSelectedTime)
        taskDivisible = view.findViewById(R.id.checkBoxDivisible)
        taskConcentration = view.findViewById(R.id.checkBoxConcentration)
        taskPriority = view.findViewById(R.id.spinnerPriority)

        getCategories(taskCategory)
        getPriority(taskPriority)

        taskName.text = task.name
        Log.d("CATEGORIE","${task.categorie}")
        val positionCategory = CategoryDictionary.dictionary.entries.indexOfFirst { it.key == task.categorie }
        Log.d("CATEGORIE",positionCategory.toString())
        if (positionCategory != -1) {
            taskCategory.setSelection(positionCategory)
        }
        taskDeadline.text = timestampToString(task.deadline)
        taskDuration.text = minuteToString(task.duree)
        taskDivisible.isChecked = task.divisible
        taskConcentration.isChecked = task.concentration
        val positionPriority = Priorities.dictionary.entries.indexOfFirst { it.key == task.priorite }
        Log.d("PRIORITY",positionPriority.toString())
        if (positionPriority != -1) {
            taskPriority.setSelection(positionPriority)
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun getCategories(spinnerCategory: Spinner) {
        val adapter = this.context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item
            )
        }

        // Spécification du layout déroulant à utiliser lorsque la liste apparaît
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Récupération des ressources de chaînes et ajout à l'Adapter
        for ((ref, category) in CategoryDictionary.dictionary) {
            adapter?.add(category.name)
            listCategory[category.name] = ref
        }

        // Attribution de l'Adapter au Spinner
        spinnerCategory.adapter = adapter
    }

    private fun getPriority(spinnerPriority: Spinner) {
        val adapter = this.context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item
            )
        }

        // Spécification du layout déroulant à utiliser lorsque la liste apparaît
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Récupération des ressources de chaînes et ajout à l'Adapter
        for ((value, priorityName) in Priorities.dictionary) {
            adapter?.add(priorityName)
            listPriority[priorityName] = value
        }

        // Attribution de l'Adapter au Spinner
        spinnerPriority.adapter = adapter
    }
}

private fun timestampToString(timestamp: Timestamp?): String {
    val date = timestamp?.toDate()?:Date()
    val format = SimpleDateFormat("dd/MM", Locale.FRENCH)
    return format.format(date)
}

private fun minuteToString(time: Int): String{
    val hours = time / 60
    val remainingMinutes = time % 60

    val hoursString = if (hours < 10) "0$hours" else "$hours"
    val minutesString = if (remainingMinutes < 10) "0$remainingMinutes" else "$remainingMinutes"

    return "$hoursString:$minutesString"
}
