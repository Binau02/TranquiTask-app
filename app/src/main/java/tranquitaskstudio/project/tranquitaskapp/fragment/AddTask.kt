package tranquitaskstudio.project.tranquitaskapp.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import tranquitaskstudio.project.tranquitaskapp.data.CategoryDictionary
import tranquitaskstudio.project.tranquitaskapp.data.Priorities
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.data.ListTask
import tranquitaskstudio.project.tranquitaskapp.data.Task
import tranquitaskstudio.project.tranquitaskapp.data.User
import tranquitaskstudio.project.tranquitaskapp.firebase.MyFirebase
import tranquitaskstudio.project.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddTask : Fragment() {

    private lateinit var tvDate: TextView
    private lateinit var imgShowDatePicker: ImageView
    private lateinit var imgTimeView: ImageView

    private val calendar = Calendar.getInstance()
    private var bottomBarListener: BottomBarVisibilityListener? = null
    private var formattedDate: Timestamp? = null
    private var timestampInSeconds: Int = 0
    private val listCategory = HashMap<String, DocumentReference>()
    private val listPriority = HashMap<String, Int>()

    private val db = MyFirebase.getFirestoreInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun setCheckBoxes(view: View) {
        val checkBoxConcentration = view.findViewById<CheckBox>(R.id.checkBoxConcentration)
        val checkBoxDivisible = view.findViewById<CheckBox>(R.id.checkBoxDivisible)

        // Définir la couleur de teinte des cases à cocher programmées
        val checkBoxColor = ContextCompat.getColor(requireContext(), R.color.my_dark)
        checkBoxConcentration.buttonTintList = ColorStateList.valueOf(checkBoxColor)
        checkBoxDivisible.buttonTintList = ColorStateList.valueOf(checkBoxColor)
    }

    private fun createNewTask(
        categorie: DocumentReference,
        priority: Int,
        nameTask: String,
        deadline: Timestamp,
        divisible: Boolean,
        concentration: Boolean,
        duree: Int
    ) {
        val taskCollection = db.collection("tache")

        val taskData = hashMapOf(
            "categorie" to categorie,
            "concentration" to concentration,
            "deadline" to deadline,
            "divisible" to divisible,
            "done" to 0,
            "duree" to duree,
            "name" to nameTask,
            "priorite" to priority
            )
        taskCollection.add(taskData)
            .addOnSuccessListener {
                addTaskToUser(it)
                ListTask.list.add(Task(
                    name = nameTask,
                    concentration = concentration,
                    divisible = divisible,
                    done = 0,
                    duree = duree,
                    deadline = deadline,
                    categorie = categorie,
                    priorite = priority,
                    ref = it
                ))

                Toast.makeText(this.context, "Tâche ajoutée", Toast.LENGTH_SHORT).show()

                val fragment = AddTask()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameLayout, fragment)?.commit()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this.context, "Il y a eu une erreur $e", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_addtask, container, false)
        val saveBtn = view.findViewById<ImageView>(R.id.btnSave)
        imgTimeView = view.findViewById(R.id.imgTimeView)
        val tvSelectedTime = view.findViewById<TextView>(R.id.tvSelectedTime)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerPriority = view.findViewById<Spinner>(R.id.spinnerPriority)

        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        setCheckBoxes(view)
        bottomBarListener?.setBottomBarVisibility(this)
        getCategories(spinnerCategory)
        getPriority(spinnerPriority)

        tvDate = view.findViewById(R.id.tvSelectedDate)
        imgShowDatePicker = view.findViewById(R.id.ImgCalendarView)

        imgShowDatePicker.setOnClickListener {
            showDatePicker()
        }
        imgTimeView.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                // Convertir le timestamp en minutes
                val timestampInMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

                tvSelectedTime.text = SimpleDateFormat("HH:mm").format(cal.time)
                timestampInSeconds = timestampInMinutes
            }

            // Créer le TimePickerDialog en mode "spinner"
            val timePickerDialog =
                TimePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                )
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }

        saveBtn.setOnClickListener {
            val checkBoxConcentration = view.findViewById<CheckBox>(R.id.checkBoxConcentration)
            val isConcentrationChecked = checkBoxConcentration.isChecked
            val checkBoxDivisible = view.findViewById<CheckBox>(R.id.checkBoxDivisible)
            val isDivisibleChecked = checkBoxDivisible.isChecked
            val nameTask = view.findViewById<TextView>(R.id.editNameTask)
            val categorie =
                listCategory[spinnerCategory.selectedItem]
            val priority =
                listPriority[spinnerPriority.selectedItem]
            val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning)

            if (nameTask.text.isNullOrBlank() || formattedDate == null || timestampInSeconds == 0) {
                // Afficher une erreur ou une notification indiquant que tous les champs doivent être remplis
                if (nameTask.text.isEmpty()) {
                    nameTask.setError("Please Enter a name", icon)
                }
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (categorie != null && priority != null) {
                    createNewTask(
                        categorie,
                        priority,
                        nameTask.text.toString(),
                        formattedDate!!,
                        isDivisibleChecked,
                        isConcentrationChecked,
                        timestampInSeconds
                    )
                    Toast.makeText(context, "Tâche bien ajouté à la liste !", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerDialogStyle,
            { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar.set(year, monthOfYear, dayOfMonth)  // Mettez à jour l'instance de Calendar ici
                formattedDate = Timestamp(calendar.time)
                val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                tvDate.text = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun getCategories(spinnerCategory: Spinner) {
        val adapter = this.context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item
            )
        }

        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        for ((ref, category) in CategoryDictionary.dictionary) {
            adapter?.add(category.name)
            listCategory[category.name] = ref
        }

        spinnerCategory.adapter = adapter
    }

    private fun getPriority(spinnerPriority: Spinner) {
        val adapter = this.context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item
            )
        }

        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        for ((value, priorityName) in Priorities.dictionary) {
            adapter?.add(priorityName)
            listPriority[priorityName] = value
        }

        spinnerPriority.adapter = adapter
    }

    private fun addTaskToUser(idTask: DocumentReference){
        val userRef = User.ref ?: db.collection("user").document(User.id)
        val taskRef = db.collection("tache").document(idTask.id)

        userRef.update("taches", FieldValue.arrayUnion(taskRef))
            .addOnSuccessListener {
                // La mise à jour a réussi
                Log.d("Update", "ID de la tâche ajouté au tableau tasks de l'utilisateur")
            }
            .addOnFailureListener { e ->
                // Gérer les erreurs lors de la mise à jour
                Log.e("Update", "Erreur lors de l'ajout de l'ID de la tâche : $e")
            }
    }
}


