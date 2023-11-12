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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.adapter.CategoryRowAdapter
import com.example.tranquitaskapp.data.CategoryModel
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener


class Home : Fragment() {
    private val db = MyFirebase.getFirestoreInstance()
    private val listCategoryModel = mutableListOf<CategoryModel>()
    private lateinit var rv: RecyclerView

    private var bottomBarListener: BottomBarVisibilityListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    fun onClickToday() {
        Toast.makeText(this.context, "Le bouton aujourd'hui a été cliqué!", Toast.LENGTH_SHORT)
            .show()
    }

    fun onClickWeek() {
        Toast.makeText(this.context, "Le bouton semaine a été cliqué!", Toast.LENGTH_SHORT).show()
    }

    fun getCategories() {
        db.collection("tache_categorie")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val icon = document.getString("icon")
                    val name = document.getString("name")
                    if (icon != null && name != null)
                        listCategoryModel.add(CategoryModel(name, icon, 75))
                }

                // Mettre à jour l'adaptateur une fois que les données sont récupérées avec succès
                rv.adapter = CategoryRowAdapter(listCategoryModel)
            }
            .addOnFailureListener { exception ->
                // Gérer les erreurs éventuelles
                Log.e("ERROR", "Erreur lors de la récupération des catégories: $exception")
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        rv = view.findViewById(R.id.rv)
        val buttonToday = view.findViewById<Button>(R.id.todayButton)
        val buttonWeek = view.findViewById<Button>(R.id.weekButton)
        val addBtn: com.google.android.material.floatingactionbutton.FloatingActionButton =
            view.findViewById(R.id.fab1)
        val searchBtn: com.google.android.material.floatingactionbutton.FloatingActionButton =
            view.findViewById(R.id.fab2)

        getCategories()
        addBtn.setOnClickListener {
            val fragment = AddTask()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }
        /**
        searchBtn.setOnClickListener {
            val fragment = Connection()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }
        */

        buttonToday.setOnClickListener {
            onClickToday()
        }
        buttonWeek.setOnClickListener {
            onClickWeek()
        }
        rv.layoutManager = LinearLayoutManager(requireContext())

        return view
        // Inflate the layout for this fragment
    }

}