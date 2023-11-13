package com.example.tranquitaskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddTask : Fragment() {

    private lateinit var tvDate : TextView
    private lateinit var ImgShowDatePicker: ImageView
    private lateinit var imgTimeView: ImageView

    private val calendar = Calendar.getInstance()
    private var bottomBarListener: BottomBarVisibilityListener? = null
    private lateinit var formattedDate: String
    private var timestampInSeconds: Int = 0

    private val auth = MyFirebaseAuth.getFirestoreInstance()
    private val db = MyFirebase.getFirestoreInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    private fun createNewTask(categorie: String, nameTask: String, deadline: String,divisible: Boolean, concentration: Boolean, duree: Int) {
        val taskCollection = db.collection("tache")

        val taskData = hashMapOf(
            "categorie" to emptyList<String>(),
            "concentration" to concentration,
            "deadline" to deadline,
            "divisible" to divisible,
            "done" to 0,
            "duree" to duree,
            "name" to nameTask,
            "priorite" to emptyList<String>()
        )
        taskCollection.add(taskData)
            .addOnSuccessListener {
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

        tvDate = view.findViewById(R.id.tvSelectedDate)
        ImgShowDatePicker = view.findViewById<ImageView>(R.id.ImgCalendarView)

        ImgShowDatePicker.setOnClickListener{
            showDatePicker()
        }
        imgTimeView.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                tvSelectedTime.text = SimpleDateFormat("HH:mm").format(cal.time)

                val selectedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(tvSelectedTime.text.toString())
                val timestampInMillis = selectedTime?.time ?: 0 // Utilisation de 0 si la conversion échoue

// Convertir le timestamp en secondes
                timestampInSeconds = (timestampInMillis / 1000).toInt()

            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        saveBtn.setOnClickListener{
            val checkBoxConcentration = view.findViewById<CheckBox>(R.id.checkBoxConcentration)
            val isConcentrationChecked = checkBoxConcentration.isChecked

            val checkBoxDivisible = view.findViewById<CheckBox>(R.id.checkBoxDivisible)
            val isDivisibleChecked = checkBoxDivisible.isChecked

            val nameTask = view.findViewById<TextView>(R.id.editNameTask)
            val categorie = "null"

            if (nameTask.text.isNullOrBlank() || formattedDate.isNullOrBlank() || timestampInSeconds == null) {
                // Afficher une erreur ou une notification indiquant que tous les champs doivent être remplis
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                createNewTask(categorie, nameTask.text.toString(), formattedDate, isDivisibleChecked, isConcentrationChecked, timestampInSeconds)
            }
            createNewTask(categorie, nameTask.text.toString(), formattedDate, isDivisibleChecked, isConcentrationChecked, timestampInSeconds)
        }

        return view
    }

    private fun showDatePicker(){

        val datePickerDialog = DatePickerDialog(requireContext(), { datePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
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
}


