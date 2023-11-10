package com.example.tranquitaskapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val addBtn : com.google.android.material.floatingactionbutton.FloatingActionButton = view.findViewById(R.id.fab1)
        val searchBtn : com.google.android.material.floatingactionbutton.FloatingActionButton = view.findViewById(R.id.fab2)

        addBtn.setOnClickListener{
            val fragment = AddTask()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout,fragment)?.commit()
        }
        searchBtn.setOnClickListener{
            val fragment = Friends()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout,fragment)?.commit()
        }
        return view
    }

}