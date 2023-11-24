package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener

class ModifyProfile : Fragment() {

    private val db = MyFirebase.getFirestoreInstance()
    private lateinit var rv: RecyclerView

    private var bottomBarListener: BottomBarVisibilityListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    private fun onClickBack(){
        Toast.makeText(this.context, "Le bouton Retour a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    private fun onClickSave(){
        Toast.makeText(this.context, "Le bouton Sauvegarder a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    private fun onClickCancel(){
        Toast.makeText(this.context, "Le bouton Annuler a été cliqué !", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_modify_profile, container, false)

        val buttonBack = view.findViewById<Button>(R.id.back2)
        val buttonSave = view.findViewById<Button>(R.id.buttonSave)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val pseudo = view.findViewById<TextView>(R.id.tv_pseudo)
        val profilePicture = view.findViewById<ImageView>(R.id.profileimage)
        val username = view.findViewById<EditText>(R.id.reg_username)
        val email = view.findViewById<EditText>(R.id.reg_mail)
        val password = view.findViewById<EditText>(R.id.reg_password)
        val passwordConfirm = view.findViewById<EditText>(R.id.reg_passwordconfirm)

        pseudo.text = User.username

        if (User.profile_picture != "") {
            Glide.with(this)
                .load(User.profile_picture)
                .into(profilePicture)
        }

        buttonBack.setOnClickListener {
            onClickBack()
        }
        buttonSave.setOnClickListener {
            onClickSave()
        }
        buttonCancel.setOnClickListener {
            onClickCancel()
        }


        return view
    }

}