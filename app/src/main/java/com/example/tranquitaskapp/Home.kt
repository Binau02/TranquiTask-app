package com.example.tranquitaskapp

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
import com.example.tranquitaskapp.adapter.CategoryRowAdapter
import com.example.tranquitaskapp.data.CategoryModel


class Home : Fragment() {
    private val db = MyFirebase.getFirestoreInstance()
    private val listCategoryModel = mutableListOf<CategoryModel>(
        CategoryModel("Sport",R.drawable.add,50),
        CategoryModel("Maison",R.drawable.home_icon,75),
        CategoryModel("Etudes",R.drawable.friends_icon,25),
        CategoryModel("Jeux",R.drawable.leaderboard_icon,100),
        CategoryModel("Sport",R.drawable.add,50),
        CategoryModel("Maison",R.drawable.home_icon,75),
        CategoryModel("Etudes",R.drawable.friends_icon,25),
        CategoryModel("Jeux",R.drawable.leaderboard_icon,100),
        CategoryModel("Sport",R.drawable.add,50),
        CategoryModel("Maison",R.drawable.home_icon,75),
        CategoryModel("Etudes",R.drawable.friends_icon,25),
        CategoryModel("Jeux",R.drawable.leaderboard_icon,100),
    )

    fun onClickToday(){
        Toast.makeText(this.context, "Le bouton aujourd'hui a été cliqué!", Toast.LENGTH_SHORT).show()
    }
    fun onClickWeek(){
        Toast.makeText(this.context, "Le bouton semaine a été cliqué!", Toast.LENGTH_SHORT).show()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val rv: RecyclerView = view.findViewById(R.id.rv)
        val buttonToday = view.findViewById<Button>(R.id.todayButton)
        val buttonWeek = view.findViewById<Button>(R.id.weekButton)

        buttonToday.setOnClickListener {
            onClickToday()
        }
        buttonWeek.setOnClickListener {
            onClickWeek()
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = CategoryRowAdapter(listCategoryModel) // Initialisez avec une liste vide ou vos données

        //loadRecyclerViewData(rv) // Chargez les données dans la RecyclerView

        return view
        // Inflate the layout for this fragment

    }

    /**
     * EXEMPLE DE GET
     *
     *
     * db.collection("item")
     *             .get()
     *             .addOnSuccessListener { documents ->
     *                 for (document in documents) {
     *                     val image = document.getString("image")
     *                     val name = document.getString("name")
     *                     val type = document.getDocumentReference("type")
     *                     Log.d("TEST","Document->$image;$name;$type")
     *
     *                     val docRef = type?.let { db.collection("item_categorie").document(it.id) }
     *
     *                     docRef?.get()?.addOnSuccessListener { documenTr ->
     *                         if (documenTr.exists()) {
     *                             val cadre = documenTr.getString("name")
     *                             // Le document existe, vous pouvez obtenir ses données
     *                             Log.d("TEST","$cadre")
     *                             // Faites quelque chose avec votreObjet
     *                         } else {
     *                             // Le document n'existe pas
     *                             Log.d("TEST","Ta mère")
     *                         }
     *                     }
     *                 }
     *             }
     *             .addOnFailureListener { exception ->
     *                 Log.w("TEST", "Error getting documents: ", exception)
     *             }
     *         return inflater.inflate(R.layout.fragment_home, container, false)
     */
}