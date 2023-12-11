package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tranquitaskapp.R
import com.google.firebase.firestore.DocumentReference

class ProfileOther (ref : DocumentReference): Fragment() {
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }

}