package tranquitaskstudio.project.tranquitaskapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import tranquitaskstudio.project.tranquitaskapp.data.Category
import tranquitaskstudio.project.tranquitaskapp.data.CategoryDictionary
import tranquitaskstudio.project.tranquitaskapp.data.ListTask
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.data.FriendsDictionary
import tranquitaskstudio.project.tranquitaskapp.data.Period
import tranquitaskstudio.project.tranquitaskapp.data.PeriodDictionary
import tranquitaskstudio.project.tranquitaskapp.data.Priorities
import tranquitaskstudio.project.tranquitaskapp.data.Task
import tranquitaskstudio.project.tranquitaskapp.data.User
import tranquitaskstudio.project.tranquitaskapp.firebase.MyFirebase
import tranquitaskstudio.project.tranquitaskapp.firebase.MyFirebaseAuth
import tranquitaskstudio.project.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tranquitaskstudio.project.tranquitaskapp.data.Friends


class SignIn : Fragment() {

    private val auth = MyFirebaseAuth.getFirestoreInstance()
    private val db = MyFirebase.getFirestoreInstance()

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var sharedPreferences: SharedPreferences

    private var signInButtonPressed: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
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
            User.ref = user.reference

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

            val categories = listOf(
                "sol",
                "maison",
                "arbre",
                "ciel"
            )

            for (category in categories) {
                User.bought[category] = mutableListOf()

                val boughts = user.get(category + "_bought") as List<String>
                for (bought in boughts) {
                    User.bought[category]?.add(bought)
                }

                User.decor[category] = user.getString(category) ?: ""
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
                    CategoryDictionary.nameToDocumentReference[getString(resourceId)] = categoryDoc.reference
                }
                else {
                    Log.e("ERROR", "Category name not found")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding categories : $e")
        }

        PeriodDictionary.periodToString[Period.DAY] = getString(R.string.today)
        PeriodDictionary.periodToString[Period.WEEK] = getString(R.string.this_week)
        PeriodDictionary.periodToString[Period.ALL] = getString(R.string.any_time)

        PeriodDictionary.stringToPeriod[getString(R.string.today)] = Period.DAY
        PeriodDictionary.stringToPeriod[getString(R.string.this_week)] = Period.WEEK
        PeriodDictionary.stringToPeriod[getString(R.string.any_time)] = Period.ALL

        FriendsDictionary.friendsToString[Friends.GLOBAL] = getString(R.string.global)
        FriendsDictionary.friendsToString[Friends.FRIENDS] = getString(R.string.friends)

        FriendsDictionary.stringToFriends[getString(R.string.global)] = Friends.GLOBAL
        FriendsDictionary.stringToFriends[getString(R.string.friends)] = Friends.FRIENDS

        for ((value, name) in Priorities.dictionary) {
            val id = resources.getIdentifier(name, "string", packageName)
            Priorities.dictionary[value] = getString(id)
            Priorities.reversedDictionary[getString(id)] = value
        }

        val fragment = Home()
        val slideUp = Slide(Gravity.END)
        slideUp.duration = 300 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        val buttonSignIn = view.findViewById<ImageView>(R.id.btnLogIn)
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
            logUsername.setText(storedEmail)
            logPassword.setText(storedPassword)

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
            val slideUp = Slide(Gravity.START)
            slideUp.duration = 300 // Durée de l'animation en millisecondes
            fragment.enterTransition = slideUp
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }

        forgotPassword.setOnClickListener {
            val fragment = ForgotPassword()
            val slideUp = Slide(Gravity.BOTTOM)
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