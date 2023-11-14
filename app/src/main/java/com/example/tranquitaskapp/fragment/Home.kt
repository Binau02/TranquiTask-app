package com.example.tranquitaskapp.fragment

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
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.adapter.CategoryRowAdapter
import com.example.tranquitaskapp.data.CategoryModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.ui.CircularProgressBar


class Home : Fragment() {
    private val db = MyFirebase.getFirestoreInstance()
    private val listCategoryModel = mutableListOf<CategoryModel>()
    private lateinit var rv: RecyclerView
    private lateinit var progressBar: CircularProgressBar

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

    suspend fun getTasks() {
        val categories : MutableList<Pair<DocumentReference?, MutableList<Int>>> = mutableListOf()

        // récupérer l'utilisateur
        try {
             val user = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("user").document(User.id).get())
            }
            val tasks = user.get("taches") as List<DocumentReference>
            var totalPercentage = 0F;
            for (task in tasks) {
                // récupérer chaque tâche de l'utilisateur
                try {
                    val taskDoc = withContext(Dispatchers.IO) {
                        Tasks.await(task.get())
                    }
                    val categorieRef = taskDoc.getDocumentReference("categorie")
                    val done = taskDoc.getLong("done")?.toInt()

                    var categoryExists = false
                    var index = 0
                    var i = 0
                    for (category in categories) {
                        if (category.first == categorieRef) {
                            categoryExists = true
                            index = i
                        }
                        i++
                    }
                    if (!categoryExists) {
                        index = categories.size
                        categories.add(Pair(categorieRef, mutableListOf()))
                    }
                    if (done != null) {
                        categories[index].second.add(done)
                        totalPercentage += done
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", "Error getting categorie document: $e")
                }
            }
            totalPercentage /= tasks.size
            progressBar.setPercentageExternal(totalPercentage)
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding user: $e")
        }

        for (category in categories) {
            try {
                val categorieDoc = withContext(Dispatchers.IO) {
                    Tasks.await(category.first!!.get())
                }
                val name = categorieDoc.getString("name")
                val icon = categorieDoc.getString("icon")
                if (icon != null && name != null)
                    listCategoryModel.add(CategoryModel(name, icon, category.second.sum()/category.second.size))
            } catch (e: Exception) {
                Log.e("ERROR", "Error getting categorie document: $e")
            }
        }
        rv.adapter = CategoryRowAdapter(listCategoryModel)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        rv = view.findViewById(R.id.rv)
        progressBar = view.findViewById(R.id.progressBar)
        val buttonToday = view.findViewById<Button>(R.id.todayButton)
        val buttonWeek = view.findViewById<Button>(R.id.weekButton)
        val searchBtn: com.google.android.material.floatingactionbutton.FloatingActionButton =
            view.findViewById(R.id.fab2)

//        Log.d("TEST","${User.getUser().id}")

        lifecycleScope.launch {
            getTasks()
        }

        searchBtn.setOnClickListener {
            val fragment = ListTaches()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }


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