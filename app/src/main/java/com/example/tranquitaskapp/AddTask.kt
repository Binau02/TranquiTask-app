package com.example.tranquitaskapp

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddTask : Fragment() {

    lateinit var tvDate : TextView
    lateinit var btnShowDatePicker: Button
    private val calendar = Calendar.getInstance()
    private var bottomBarListener: BottomBarVisibilityListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_addtask, container, false)
        val saveBtn : Button = view.findViewById(R.id.btnSave)

        tvDate = view.findViewById<TextView>(R.id.tvSelectedDate)
        btnShowDatePicker = view.findViewById<Button>(R.id.btnCalendarView)

        btnShowDatePicker.setOnClickListener{
            showDatePicker()
        }

        saveBtn.setOnClickListener{
            val fragment = Home()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout,fragment)?.commit()
        }

        return view
    }

    private fun showDatePicker(){

        val datePickerDialog = DatePickerDialog(requireContext(), { datePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            tvDate.text = formattedDate
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

    }
}