package com.example.tranquitaskapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.CategoryDictionary
import com.example.tranquitaskapp.data.Period
import com.example.tranquitaskapp.data.PeriodDictionary
import com.example.tranquitaskapp.data.Priorities
import com.example.tranquitaskapp.data.ListTaskFilter


/**
 * A simple [Fragment] subclass.
 * Use the [ListTaskFilter.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListTaskFilter : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_task_filter, container, false)
        val buttonback = view.findViewById<ImageView>(R.id.btn_back)
        buttonback.setOnClickListener {
            val fragment = ListTaches()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }
        setRadioButtons(view)

        return view
    }

    private fun setRadioButtons(view : View) {
        var buttonId : Int = 0

        // set period radio buttons
        val periodRadioGroup = view.findViewById<RadioGroup>(R.id.period_group)
        for ((period, option) in PeriodDictionary.periodToString) {
            val radioButton = RadioButton(context)
            radioButton.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.text = option
            periodRadioGroup.addView(radioButton)
            if (period == ListTaskFilter.period) {
//                radioButton.isChecked = true
                buttonId = radioButton.id
            }
        }

        periodRadioGroup.check(buttonId)

        periodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            if (selectedRadioButton != null) {
                val selectedValue = selectedRadioButton.text.toString()
                ListTaskFilter.period = PeriodDictionary.stringToPeriod[selectedValue] ?: Period.ALL
            }
        }


        // set category radio buttons
        val categoryRadioGroup = view.findViewById<RadioGroup>(R.id.category_group)
        val radioButtonCategory = RadioButton(context)
        radioButtonCategory.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        radioButtonCategory.text = getString(R.string.all)
        categoryRadioGroup.addView(radioButtonCategory)
        if (ListTaskFilter.category == null) {
//            radioButtonCategory.isChecked = true
            buttonId = radioButtonCategory.id
        }
        for ((ref, option) in CategoryDictionary.dictionary) {
            val radioButton = RadioButton(context)
            radioButton.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.text = option.name
            categoryRadioGroup.addView(radioButton)
            if (ref == ListTaskFilter.category) {
//                radioButton.isChecked = true
                buttonId = radioButton.id
            }
        }

        Log.d("TEST", "buttonId : $buttonId, filter cat : ${ListTaskFilter.category}")
        categoryRadioGroup.check(buttonId)

        categoryRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            if (selectedRadioButton != null) {
                val selectedValue = selectedRadioButton.text.toString()
                Log.d("TEST", "new cat : ${CategoryDictionary.nameToDocumentReference}")
                ListTaskFilter.category = CategoryDictionary.nameToDocumentReference[selectedValue]
            }
        }


        // set priority radio buttons
        val priorityRadioGroup = view.findViewById<RadioGroup>(R.id.priority_group)
        val radioButtonPriority = RadioButton(context)
        radioButtonPriority.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        radioButtonPriority.text = getString(R.string.all)
        priorityRadioGroup.addView(radioButtonPriority)
        if (ListTaskFilter.priority == -1) {
//            radioButtonPriority.isChecked = true
            buttonId = radioButtonPriority.id
        }
        for ((value, option) in Priorities.dictionary) {
            val radioButton = RadioButton(context)
            radioButton.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.text = option
            priorityRadioGroup.addView(radioButton)
            if (value == ListTaskFilter.priority) {
//                radioButton.isChecked = true
                buttonId = radioButton.id
            }
        }

        priorityRadioGroup.check(buttonId)

        priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            if (selectedRadioButton != null) {
                val selectedValue = selectedRadioButton.text.toString()
                ListTaskFilter.priority = Priorities.reversedDictionary[selectedValue] ?: -1
            }
        }
    }
}