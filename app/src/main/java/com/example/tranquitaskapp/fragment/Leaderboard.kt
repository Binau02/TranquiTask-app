package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.adapter.LeaderboardRowAdapter
import com.example.tranquitaskapp.data.LeaderboardModel
import com.example.tranquitaskapp.firebase.MyFirebase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Leaderboard.newInstance] factory method to
 * create an instance of this fragment.
 */
data class UserData(val ref: DocumentReference, var totalAmount: Int, val categories : MutableList<CategorieData>)

data class CategorieData(val ref: DocumentReference, var totalAmount: Int)

data class Categorie(val ref: DocumentReference?, val name: String)

class Leaderboard : Fragment() {
    // TODO: Rename and change types of parameters
    /*
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var view: View
     */

    private val db = MyFirebase.getFirestoreInstance()
//    private val listLeaderboardModel = mutableListOf<LeaderboardModel>(
//        LeaderboardModel("Pseudo 1",R.drawable.arbre_removebg,"976", "#1"),
//        LeaderboardModel("Pseudo 2",R.drawable.leaderboard_icon, "965", "#2"),
//        LeaderboardModel("Pseudo 3",R.drawable.or, "875", "#3"),
//        LeaderboardModel("Pseudo 4",R.drawable.leaderboard_icon, "834", "#4"),
//        LeaderboardModel("Pseudo 5",R.drawable.arbre_removebg, "621", "#5"),
//        LeaderboardModel("Pseudo 6",R.drawable.add, "608", "#6"),
//        LeaderboardModel("Pseudo 7",R.drawable.or, "487", "#7"),
//        LeaderboardModel("Pseudo 8",R.drawable.leaderboard_icon, "435", "#8"),
//        LeaderboardModel("Pseudo 9",R.drawable.add, "384", "#9"),
//        LeaderboardModel("Pseudo 10",R.drawable.arbre_removebg, "104", "#10"),
//    )
    private var listLeaderboardModel = mutableListOf<LeaderboardModel>()


    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var rv : RecyclerView
    private lateinit var textCategorie : TextView

    private val users : MutableList<UserData> = mutableListOf()

    private val globalCategories: MutableList<Categorie> = mutableListOf()
    private var categorieIndex: Int = 0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    fun onClickFiltre(){
        Toast.makeText(this.context, "Le bouton Filtre a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickChangeLeft(){
//        Toast.makeText(this.context, "Le bouton < a été cliqué !", Toast.LENGTH_SHORT).show()
        categorieIndex--
        if (categorieIndex == -1) {
            categorieIndex = globalCategories.size - 1
        }
        lifecycleScope.launch {
            setLeaderboard()
        }
    }
    fun onClickChangeRight(){
//        Toast.makeText(this.context, "Le bouton > a été cliqué !", Toast.LENGTH_SHORT).show()
        categorieIndex++
        if (categorieIndex == globalCategories.size) {
            categorieIndex = 0
        }
        lifecycleScope.launch {
            setLeaderboard()
        }
    }
    fun onClickText(){
//        Toast.makeText(this.context, "Le texte a été cliqué !", Toast.LENGTH_SHORT).show()
        categorieIndex = 0
        lifecycleScope.launch {
            setLeaderboard()
        }
    }

    suspend fun getLeaderboard() {
        val emptyCategories : MutableList<CategorieData> = mutableListOf()

        globalCategories.add(Categorie(null, "Global"))

        try {
            val categories = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("tache_categorie").get())
            }
            for (category in categories) {
                val name = category.getString("name")
                val categoryRef = category.reference
                if (name != null) {
                    emptyCategories.add(CategorieData(categoryRef, 0))
                    globalCategories.add(Categorie(categoryRef, name))
                }
            }
        } catch (e : Exception) {
            Log.e("ERROR", "error in getting categories document: $e")
        }
        try {
            val transactions = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("transaction").get())
            }
            for (transaction in transactions) {
                val amount = transaction.getLong("amount")?.toInt()
                val date = transaction.getDate("date")
                val userRef = transaction.getDocumentReference("user")
                val categorieRef = transaction.getDocumentReference("categorie")

                if (userRef != null && amount != null) {
                    // Check if the user already exists in the list
                    val existingUser = users.find { it.ref == userRef }

                    if (existingUser != null) {
                        // Update the total amount for the existing user
                        existingUser.totalAmount += amount
                        val categorieAmount = existingUser.categories.find { it.ref == categorieRef }?.totalAmount
                        if (categorieAmount != null) {
                            existingUser.categories.find { it.ref == categorieRef }?.totalAmount = categorieAmount + amount
                        }
                    }
                    else {
                        // Add a new user to the list
                        val newList = emptyCategories.map { it.copy() }.toMutableList()
                        newList.find { it.ref == categorieRef }?.totalAmount = amount
                        users.add(UserData(userRef, amount, newList))
                    }
                }
            }
            onClickText()
        } catch (e : Exception) {
            Log.e("ERROR", "error getting transactions : $e")
        }
    }

    suspend fun setLeaderboard() {
        textCategorie.text = globalCategories[categorieIndex].name

        listLeaderboardModel = mutableListOf()

        Log.d("TEST", "actual cat ${globalCategories[categorieIndex]}")
        for (user in users) {
            for (category in user.categories) {
                Log.d("TEST", "${user.totalAmount} cats ${category}")
            }
        }

        if (globalCategories[categorieIndex].name == "Global") {
            users.sortByDescending { it.totalAmount }
        }
        else {
            users.sortByDescending { user -> user.categories.find { it.ref == globalCategories[categorieIndex].ref }?.totalAmount }
        }

        var i = 0
        for (user in users) {
            i++
            try {
                val userDoc = withContext(Dispatchers.IO) {
                    Tasks.await(user.ref.get())
                }
                val pseudo = userDoc.getString("username")
                val pp = userDoc.getString("profile_picture")
                var amount = 0
                if (globalCategories[categorieIndex].name == "Global") {
                    amount = user.totalAmount
                }
                else {
                    amount = user.categories.find { it.ref == globalCategories[categorieIndex].ref }?.totalAmount
                        ?: 0
                }
                if (pseudo != null && pp != null) {
                    listLeaderboardModel.add(
                        LeaderboardModel(
                            pseudo,
                            R.drawable.or,
                            amount.toString(),
                            "#" + i.toString()
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error getting categorie document: $e")
            }
        }
        rv.adapter = LeaderboardRowAdapter(listLeaderboardModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        rv = view.findViewById(R.id.rv_leaderboard)
        val buttonFiltre = view.findViewById<Button>(R.id.filtre)
        val buttonChangeL = view.findViewById<Button>(R.id.fleche_gauche)
        val buttonChangeR = view.findViewById<Button>(R.id.fleche_droite)
        textCategorie = view.findViewById<TextView>(R.id.text_middle_categorie)

        lifecycleScope.launch {
            getLeaderboard()
        }

        buttonFiltre.setOnClickListener {
            onClickFiltre()
        }
        buttonChangeL.setOnClickListener {
            onClickChangeLeft()
        }
        buttonChangeR.setOnClickListener {
            onClickChangeRight()
        }
        textCategorie.setOnClickListener {
            onClickText()
        }


        rv.layoutManager = LinearLayoutManager(requireContext())
//        rv.adapter = LeaderboardRowAdapter(listLeaderboardModel) // Initialisez avec une liste vide ou vos données

        //loadRecyclerViewData(rv) // Chargez les données dans la RecyclerView

        return view
        // Inflate the layout for this fragment
    }

    /*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Leaderboard.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Leaderboard().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

     */
}