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
import com.example.tranquitaskapp.Category
import com.example.tranquitaskapp.CategoryDictionnary
import com.example.tranquitaskapp.ListTask
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.Task
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
import java.util.Calendar


class Home : Fragment() {
    private val db = MyFirebase.getFirestoreInstance()
    private val listCategoryModel = mutableListOf<CategoryModel>()
    private lateinit var rv: RecyclerView
    private lateinit var progressBar: CircularProgressBar

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private var day : Boolean = true


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    fun onClickToday() {
//        Toast.makeText(this.context, "Le bouton aujourd'hui a été cliqué!", Toast.LENGTH_SHORT)
//            .show()
        if (!day) {
            day = true
            setTasks(day)
        }
    }

    fun onClickWeek() {
//        Toast.makeText(this.context, "Le bouton semaine a été cliqué!", Toast.LENGTH_SHORT).show()
        if (day) {
            day = false
            setTasks(day)
        }
    }


    private fun setTasks(today: Boolean = true) {
        listCategoryModel.clear()

        val categories : MutableList<Pair<DocumentReference?, MutableList<Int>>> = mutableListOf()

        val tasks : List<Task>

        if (today) {
            tasks = ListTask.list.filter { task -> isToday(task.deadline) }
        }
        else {
            tasks = ListTask.list.filter { task -> isOnWeek(task.deadline) }
        }

        var totalPercentage = 0F;

        for (task in tasks) {
            var categoryExists = false
            var index = 0
            var i = 0
            for (category in categories) {
                if (category.first == task.categorie) {
                    categoryExists = true
                    index = i
                }
                i++
            }
            if (!categoryExists) {
                index = categories.size
                categories.add(Pair(task.categorie, mutableListOf()))
            }
            categories[index].second.add(task.done)
            totalPercentage += task.done
        }

        totalPercentage /= tasks.size
        progressBar.setPercentageExternal(totalPercentage.toInt().toFloat())

        for (category in categories) {
            val actualCategory : Category? = CategoryDictionnary.dictionary.get(category.first)
            if (actualCategory != null) {
                listCategoryModel.add(
                    CategoryModel(
                        actualCategory.name,
                        actualCategory.icon,
                        category.second.sum() / category.second.size
                    )
                )
            }
        }
        rv.adapter = CategoryRowAdapter(listCategoryModel)
    }

    public fun isToday(date : com.google.firebase.Timestamp?) : Boolean {
        if (date == null) {
            return false
        }

        val currentDate = Calendar.getInstance()

        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)

        val endOfDay = Calendar.getInstance()
        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)
        endOfDay.set(Calendar.SECOND, 59)
        endOfDay.set(Calendar.MILLISECOND, 999)

        return date.toDate() in currentDate.time..endOfDay.time
    }

    public fun isOnWeek(date : com.google.firebase.Timestamp?) : Boolean {
        if (date == null) return false

        val currentDate = Calendar.getInstance()

        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)

        val endOfWeek = currentDate.clone() as Calendar
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6) // Sunday
        endOfWeek.set(Calendar.HOUR_OF_DAY, 23)
        endOfWeek.set(Calendar.MINUTE, 59)
        endOfWeek.set(Calendar.SECOND, 59)
        endOfWeek.set(Calendar.MILLISECOND, 999)

        return date.toDate() in currentDate.time..endOfWeek.time
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


//        lifecycleScope.launch {
//            getTasks()
//        }
        setTasks()

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