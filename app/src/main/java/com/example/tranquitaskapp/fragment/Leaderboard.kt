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
import com.example.tranquitaskapp.CategoryDictionnary
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

class Leaderboard : Fragment() {
    // TODO: Rename and change types of parameters
    /*
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var view: View
     */

    private val db = MyFirebase.getFirestoreInstance()

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var rv : RecyclerView
    private lateinit var textCategorie : TextView

    private val globalCategories: MutableList<Pair<String, DocumentReference?>> = mutableListOf()
    private var categorieIndex: Int = 0

    private val leaderboard: HashMap<DocumentReference?, List<LeaderboardModel>> = hashMapOf()

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
        setLeaderboard()
    }
    fun onClickChangeRight(){
//        Toast.makeText(this.context, "Le bouton > a été cliqué !", Toast.LENGTH_SHORT).show()
        categorieIndex++
        if (categorieIndex == globalCategories.size) {
            categorieIndex = 0
        }
        setLeaderboard()
    }
    fun onClickText(){
//        Toast.makeText(this.context, "Le texte a été cliqué !", Toast.LENGTH_SHORT).show()
        categorieIndex = 0
        setLeaderboard()
    }

    // TODO : modify with local categories
    suspend fun getLeaderboard() {
        val competitors : HashMap<DocumentReference?, HashMap<DocumentReference, LeaderboardModel>> = hashMapOf()

        competitors[null] = hashMapOf()
        globalCategories.add(Pair("Global", null))

        for ((categoryRef, category) in CategoryDictionnary.dictionary) {
            competitors[categoryRef] = hashMapOf()
            globalCategories.add((Pair(category.name, categoryRef)))
        }

        try {
            val transactions = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("transaction").get())
            }
            for (transaction in transactions) {
                val amount = (transaction.getLong("amount") ?: 0).toInt()
                val date = transaction.getDate("date")
                val userRef = transaction.getDocumentReference("user")
                val categorieRef = transaction.getDocumentReference("categorie")

                if (userRef != null) {
                    val refs = listOf(null, categorieRef)
                    for (ref in refs) {
                        if (competitors[ref] != null) {
                            if (competitors[ref]!!.containsKey(userRef)) {
                                competitors[ref]!![userRef]!!.coin = competitors[ref]!![userRef]!!.coin + amount
                            } else {
                                try {
                                    val user = withContext(Dispatchers.IO) {
                                        Tasks.await(userRef.get())
                                    }
                                    competitors[ref]!![userRef] = LeaderboardModel(
                                        pseudo = user.getString("username") ?: "",
                                        avatar = user.getString("profile_picture") ?: "",
                                        coin = amount,
                                        rank = ""
                                    )
                                } catch (e : Exception) {
                                    Log.e("ERROR", "Error getting user : $e")
                                }
                            }
                        }
                    }
                }
            }
            onClickText()
        } catch (e : Exception) {
            Log.e("ERROR", "error getting transactions : $e")
        }

        for ((category, categoryCompetitors) in competitors) {
            leaderboard[category] = categoryCompetitors.values.sortedByDescending { it.coin }.take(10)
        }

        setLeaderboard()
    }

    fun setLeaderboard() {
        textCategorie.text = globalCategories[categorieIndex].first

        rv.adapter = LeaderboardRowAdapter(leaderboard[globalCategories[categorieIndex].second] ?: listOf(), this)
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