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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.CategoryDictionnary
import com.example.tranquitaskapp.ListTask
import com.example.tranquitaskapp.Period
import com.example.tranquitaskapp.PriorityDictionnary
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.User
import com.example.tranquitaskapp.adapter.ListeTachesRowAdapter
import com.example.tranquitaskapp.data.TacheModel
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.interfaces.TaskButtonClickListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.tranquitaskapp.Task


/**
 * A simple [Fragment] subclass.
 * Use the [ListTaches.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListTaches : Fragment(), TaskButtonClickListener {

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var rv : RecyclerView

    override fun onStartButtonClick(position: Int) {
//        val fragment = StartTask(ListeTacheModel[position]) // Remplacez par le fragment que vous souhaitez afficher
//        val transaction = fragmentManager?.beginTransaction()
//        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

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
//    private val ListeTacheModel = mutableListOf<TacheModel>()

    fun onClickFiltre(){
        Toast.makeText(this.context, "Le bouton Filtre a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickBack(){
        val fragment = Home()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }


    private fun setTasks(
        period : Period = Period.ALL,
        category: DocumentReference? = null,
        priority: DocumentReference? = null
    ) {
        var tasks : List<Task>

        val home = Home()

        // filter by period
        if (period == Period.DAY) {
            tasks = ListTask.list.filter { task -> home.isToday(task.deadline) }
        }
        else if (period == Period.WEEK) {
            tasks = ListTask.list.filter { task -> home.isOnWeek(task.deadline) }
        }
        else {
            tasks = ListTask.list
        }

        // filter by category
        if (category != null) {
            tasks = tasks.filter { task -> task.categorie == category }
        }

        // filter by priority
        if (priority != null) {
            tasks = tasks.filter { task -> task.priorite == priority }
        }

        val ListeTacheModel = mutableListOf<TacheModel>()

        val resources = context?.resources
        val packageName = context?.packageName

        for (task in tasks) {
            val taskCategory = CategoryDictionnary.dictionary.get(task.categorie)
            val taskPriority = PriorityDictionnary.dictionary.get(task.priorite)
            val resourceId = resources?.getIdentifier(taskCategory?.icon ?: "", "drawable", packageName)

            if (taskCategory != null && taskPriority != null && resourceId != null && task.deadline != null) {
                ListeTacheModel.add(
                    TacheModel(
                        "",
                        task.name,
                        resourceId,
                        task.done,
                        false,
                        task.duree,
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(task.deadline!!.toDate()),
                        taskPriority.name,
                        taskCategory.name
                    )
                )
            }
        }

        rv.adapter = ListeTachesRowAdapter(ListeTacheModel, this){
            val fragment = ListTaches()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liste_taches, container, false)
        rv = view.findViewById(R.id.rv_liste_tache)
        val buttonFiltre = view.findViewById<Button>(R.id.filtre)
        val buttonBack = view.findViewById<Button>(R.id.back)

        setTasks()

        buttonFiltre.setOnClickListener {
            onClickFiltre()
        }
        buttonBack.setOnClickListener {
            onClickBack()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
//        rv.adapter = ListeTachesRowAdapter(ListeTacheModel,this){
//            val fragment = ListTaches()
//            val transaction = fragmentManager?.beginTransaction()
//            transaction?.replace(R.id.frameLayout, fragment)?.commit()
//        } // Initialisez avec une liste vide ou vos données

        //loadRecyclerViewData(rv) // Chargez les données dans la RecyclerView

        return view
        // Inflate the layout for this fragment
    }
}