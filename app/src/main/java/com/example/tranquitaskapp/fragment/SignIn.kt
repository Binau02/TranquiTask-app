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
import androidx.lifecycle.lifecycleScope
import com.example.tranquitaskapp.data.Category
import com.example.tranquitaskapp.data.CategoryDictionary
import com.example.tranquitaskapp.data.ListTask
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.Task
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    // unused, TODO : @Hugo check si tu veux garder des trucs, et tu peux delete
    fun getUsersInformations(email: String) {
        db.collection("user").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    val username = documents.documents[0].getString("username")

                    val coins = documents.documents[0].getLong("coins")
                    val profilePicture = documents.documents[0].getString("profile_picture")
                    if (username != null && coins != null && profilePicture != null) {
                        User.username = username
                        User.mail = email
                        User.coins = coins
                        User.profile_picture = profilePicture
                        User.id = documents.documents[0].id
                    }
                    val fragment = Home()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameLayout, fragment)?.commit()
                }

            }
            .addOnFailureListener { exception ->
                // Gérer les erreurs éventuelles
                Log.e("ERROR", "Erreur lors de la récupération du user : $exception")
            }
    }

    private suspend fun getInformations(email : String) {
        val packageName = this.context?.packageName

        // récupérer l'utilisateur
        try {
            val userDocs = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("user").whereEqualTo("email", email).get())
            }
            val user = userDocs.documents[0]

            User.username = user.getString("username") ?: ""
            User.mail = email
            User.coins = user.getLong("coins") ?: 0
            User.profile_picture = user.getString("profile_picture") ?: ""
            User.id = user.id
            val tasks = user.get("taches") as List<DocumentReference>
            // récupérer chaque tâche de l'utilisateur
            for (task in tasks) {
                try {
                    val taskDoc = withContext(Dispatchers.IO) {
                        Tasks.await(task.get())
                    }
                    val home = Home()

                    val newTask = Task (
                        name = taskDoc.getString("name") ?: "",
                        concentration = taskDoc.getBoolean("concentration") ?: false,
                        divisible = taskDoc.getBoolean("divisible") ?: false,
                        done = (taskDoc.getLong("done") ?: 0).toInt(),
                        duree = (taskDoc.getLong("duree") ?: 0).toInt(),
                        deadline = taskDoc.getTimestamp("deadline"),
                        categorie = taskDoc.getDocumentReference("categorie"),
                        priorite = (taskDoc.getLong("priorite") ?: 0).toInt(),
                        ref = taskDoc.reference
                    )

                    if (!home.isOnWeek(newTask.deadline) && newTask.done == 100) {
                        val documentReference: DocumentReference = taskDoc.reference

                        documentReference.let { reference ->
                            reference.delete()
                                .addOnSuccessListener {}
                                .addOnFailureListener { e ->
                                    Log.e("ERROR", "Error deleting document", e)
                                }
                        }

                        val taskArray = user.get("taches") as? ArrayList<DocumentReference>
                        taskArray?.remove(documentReference)
                        user.reference.update("taches", taskArray).addOnFailureListener { e ->
                            Log.e("ERROR", "Error updating user", e)
                        }
                    }
                    else {
                        ListTask.list.add(newTask)
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", "Error getting task document: $e")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding user: $e")
        }

        try {
            val categoryDocs = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("tache_categorie").get())
            }
            for (categoryDoc in categoryDocs) {
                val resourceId = resources.getIdentifier(categoryDoc.getString("name") ?: "", "string", packageName)
                if (resourceId != 0) {
                    val category = Category(
                        name = getString(resourceId),
                        icon = categoryDoc.getString("icon") ?: ""
                    )
                    CategoryDictionary.dictionary[categoryDoc.reference] = category
                }
                else {
                    Log.e("ERROR", "Ctegory name not found")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding categories : $e")
        }

//        try {
//            val priorityDocs = withContext(Dispatchers.IO) {
//                Tasks.await(db.collection("tache_priorite").get())
//            }
//            for (priorityDoc in priorityDocs) {
//                val priority = Priority (
//                    name = priorityDoc.getString("name") ?: "",
//                    value = priorityDoc.getLong("value")?.toInt() ?: 0
//                )
//                PriorityDictionnary.dictionary.put(priorityDoc.reference, priority)
//            }
//        } catch (e: Exception) {
//            Log.e("ERROR", "Error finding priorities : $e")
//        }

        val fragment = Home()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        val buttonSignIn = view.findViewById<Button>(R.id.btnLogIn)
        val textSignUp = view.findViewById<TextView>(R.id.tvSignIn)
        val logUsername = view.findViewById<EditText>(R.id.log_username)
        val forgotPassword = view.findViewById<TextView>(R.id.tvForgotPassword)
        val logPassword = view.findViewById<EditText>(R.id.log_password)

        // Vérifier s'il existe des identifiants stockés
        val storedEmail = sharedPreferences.getString("email", null)
        val storedPassword = sharedPreferences.getString("password", null)
        val checkBoxStillConnected = view.findViewById<CheckBox>(R.id.checkBoxStillConnected)

        // Charger l'état de la case à cocher depuis les préférences partagées
        var isCheckBoxChecked = sharedPreferences.getBoolean("checkBoxState", false)
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
                isCheckBoxChecked = checkBoxStillConnected.isChecked

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
//                            getUsersInformations(mail)
                            lifecycleScope.launch {
                                getInformations(mail)
                            }
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

        forgotPassword.setOnClickListener {
            val fragment = ForgotPassword()
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