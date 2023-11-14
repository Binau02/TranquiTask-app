package com.example.tranquitaskapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.adapter.ListeTachesRowAdapter
import com.example.tranquitaskapp.data.ListeTachesModel
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [ListTaches.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListTaches : Fragment() {

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var rv : RecyclerView


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    private val db = MyFirebase.getFirestoreInstance()
    /*
    private val listListeTacheModel = mutableListOf<ListeTachesModel>(
        ListeTachesModel("Tâche 1",R.drawable.arbre_removebg,50, false, 30, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 2",R.drawable.leaderboard_icon, 45, false,60, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 3",R.drawable.or, 90, false,50, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 4",R.drawable.leaderboard_icon, 40, false,30, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 5",R.drawable.arbre_removebg, 40, false,30, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 6",R.drawable.add, 30, false,30, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 7",R.drawable.or, 60, false,30, "3/12/2023","haute","maison"),
    )*/
    private val listListeTacheModel = mutableListOf<ListeTachesModel>()

    fun onClickFiltre(){
        Toast.makeText(this.context, "Le bouton Filtre a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickBack(){
        val fragment = Home()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    suspend fun getTasks() {
        try {
            val user = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("user").document(User.id).get())
            }
            val tasks = user.get("taches") as List<DocumentReference>
            for (task in tasks) {
                // récupérer chaque tâche de l'utilisateur
                try {
                    val taskDoc = withContext(Dispatchers.IO) {
                        Tasks.await(task.get())
                    }
                    val name = taskDoc.getString("name")
                    val categorieRef = taskDoc.getDocumentReference("categorie")
                    val done = taskDoc.getLong("done")?.toInt()
                    val duree = taskDoc.getLong("duree")?.toInt()
                    val deadlineTimestamp = taskDoc.getTimestamp("deadline")
                    val prioriteRef = taskDoc.getDocumentReference("priorite")
                    lateinit var categoryName : String
                    lateinit var prioriteName : String

                    if (categorieRef != null) {
                        try {
                            val categorieDoc = withContext(Dispatchers.IO) {
                                Tasks.await(categorieRef.get())
                            }
                            categoryName = categorieDoc.getString("name").toString()
                        } catch (e: Exception) {
                            Log.e("ERROR", "Error getting categorie document: $e")
                        }
                    }
                    if (prioriteRef != null) {
                        try {
                            val prioriteDoc = withContext(Dispatchers.IO) {
                                Tasks.await(prioriteRef.get())
                            }
                            prioriteName = prioriteDoc.getString("name").toString()
                        } catch (e: Exception) {
                            Log.e("ERROR", "Error getting priorite document: $e")
                        }
                    }
                    if (name != null && done != null && duree != null && deadlineTimestamp != null) {
                        val deadlineDate = deadlineTimestamp.toDate()
//                        Log.d("TEST", "date $deadline")
                        listListeTacheModel.add(
                            ListeTachesModel(
                                name,
                                R.drawable.or,
                                done,
                                false,
                                duree,
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(deadlineDate),
                                prioriteName,
                                categoryName
                            )
                        )
                    }

                } catch (e: Exception) {
                    Log.e("ERROR", "Error getting task document: $e")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding user: $e")
        }
        rv.adapter = ListeTachesRowAdapter(listListeTacheModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liste_taches, container, false)
        rv = view.findViewById(R.id.rv_liste_tache)
        val buttonFiltre = view.findViewById<Button>(R.id.filtre)
        val buttonBack = view.findViewById<Button>(R.id.back)

        lifecycleScope.launch {
            getTasks()
        }

        buttonFiltre.setOnClickListener {
            onClickFiltre()
        }
        buttonBack.setOnClickListener {
            onClickBack()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = ListeTachesRowAdapter(listListeTacheModel) // Initialisez avec une liste vide ou vos données

        //loadRecyclerViewData(rv) // Chargez les données dans la RecyclerView

        return view
        // Inflate the layout for this fragment
    }
}