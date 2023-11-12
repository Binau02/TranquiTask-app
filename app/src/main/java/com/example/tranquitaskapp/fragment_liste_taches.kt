package com.example.tranquitaskapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * A simple [Fragment] subclass.
 * Use the [fragment_liste_taches.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_liste_taches : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liste_taches, container, false)



        return view
    }
}