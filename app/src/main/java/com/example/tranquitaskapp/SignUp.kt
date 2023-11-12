package com.example.tranquitaskapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.regex.RegexPatterns


/**
 * A simple [Fragment] subclass.
 * Use the [SignUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUp : Fragment() {
    private val auth = MyFirebaseAuth.getFirestoreInstance()
    private val db = MyFirebase.getFirestoreInstance()

    fun onClickSignIn(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // L'utilisateur est enregistré avec succès
                    User.getUser().mail = email
                    User.getUser().username = username
                    createNewUser(email,username)
                } else {
                    Toast.makeText(this.context, "Il y a eu une erreur", Toast.LENGTH_SHORT).show()
                }
            }

    }

    fun createNewUser(email: String, username: String) {
       val usersCollection = db.collection("user")
        usersCollection.whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val userData = hashMapOf(
                        "coins" to 0,
                        "profile_picture" to "",
                        "email" to email,
                        "demandes" to emptyList<String>(), // Champ avec une liste vide
                        "username" to username,
                        "sol" to "",
                        "taches" to emptyList<String>()
                    )
                    usersCollection.add(userData)
                        .addOnSuccessListener { documentReference ->
                            Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                        }
                } else {
                    Toast.makeText(this.context, "Il y a eu deja ce pseudo", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        val buttonSignUp = view.findViewById<Button>(R.id.btnSignUp)

        buttonSignUp.setOnClickListener {
            val mail = view.findViewById<EditText>(R.id.reg_mail).text.toString()
            val username = view.findViewById<EditText>(R.id.reg_username).text.toString()
            val password = view.findViewById<EditText>(R.id.reg_password).text.toString()
            val confirmPassword =
                view.findViewById<EditText>(R.id.reg_passwordconfirm).text.toString()
            Log.d("TEST", "Je suis bien co")
            if (
                password.matches(Regex(RegexPatterns.PASSWORD_PATTERN)) &&
                mail.matches(Regex(RegexPatterns.MAIL_PATTERN)) &&
                password == confirmPassword &&
                username != ""
            ) {
                db.collection("user").whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            onClickSignIn(mail, password, username)
                        } else {
                            Toast.makeText(this.context, "Il y a eu deja ce pseudo", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this.context, "", Toast.LENGTH_SHORT)
                    .show()
                Log.d("TEST", "$password,$confirmPassword,$mail,$username")
            }
        }
        // Inflate the layout for this fragment
        return view
    }
}