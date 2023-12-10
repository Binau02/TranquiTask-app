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

class Profile : Fragment() {
    private var bottomBarListener: BottomBarVisibilityListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private fun onClickModifProfile(){
        replaceFragment(ModifyProfile())
    }
    private fun onClickScenery(){
//        Toast.makeText(this.context, "Le bouton Modifier Décor a été cliqué !", Toast.LENGTH_SHORT).show()
        val fragment = EditDecor()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }
    private fun onClickShop(){
//        Toast.makeText(this.context, "Le bouton Boutique a été cliqué !", Toast.LENGTH_SHORT).show()
        val fragment = Shop()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
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

        val categories = mapOf<String, ImageView>(
            "sol" to view.findViewById(R.id.sol),
            "maison" to view.findViewById(R.id.maison),
            "arbre" to view.findViewById(R.id.arbre),
            "ciel" to view.findViewById(R.id.ciel)
        )

        for ((category, image) in categories) {
            if (User.decor[category] != "") {
                Glide.with(this)
                    .load(User.decor[category])
                    .into(image)
            }
        }
        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)
        return view
    }
}