package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    /*
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
     */

    private var bottomBarListener: BottomBarVisibilityListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
     */
    private fun onClickModifProfile(){
        Toast.makeText(this.context, "Le bouton Modifier profil a été cliqué !", Toast.LENGTH_SHORT)
            .show()
    }
    private fun onClickScenery(){
        Toast.makeText(this.context, "Le bouton Modifier Décor a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    private fun onClickShop(){
        Toast.makeText(this.context, "Le bouton Boutique a été cliqué !", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val buttonModifProfile = view.findViewById<Button>(R.id.button_modif)
        val buttonScenery = view.findViewById<Button>(R.id.button_scenery)
        val buttonShop = view.findViewById<Button>(R.id.button_shop)
        val pseudo = view.findViewById<TextView>(R.id.tv_pseudo)

        pseudo.text = User.username
        buttonModifProfile.setOnClickListener {
            onClickModifProfile()
        }
        buttonScenery.setOnClickListener {
            onClickScenery()
        }
        buttonShop.setOnClickListener {
            onClickShop()
        }

        val profilePicture = view.findViewById<ImageView>(R.id.profileimage)

        if (User.profile_picture != "") {
            Glide.with(this)
                .load(User.profile_picture)
                .into(profilePicture)
        }

        return view
        // Inflate the layout for this fragment
    }


/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

 */
}