package com.example.tranquitaskapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.Task

class ModifyTask(private val task: Task) : Fragment() {
    private lateinit var taskName : TextView
    private lateinit var taskDuration : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_modify_task, container, false)
        taskName =
        // Inflate the layout for this fragment
        return view
    }
}