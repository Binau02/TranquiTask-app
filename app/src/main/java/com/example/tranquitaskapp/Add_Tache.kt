package com.example.tranquitaskapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Add_Tache : Fragment() {


    lateinit var tvDate : TextView
    lateinit var btnShowDatePicker: Button
    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_tache, container, false)
        tvDate = view.findViewById<TextView>(R.id.tvSelectedDate)
        btnShowDatePicker = view.findViewById<Button>(R.id.btnSelectDate)

        btnShowDatePicker.setOnClickListener{
            showDatePicker()
        }
        return view
    }

    private fun showDatePicker(){

        val datePickerDialog = DatePickerDialog(requireContext(), { datePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            tvDate.text = "Selected Date:" + formattedDate
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

    }

}