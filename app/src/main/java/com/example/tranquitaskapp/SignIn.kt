package com.example.tranquitaskapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener

class SignIn : Fragment() {

    private val auth = MyFirebaseAuth.getFirestoreInstance()
    private val db = MyFirebase.getFirestoreInstance()

    private var bottomBarListener: BottomBarVisibilityListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    fun getUsersInformations(email: String) {
        db.collection("user").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    val username = documents.documents[0].getString("username")

                    val coins = documents.documents[0].getLong("coins")
                    val profile_picture = documents.documents[0].getString("profile_picture")
                    if (username != null && coins != null && profile_picture != null) {
                        User.getUser().username = username
                        User.getUser().mail = email
                        User.getUser().coins = coins
                        User.getUser().profile_picture = profile_picture
                    }
                    val fragment = Home()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameLayout, fragment)?.commit()
                }

            }
            .addOnFailureListener { exception ->
                // Gérer les erreurs éventuelles
                Log.e("ERROR", "Erreur lors de la récupération du user")
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        val buttonSignIn = view.findViewById<Button>(R.id.btnLogIn)
        val textSignUp = view.findViewById<TextView>(R.id.tvSignIn)

        buttonSignIn.setOnClickListener {
            val mail = view.findViewById<EditText>(R.id.log_username).text.toString()
            val password = view.findViewById<EditText>(R.id.log_password).text.toString()
            if (mail.isEmpty() && password.isEmpty()) {
                Toast.makeText(this.context, "Textes vides", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            getUsersInformations(mail)
                        } else {
                            // Gérez les erreurs lors de la connexion
                            // Exemple : afficher un message d'erreur à l'utilisateur
                            Toast.makeText(this.context, "Échec de la connexion", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }

        }

        textSignUp.setOnClickListener {
            val fragment = SignUp()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }
        // Inflate the layout for this fragment
        return view
    }
}