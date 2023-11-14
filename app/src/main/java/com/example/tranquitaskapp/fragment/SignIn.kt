package com.example.tranquitaskapp.fragment

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
import androidx.appcompat.content.res.AppCompatResources
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener

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
                        User.username = username
                        User.mail = email
                        User.coins = coins
                        User.profile_picture = profile_picture
                        User.id = documents.documents[0].id
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
           /* if (checkBoxStillConnected.isChecked) {
                auth.signInWithEmailAndPassword(storedEmail, storedPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            getUsersInformations(storedEmail)
                        } else {
                            // Gérer les erreurs lors de la connexion automatique
                            Log.e("SignIn", "Échec de la connexion automatique")
                        }
                    }
            }*/
        }

        buttonSignIn.setOnClickListener {
            val mail = logUsername.text.toString()
            val password = logPassword.text.toString()

            val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning)

            if (mail.isEmpty() || password.isEmpty()) {
                if (mail.isEmpty()) {
                    logUsername.setError("Please Enter Username", icon)
                }
                if (password.isEmpty()) {
                    logPassword.setError("Please Enter Password", icon)
                }
                Toast.makeText(this.context, "Textes vides", Toast.LENGTH_SHORT).show()
            } else {
                // Vérifier si la case à cocher est cochée
                val isCheckBoxChecked = checkBoxStillConnected.isChecked

                if (isCheckBoxChecked) {
                    // Sauvegarder l'état de la case à cocher et les identifiants dans les préférences partagées
                    saveCredentialsToSharedPreferences(mail, password)
                    sharedPreferences.edit().putBoolean("checkBoxState", true).apply()
                } else {
                    // Si la case n'est pas cochée, effacer les identifiants et sauvegarder l'état dans les préférences partagées
                    clearCredentialsFromSharedPreferences()
                    sharedPreferences.edit().putBoolean("checkBoxState", false).apply()
                }

                // Connexion uniquement si l'utilisateur appuie sur le bouton de connexion
                // (et non automatiquement au démarrage de l'application)
                // Notez que la connexion automatique doit être désactivée côté Firebase (dans la console Firebase).
                // Si elle est activée, l'authentification automatique peut se produire même si le code ici l'empêche.

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