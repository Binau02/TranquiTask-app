package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.firebase.MyFirebase
import com.google.firebase.firestore.DocumentReference


class ProfileOther (val ref : DocumentReference,val friends: Boolean): Fragment() {
    val db = MyFirebase.getFirestoreInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun replaceFragment(fragment: Fragment){
        val slideUp = Slide(Gravity.BOTTOM)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val view = inflater.inflate(R.layout.fragment_profile_other, container, false)
        val arrowReturn = view.findViewById<Button>(R.id.back2)
        arrowReturn.setOnClickListener{
            if(friends){
                replaceFragment(Friends())
            }else{
                replaceFragment(Leaderboard())
            }
        }
        return view
    }

}