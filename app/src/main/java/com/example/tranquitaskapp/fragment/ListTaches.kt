package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
class ListTaches (private val period: Period = Period.ALL, private val category: DocumentReference? = null, private val priority: DocumentReference? = null) : Fragment(), TaskButtonClickListener {

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


    fun onClickFiltre(){
        Toast.makeText(this.context, "Le bouton Filtre a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickBack(){
        val fragment = Home()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }


    private fun setTasks(search : String = "") {
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

        // filter by searching
        if (search != "") {
            tasks = tasks.filter { task -> task.name.contains(search) }
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
            val fragment = ListTaches(period, category, priority)
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
        val searchBar = view.findViewById<EditText>(R.id.search_bar)

        buttonFiltre.setOnClickListener {
            onClickFiltre()
        }
        buttonBack.setOnClickListener {
            onClickBack()
        }
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                setTasks(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {
//                val searchText = editable.toString()
//                onSearchTextChanged(searchText)
            }
        })

        rv.layoutManager = LinearLayoutManager(requireContext())

        setTasks()

        //loadRecyclerViewData(rv) // Chargez les données dans la RecyclerView

        return view
        // Inflate the layout for this fragment
    }
}