package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener


/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPassword.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPassword : Fragment() {

    private val auth = MyFirebaseAuth.getFirestoreInstance()

    private var bottomBarListener: BottomBarVisibilityListener? = null
    private lateinit var email : EditText
    private lateinit var buttonSend : Button
    private lateinit var buttonBack : Button


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.connection_password_forgotten, container, false)
        email = view.findViewById(R.id.mail_user)
        buttonSend = view.findViewById(R.id.buttonSend)
        buttonBack = view.findViewById(R.id.buttonBack)

        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)
        buttonSend.setOnClickListener{
            val userEmail = email.text.toString().trim()

            if (userEmail.isNotEmpty()) {
                auth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Un mail vous a été envoyé", Toast.LENGTH_SHORT)
                                .show()
                            val fragment = SignIn()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.frameLayout, fragment)?.commit()
                        } else {
                            Toast.makeText(
                                context,
                                "La réinitialisation a échoué",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            else {
                Toast.makeText(context, "Veuillez entrer une adresse e-mail", Toast.LENGTH_SHORT).show()
            }
        }

        buttonBack.setOnClickListener{
            val fragment = SignIn()
            val slideUp = Slide(Gravity.TOP)
            slideUp.duration = 300 // Durée de l'animation en millisecondes
            fragment.enterTransition = slideUp
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }

        return view
        // Inflate the layout for this fragment
    }

}
