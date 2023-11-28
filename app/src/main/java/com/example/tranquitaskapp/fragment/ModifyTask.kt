package com.example.tranquitaskapp.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.CategoryDictionary
import com.example.tranquitaskapp.data.ListTask
import com.example.tranquitaskapp.data.Task
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.tranquitaskapp.data.Priorities
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import java.util.Calendar


class ModifyTask(private val task: Task) : Fragment() {
    private lateinit var taskName : TextView
    private lateinit var taskCategory : Spinner
    private lateinit var taskDeadline : TextView
    private lateinit var taskDuration : TextView
    private lateinit var taskDivisible : CheckBox
    private lateinit var taskConcentration: CheckBox
    private lateinit var taskPriority : Spinner
    private lateinit var imgShowDatePicker: ImageView
    private lateinit var imgTimeView: ImageView
    private lateinit var saveBtn: ImageView
    private val listCategory = HashMap<String, DocumentReference>()
    private val listPriority = HashMap<String, Int>()
    private val calendar = Calendar.getInstance()
    private var formattedDate: Timestamp? = null
    private var timestampInSeconds: Int = 0
    private val db = MyFirebase.getFirestoreInstance()


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
        imgTimeView = view.findViewById(R.id.imgTimeView)
        imgShowDatePicker = view.findViewById(R.id.ImgCalendarView)
        saveBtn = view.findViewById(R.id.btnSave)

        getCategories(taskCategory)
        getPriority(taskPriority)

        taskName.text = task.name
        val positionCategory = CategoryDictionary.dictionary.entries.indexOfFirst { it.key == task.categorie }
        if (positionCategory != -1) {
            taskCategory.setSelection(positionCategory)
        }
        taskDeadline.text = timestampToString(task.deadline)
        taskDuration.text = minuteToString(task.duree)
        taskDivisible.isChecked = task.divisible
        taskConcentration.isChecked = task.concentration
        val positionPriority = Priorities.dictionary.entries.indexOfFirst { it.key == task.priorite }
        if (positionPriority != -1) {
            taskPriority.setSelection(positionPriority)
        }

        imgShowDatePicker.setOnClickListener {
            showDatePicker()
        }
        imgTimeView.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                // Convertir le timestamp en minutes
                val timestampInMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

                taskDuration.text = SimpleDateFormat("HH:mm").format(cal.time)
                timestampInSeconds = timestampInMinutes
            }

            // Créer le TimePickerDialog en mode "spinner"
            val timePickerDialog =
                TimePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                )
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }

        saveBtn.setOnClickListener {
            val checkBoxConcentration = view.findViewById<CheckBox>(R.id.checkBoxConcentration)
            val isConcentrationChecked = checkBoxConcentration.isChecked

            val checkBoxDivisible = view.findViewById<CheckBox>(R.id.checkBoxDivisible)
            val isDivisibleChecked = checkBoxDivisible.isChecked

            val nameTask = view.findViewById<TextView>(R.id.editNameTask)
            val categorie =
                listCategory[taskCategory.selectedItem]
            val priority =
                listPriority[taskPriority.selectedItem]

            val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning)


            if (nameTask.text.isNullOrBlank() || formattedDate == null || timestampInSeconds == 0) {
                // Afficher une erreur ou une notification indiquant que tous les champs doivent être remplis
                if (nameTask.text.isEmpty()) {
                    nameTask.setError("Please Enter a name", icon)
                }


                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (categorie != null && priority != null) {
                    saveModifTask(
                        categorie,
                        priority,
                        nameTask.text.toString(),
                        formattedDate!!,
                        isDivisibleChecked,
                        isConcentrationChecked,
                        timestampInSeconds
                    )
                }
            }
        }

        // Inflate the layout for this fragment
        return view
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar.set(year, monthOfYear, dayOfMonth)  // Mettez à jour l'instance de Calendar ici
                formattedDate = Timestamp(calendar.time)
                val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                taskDeadline.text = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
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

    private fun saveModifTask(
        newCategorie: DocumentReference,
        newPriority: Int,
        newNameTask: String,
        newDeadline: Timestamp,
        newDivisible: Boolean,
        newConcentration: Boolean,
        newDuree: Int
    ) {
        task.ref.update(
            mapOf(
            "categorie" to newCategorie,
            "concentration" to newConcentration,
            "deadline" to newDeadline,
            "divisible" to newDivisible,
            "done" to task.done,
            "duree" to newDuree,
            "name" to newNameTask,
            "priorite" to newPriority
        ))
            .addOnSuccessListener {
                ListTask.list.find { it.ref == task.ref }?.let { taskToUpdate ->
                    // Mettre à jour la tâche si elle est trouvée
                    taskToUpdate.apply {
                        categorie = newCategorie
                        concentration = newConcentration
                        deadline = newDeadline
                        divisible = newDivisible
                        done = task.done
                        duree = newDuree
                        name = newNameTask
                        priorite = newPriority
                    }
                }
                Toast.makeText(this.context, "Tâche modifiee", Toast.LENGTH_SHORT).show()

                val fragment = ListTaches()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameLayout, fragment)?.commit()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this.context, "Il y a eu une erreur $e", Toast.LENGTH_SHORT).show()
            }
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
