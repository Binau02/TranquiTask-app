package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.data.Category
import com.example.tranquitaskapp.data.CategoryDictionary
import com.example.tranquitaskapp.data.ListTask
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.Task
import com.example.tranquitaskapp.adapter.CategoryRowAdapter
import com.example.tranquitaskapp.data.CategoryModel
import com.google.firebase.firestore.DocumentReference
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.ui.CircularProgressBar
import java.util.Calendar
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.data.User


class Home : Fragment() {
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

    private fun onClickToday() {
        if (!day) {
            day = true
            setTasks(true)
        }
    }

    private fun onClickWeek() {
        if (day) {
            day = false
            setTasks(false)
        }
    }


    private fun setTasks(today: Boolean = true) {
        listCategoryModel.clear()

        val categories : MutableList<Pair<DocumentReference?, MutableList<Int>>> = mutableListOf()

        val tasks : List<Task> = if (today) {
            ListTask.list.filter { task -> isToday(task.deadline) }
        } else {
            ListTask.list.filter { task -> isOnWeek(task.deadline) }
        }

        if (tasks.isNotEmpty()) {
            var totalPercentage = 0F
            var totalDivider = 0

            for (task in tasks) {
                var categoryExists = false
                var index = 0
                for ((i, category) in categories.withIndex()) {
                    if (category.first == task.categorie) {
                        categoryExists = true
                        index = i
                    }
                }
                if (!categoryExists) {
                    index = categories.size
                    categories.add(Pair(task.categorie, mutableListOf()))
                }
                categories[index].second.add(task.done)
                totalPercentage += task.done * task.duree
                totalDivider += task.duree
            }

            totalPercentage /= totalDivider
            progressBar.setPercentageExternal(totalPercentage.toInt().toFloat())

            for (category in categories) {
                val actualCategory: Category? = CategoryDictionary.dictionary[category.first]
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
        }
        else {
            val packageName = context?.packageName

            listCategoryModel.add(
                CategoryModel(
//                    getString(resources.getIdentifier("no_task", "string", packageName)),
                    getString(R.string.no_task),
                    "empty",
                    0
                )
            )

            progressBar.setPercentageExternal(100F)
        }
        rv.adapter = CategoryRowAdapter(listCategoryModel)
    }

    fun isToday(date : com.google.firebase.Timestamp?) : Boolean {
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

    fun isOnWeek(date : com.google.firebase.Timestamp?) : Boolean {
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

        val profilePicture = view.findViewById<ImageView>(R.id.profileimage)

        if (User.profile_picture != "") {
            Glide.with(this)
                .load(User.profile_picture)
                .into(profilePicture)
        }

        return view
        // Inflate the layout for this fragment
    }

}