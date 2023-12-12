package com.example.tranquitaskapp.fragment

import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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



class ListTaskFilter : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_task_filter, container, false)
        val buttonback = view.findViewById<ImageView>(R.id.btn_back)

        val buttonPriority = view.findViewById<ImageView>(R.id.button_priority)
        val priorityGroup = view.findViewById<RadioGroup>(R.id.priority_group)

        val buttonCategory = view.findViewById<ImageView>(R.id.button_category)
        val categoryGroup = view.findViewById<RadioGroup>(R.id.category_group)

        val buttonPeriod = view.findViewById<ImageView>(R.id.button_period)
        val periodGroup = view.findViewById<RadioGroup>(R.id.period_group)
        buttonback.setOnClickListener {
            val fragment = ListTaches()
            val slideUp = Slide(Gravity.BOTTOM)
            slideUp.duration = 150 // Durée de l'animation en millisecondes
            fragment.enterTransition = slideUp
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }
        buttonCategory.setOnClickListener {
            // Inverser la visibilité du RadioGroup
            periodGroup.visibility = View.GONE
            buttonPeriod.setImageResource(R.drawable.arrow_down)
            priorityGroup.visibility = View.GONE
            buttonPriority.setImageResource(R.drawable.arrow_down)
            categoryGroup.visibility = if (categoryGroup.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Changer la flèche en fonction de la visibilité
            val arrowResource =
                if (categoryGroup.visibility == View.VISIBLE) R.drawable.arrow_up else R.drawable.arrow_down
            buttonCategory.setImageResource(arrowResource)
        }
        buttonPeriod.setOnClickListener {
            // Inverser la visibilité du RadioGroup
            categoryGroup.visibility = View.GONE
            buttonCategory.setImageResource(R.drawable.arrow_down)
            priorityGroup.visibility = View.GONE
            buttonPriority.setImageResource(R.drawable.arrow_down)
            periodGroup.visibility = if (periodGroup.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Changer la flèche en fonction de la visibilité
            val arrowResource =
                if (periodGroup.visibility == View.VISIBLE) R.drawable.arrow_up else R.drawable.arrow_down
            buttonPeriod.setImageResource(arrowResource)
        }
        buttonPriority.setOnClickListener {
            categoryGroup.visibility = View.GONE
            buttonCategory.setImageResource(R.drawable.arrow_down)
            periodGroup.visibility = View.GONE
            buttonPeriod.setImageResource(R.drawable.arrow_down)
            priorityGroup.visibility = if (priorityGroup.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Changer la flèche en fonction de la visibilité
            val arrowResource =
                if (priorityGroup.visibility == View.VISIBLE) R.drawable.arrow_up else R.drawable.arrow_down
            buttonPriority.setImageResource(arrowResource)
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