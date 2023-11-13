package com.example.tranquitaskapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.adapter.LeaderboardRowAdapter
import com.example.tranquitaskapp.adapter.ListeTachesRowAdapter
import com.example.tranquitaskapp.data.ListeTachesModel
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener


/**
 * A simple [Fragment] subclass.
 * Use the [fragment_liste_taches.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_liste_taches : Fragment() {

    private var bottomBarListener: BottomBarVisibilityListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    private val db = MyFirebase.getFirestoreInstance()
    private val listListeTacheModel = mutableListOf<ListeTachesModel>(
        ListeTachesModel("Tâche 1",R.drawable.arbre_removebg,50, false, 30, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 2",R.drawable.leaderboard_icon, 45, false,60, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 3",R.drawable.or, 90, false,50, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 4",R.drawable.leaderboard_icon, 40, false,30, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 5",R.drawable.arbre_removebg, 40, false,30, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 6",R.drawable.add, 30, false,30, "3/12/2023","haute","maison"),
        ListeTachesModel("Tâche 7",R.drawable.or, 60, false,30, "3/12/2023","haute","maison"),
    )
    fun onClickFiltre(){
        Toast.makeText(this.context, "Le bouton Filtre a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickBack(){
        val fragment = Home()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liste_taches, container, false)
        val rv: RecyclerView = view.findViewById(R.id.rv_liste_tache)
        val buttonFiltre = view.findViewById<Button>(R.id.filtre)
        val buttonBack = view.findViewById<Button>(R.id.back)

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