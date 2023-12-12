package com.example.tranquitaskapp.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.regex.RegexPatterns



class SignUp : Fragment() {
    private val auth = MyFirebaseAuth.getFirestoreInstance()
    private val db = MyFirebase.getFirestoreInstance()

    private lateinit var username: EditText
    private lateinit var password: EditText

    private var bottomBarListener: BottomBarVisibilityListener? = null
    private var icon: Drawable? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        icon = AppCompatResources.getDrawable(requireContext(), R.drawable.or)

    }

    private fun onClickSignIn(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // L'utilisateur est enregistré avec succès
                    User.mail = email
                    User.username = username
                    createNewUser(email, username)
                } else {
                    Toast.makeText(this.context, "Il y a eu une erreur", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun createNewUser(email: String, username: String) {
        val usersCollection = db.collection("user")

        val userData = hashMapOf(
            "coins" to 0,
            "profile_picture" to "",
            "email" to email,
            "demandes" to emptyList<String>(), // Champ avec une liste vide
            "username" to username,
            "taches" to emptyList<String>(),
            "sol" to "",
            "maison" to "",
            "arbre" to "",
            "ciel" to "",
            "sol_bought" to emptyList<String>(),
            "maison_bought" to emptyList<String>(),
            "arbre_bought" to emptyList<String>(),
            "ciel_bought" to emptyList<String>()
        )
        usersCollection.add(userData)
            .addOnSuccessListener {
                User.id = it.id
                val fragment = Home()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameLayout, fragment)?.commit()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this.context, "Il y a eu une erreur : $e", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        val buttonSignUp = view.findViewById<ImageView>(R.id.btnSignUp)
        val buttonCancel = view.findViewById<ImageView>(R.id.btnCancel)

        buttonSignUp.setOnClickListener {
            val reg_mail = view.findViewById<EditText>(R.id.reg_mail)
            val reg_username = view.findViewById<EditText>(R.id.reg_username)
            val reg_password = view.findViewById<EditText>(R.id.reg_password)
            val reg_passwordconfirm =
                view.findViewById<EditText>(R.id.reg_passwordconfirm)
            val email = reg_mail.text.toString()
            val username = reg_username.text.toString()
            val password = reg_password.text.toString()
            val confirmPassword = reg_passwordconfirm.text.toString()



            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                if (email.isEmpty()) {
                    reg_mail.setError("Please Enter Email", icon)
                }
                if (username.isEmpty()) {
                    reg_username.setError("Please Enter Username", icon)
                }
                if (password.isEmpty()) {
                    reg_password.setError("Please Enter Password", icon)
                }
                if (confirmPassword.isEmpty()) {
                    reg_passwordconfirm.setError("Please Confirm Password", icon)
                }
                Toast.makeText(this.context, "Textes vides", Toast.LENGTH_SHORT).show()
            }
            Log.d("TEST", "Je suis bien co")
            if (
                password.matches(Regex(RegexPatterns.PASSWORD_PATTERN)) &&
                email.matches(Regex(RegexPatterns.MAIL_PATTERN)) &&
                password == confirmPassword &&
                username != ""
            ) {
                db.collection("user").whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            onClickSignIn(email, password, username)
                        } else {
                            Toast.makeText(
                                this.context,
                                "Il y a eu deja ce pseudo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this.context, "Vérifiez vos informations", Toast.LENGTH_SHORT).show()
            }
        }

        buttonCancel.setOnClickListener{
            val fragment = SignIn()
            val slideUp = Slide(Gravity.END)
            slideUp.duration = 300 // Durée de l'animation en millisecondes
            fragment.enterTransition = slideUp
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }
        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)
        // Inflate the layout for this fragment
        return view
    }
}