package com.example.tranquitaskapp.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.lifecycleScope
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue

import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddTask : Fragment() {

    private lateinit var tvDate: TextView
    private lateinit var ImgShowDatePicker: ImageView
    private lateinit var imgTimeView: ImageView

    private val calendar = Calendar.getInstance()
    private var bottomBarListener: BottomBarVisibilityListener? = null
    private var formattedDate: String? = null
    private var timestampInSeconds: Int = 0
    private val listCategory = mutableListOf<String>()
    private val listPriority = mutableListOf<String>()



    private val auth = MyFirebaseAuth.getFirestoreInstance()
    private val db = MyFirebase.getFirestoreInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    private fun createNewTask(
        categorie: String,
        priority: String,
        nameTask: String,
        deadline: String,
        divisible: Boolean,
        concentration: Boolean,
        duree: Int
    ) {
        val taskCollection = db.collection("tache")
        val categorieReference = db.collection("tache_categorie").document(categorie)
        val priorityReference = db.collection("tache_priorite").document(priority)

        // Convertir la date de format texte en timestamp
        val deadlineTimestamp = Timestamp(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(deadline)
        )

        val taskData = hashMapOf(
            "categorie" to categorieReference,
            "concentration" to concentration,
            "deadline" to deadlineTimestamp,
            "divisible" to divisible,
            "done" to 0,
            "duree" to duree,
            "name" to nameTask,
            "priorite" to priorityReference
            )
        taskCollection.add(taskData)
            .addOnSuccessListener {
                addTaskToUser(it)
                val fragment = Home()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameLayout, fragment)?.commit()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this.context, "Il y a eu une erreur", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_addtask, container, false)
        val saveBtn : Button = view.findViewById(R.id.btnSave)
        imgTimeView = view.findViewById<ImageView>(R.id.imgTimeView)
        val tvSelectedTime = view.findViewById<TextView>(R.id.tvSelectedTime)
        val timestamp = 0
        val packageName = this.context?.packageName // Nom du package de votre application
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerPriority = view.findViewById<Spinner>(R.id.spinnerPriority)


        lifecycleScope.launch {
            getCategories(spinnerCategory)
            getPriority(spinnerPriority)
        }



        tvDate = view.findViewById(R.id.tvSelectedDate)
        ImgShowDatePicker = view.findViewById<ImageView>(R.id.ImgCalendarView)

        ImgShowDatePicker.setOnClickListener {
            showDatePicker()
        }
        imgTimeView.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                tvSelectedTime.text = SimpleDateFormat("HH:mm").format(cal.time)

                val selectedTime = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).parse(tvSelectedTime.text.toString())
                val timestampInMillis =
                    selectedTime?.time ?: 0 // Utilisation de 0 si la conversion échoue

                // Convertir le timestamp en minutes
                timestampInSeconds = (timestampInMillis / 60000).toInt()
            }

            // Créer le TimePickerDialog en mode "spinner"
            val timePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                TimePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                )
            } else {
                TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                )
            }
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
                listCategory[view.findViewById<Spinner>(R.id.spinnerCategory).selectedItemPosition]
            val priority =
                listPriority[view.findViewById<Spinner>(R.id.spinnerPriority).selectedItemPosition]

            val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning)


            if (nameTask.text.isNullOrBlank() || formattedDate.isNullOrEmpty() || timestampInSeconds == 0) {
                // Afficher une erreur ou une notification indiquant que tous les champs doivent être remplis
                if (nameTask.text.isEmpty()) {
                    nameTask.setError("Please Enter a name", icon)
                }


                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT)
                    .show()
            } else {
                createNewTask(
                    categorie,
                    priority,
                    nameTask.text.toString(),
                    formattedDate?: "",
                    isDivisibleChecked,
                    isConcentrationChecked,
                    timestampInSeconds
                )
            }
        }
        return view
    }

    private fun showDatePicker() {

        val datePickerDialog = DatePickerDialog(
            requireContext(), { datePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formattedDate = dateFormat.format(selectedDate.time)
                tvDate.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

    }

    private fun getCategories(spinnerCategory: Spinner) {
        val packageName = this.context?.packageName // Nom du package de votre application

        db.collection("tache_categorie")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("name")
                    if (name != null)
                        listCategory.add(name)
                }

                val adapter = this.context?.let {
                    ArrayAdapter<String>(
                        it,
                        android.R.layout.simple_spinner_item
                    )
                }

                // Spécification du layout déroulant à utiliser lorsque la liste apparaît
                if (adapter != null) {
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                // Récupération des ressources de chaînes et ajout à l'Adapter
                listCategory.forEach { resourceName ->
                    val resourceId = resources.getIdentifier(resourceName, "string", packageName)
                    if (adapter != null) {
                        adapter.add(getString(resourceId))
                    }
                }

                // Attribution de l'Adapter au Spinner
                spinnerCategory.adapter = adapter
                // Mettre à jour l'adaptateur une fois que les données sont récupérées avec succès
            }
            .addOnFailureListener { exception ->
                // Gérer les erreurs éventuelles
                Log.e("ERROR", "Erreur lors de la récupération des catégories: $exception")
            }
    }

    private fun getPriority(spinnerPriority: Spinner) {
        val packageName = this.context?.packageName // Nom du package de votre application

        db.collection("tache_priorite")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("name")
                    if (name != null)
                        listPriority.add(name)
                }

                val adapter = this.context?.let {
                    ArrayAdapter<String>(
                        it,
                        android.R.layout.simple_spinner_item
                    )
                }

                // Spécification du layout déroulant à utiliser lorsque la liste apparaît
                if (adapter != null) {
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                // Récupération des ressources de chaînes et ajout à l'Adapter
                listPriority.forEach { resourceName ->
                    val resourceId = resources.getIdentifier(resourceName, "string", packageName)
                    if (adapter != null) {
                        adapter.add(getString(resourceId))
                    }
                }

                // Attribution de l'Adapter au Spinner
                spinnerPriority.adapter = adapter
                // Mettre à jour l'adaptateur une fois que les données sont récupérées avec succès
            }
            .addOnFailureListener { exception ->
                // Gérer les erreurs éventuelles
                Log.e("ERROR", "Erreur lors de la récupération des catégories: $exception")
            }
    }

    private fun addTaskToUser(idTask: DocumentReference){
        val userRef = db.collection("user").document(User.id)
        val taskRef = db.collection("tache").document(idTask.id)

        userRef.update("taches", FieldValue.arrayUnion(taskRef))
            .addOnSuccessListener {
                // La mise à jour a réussi
                Log.d("Update", "ID de la tâche ajouté au tableau tasks de l'utilisateur")
            }
            .addOnFailureListener { e ->
                // Gérer les erreurs lors de la mise à jour
                Log.e("Update", "Erreur lors de l'ajout de l'ID de la tâche : $e")
            }
    }
}


