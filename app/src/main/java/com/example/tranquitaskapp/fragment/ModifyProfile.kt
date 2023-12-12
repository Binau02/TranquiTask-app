package com.example.tranquitaskapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.ui.CustomPopup

class ModifyProfile : Fragment() {

    private val db = MyFirebase.getFirestoreInstance()
    private val auth = MyFirebaseAuth.getFirestoreInstance()
    private lateinit var rv: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences

    private var bottomBarListener: BottomBarVisibilityListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    private fun onClickBack(){
        Toast.makeText(this.context, "Le bouton Retour a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    private fun onClickSave(newUsername : String, newMail: String, newPassword: String){
        val currentUser = auth.currentUser
        currentUser?.updateEmail(newMail)
            ?.addOnCompleteListener { task ->
            }

        currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->

            }

        val userRef = User.ref ?: db.collection("user").document(User.id)
        userRef.update("username", newUsername)
            .addOnSuccessListener {
                Log.d("Update", "ID de la tâche ajouté au tableau tasks de l'utilisateur")
            }
            .addOnFailureListener { e ->
                Log.e("Update", "Erreur lors de l'ajout de l'ID de la tâche : $e")
            }
        User.username = newUsername
        User.mail = newMail
        val fragment = Profile()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }
    private fun onClickCancel(){
        val fragment = Profile()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_modify_profile, container, false)

        val buttonSave = view.findViewById<Button>(R.id.buttonSave)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val pseudo = view.findViewById<TextView>(R.id.tv_pseudo)
        val profilePicture = view.findViewById<ImageView>(R.id.profileimage)
        val username = view.findViewById<EditText>(R.id.reg_username)
        val email = view.findViewById<EditText>(R.id.reg_mail)
        val password = view.findViewById<EditText>(R.id.reg_password)
        val passwordConfirm = view.findViewById<EditText>(R.id.reg_passwordconfirm)

        pseudo.text = User.username
        username.setText(User.username)
        email.setText(User.mail)
        password.setText(sharedPreferences.getString("password", null))
        passwordConfirm.setText(sharedPreferences.getString("password", null))

        if (User.profile_picture != "") {
            Glide.with(this)
                .load(User.profile_picture)
                .into(profilePicture)
        }


        buttonSave.setOnClickListener {
            if(password.text.toString()==passwordConfirm.text.toString()){
                this.context?.let {
                    CustomPopup.showPopup(
                        context = it,
                        getString(R.string.modify_profile_pop_up),
                        object :
                            CustomPopup.PopupClickListener {
                            override fun onPopupButtonClick() {
                                onClickSave(username.text.toString(),email.text.toString(),password.text.toString())
                            }
                        }
                    )
                }
            }else{
                Toast.makeText(this.context, "Les mots de passes ne sont pas identiques", Toast.LENGTH_SHORT).show()
            }
        }
        buttonCancel.setOnClickListener {

            this.context?.let {
                CustomPopup.showPopup(
                    context = it,
                    getString(R.string.cancelmodif),
                    object :
                        CustomPopup.PopupClickListener {
                        override fun onPopupButtonClick() {
                            onClickCancel()
                        }
                    }
                )
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