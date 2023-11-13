package com.example.tranquitaskapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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
    private lateinit var sharedPreferences: SharedPreferences


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)


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
                        User.getUser().id = documents.documents[0].id
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
        val logUsername = view.findViewById<EditText>(R.id.log_username)
        val logPassword = view.findViewById<EditText>(R.id.log_password)

        // Vérifier s'il existe des identifiants stockés
        val storedEmail = sharedPreferences.getString("email", null)
        val storedPassword = sharedPreferences.getString("password", null)
        val checkBoxStillConnected = view.findViewById<CheckBox>(R.id.checkBoxStillConnected)

        // Charger l'état de la case à cocher depuis les préférences partagées
        val isCheckBoxChecked = sharedPreferences.getBoolean("checkBoxState", false)
        checkBoxStillConnected.isChecked = isCheckBoxChecked

        if (storedEmail != null && storedPassword != null) {
            // Remplir automatiquement les champs
            logUsername.setText(storedEmail)
            logPassword.setText(storedPassword)

            // Tenter une connexion automatique si la checkbox est cochée
            if (checkBoxStillConnected.isChecked) {
                auth.signInWithEmailAndPassword(storedEmail, storedPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            getUsersInformations(storedEmail)
                        } else {
                            // Gérer les erreurs lors de la connexion automatique
                            Log.e("SignIn", "Échec de la connexion automatique")
                        }
                    }
            }
        }

        buttonSignIn.setOnClickListener {
            val mail = logUsername.text.toString()
            val password = logPassword.text.toString()

            if (mail.isEmpty() && password.isEmpty()) {
                Toast.makeText(this.context, "Textes vides", Toast.LENGTH_SHORT).show()
            } else {
                // Vérifier si la case à cocher est cochée
                val checkBoxStillConnected = view.findViewById<CheckBox>(R.id.checkBoxStillConnected)
                val isCheckBoxChecked = checkBoxStillConnected.isChecked

                if (isCheckBoxChecked) {
                    // Sauvegarder les identifiants dans les préférences partagées
                    saveCredentialsToSharedPreferences(mail, password)
                } else {
                    // Si la case n'est pas cochée, effacer les identifiants des préférences partagées
                    clearCredentialsFromSharedPreferences()
                }

                // Tenter la connexion
                auth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            getUsersInformations(mail)
                        } else {
                            // Gérer les erreurs lors de la connexion
                            Log.e("SignIn", "Échec de la connexion")
                        }
                    }
            }
        }

        textSignUp.setOnClickListener {
            val fragment = SignUp()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }

        return view
    }


    private fun saveCredentialsToSharedPreferences(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun clearCredentialsFromSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.apply()
    }
}