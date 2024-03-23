@file:Suppress("DEPRECATION")

package tranquitaskstudio.project.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tranquitaskstudio.project.tranquitaskapp.data.CategoryDictionary
import tranquitaskstudio.project.tranquitaskapp.data.ListTask
import tranquitaskstudio.project.tranquitaskapp.data.Period
import tranquitaskstudio.project.tranquitaskapp.data.Priorities
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.adapter.ListeTachesRowAdapter
import tranquitaskstudio.project.tranquitaskapp.data.TacheModel
import tranquitaskstudio.project.tranquitaskapp.interfaces.BottomBarVisibilityListener
import java.text.SimpleDateFormat
import java.util.Locale
import tranquitaskstudio.project.tranquitaskapp.data.Task
import tranquitaskstudio.project.tranquitaskapp.data.ListTaskFilter



class ListTaches : Fragment() {

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var rv: RecyclerView

    private lateinit var listeTacheModel : MutableList<TacheModel>


    private fun onStartButtonClick(position: Int) {
        val fragment =
            StartTask(listeTacheModel[position]) // Remplacez par le fragment que vous souhaitez afficher
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private fun onEditImageClick(position: Int) {
        val fragment =
            ModifyTask(listeTacheModel[position]) // Remplacez par le fragment que vous souhaitez afficher
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }


    private fun onClickFiltre() {
//        Toast.makeText(this.context, "Le bouton Filtre a été cliqué !", Toast.LENGTH_SHORT).show()
        val fragment = ListTaskFilter()
        val slideUp = Slide(Gravity.TOP)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private fun onClickBack() {
        val fragment = Home()
        val slideUp = Slide(Gravity.TOP)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }


    private fun setTasks(search: String = "") {
        var tasks: List<Task>

        val home = Home()

        // filter by period
        tasks = when (ListTaskFilter.period) {
            Period.DAY -> {
                ListTask.list.filter { task -> home.isToday(task.deadline) }
            }

            Period.WEEK -> {
                ListTask.list.filter { task -> home.isOnWeek(task.deadline) }
            }

            else -> {
                ListTask.list
            }
        }

        // filter by category
        if (ListTaskFilter.category != null) {
            tasks = tasks.filter { task -> task.categorie == ListTaskFilter.category }
        }

        // filter by priority
        if (ListTaskFilter.priority != -1) {
            tasks = tasks.filter { task -> task.priorite == ListTaskFilter.priority }
        }

        // filter by searching
        if (search != "") {
            tasks = tasks.filter { task -> task.name.contains(search) }
        }

        listeTacheModel = mutableListOf<TacheModel>()

        val resources = context?.resources
        val packageName = context?.packageName

        for (task in tasks) {
            val taskCategory = CategoryDictionary.dictionary[task.categorie]
            val imageId =
                resources?.getIdentifier(taskCategory?.icon ?: "", "drawable", packageName)
            if (taskCategory != null && imageId != null && task.deadline != null) {
                TacheModel(
                    task.ref.id,
                    task.name,
                    imageId,
                    task.done,
                    false,
                    task.duree,
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(task.deadline!!.toDate()),
                    Priorities.dictionary[task.priorite] ?: "",
                    taskCategory.name,
                    task.ref,
                    task.done,
                    task.concentration,
                    task.divisible
                )
                    .let {
                        listeTacheModel.add(
                            it
                        )
                    }
            }
        }

        rv.adapter =
            ListeTachesRowAdapter(listeTacheModel, this::onStartButtonClick, this::onEditImageClick)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liste_taches, container, false)
        rv = view.findViewById(R.id.rv_liste_tache)
        val buttonFiltre = view.findViewById<ImageView>(R.id.filtre)
        val buttonBack = view.findViewById<ImageView>(R.id.back)
        val searchBar = view.findViewById<EditText>(R.id.search_bar)

        buttonFiltre.setOnClickListener {
            onClickFiltre()
        }
        buttonBack.setOnClickListener {
            onClickBack()
        }
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                setTasks(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

        rv.layoutManager = LinearLayoutManager(requireContext())

        setTasks()

        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)

        return view
    }
}