package com.example.tranquitaskapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage


class Home : Fragment() {
    private val listCategoryModel = mutableListOf<CategoryModel>()
    private lateinit var rv: RecyclerView
    private lateinit var progressBar: CircularProgressBar

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private var day: Boolean = true

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var profilePhoto: ShapeableImageView
    private lateinit var imagePhoto: ImageView

    var currentState: ButtonState = ButtonState.TODAY

    enum class ButtonState {
        TODAY, WEEK
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // La permission a été accordée, vous pouvez maintenant lancer l'appareil photo.
                launchCamera()
            } else {
                // La permission a été refusée.
                // Vous pouvez afficher un message à l'utilisateur ou prendre d'autres mesures.
            }
        }

    // Définissez les couleurs
    private var colorPrimary: Int = 0
    private var colorDark: Int = 0

    @SuppressLint("ResourceType")
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val typedArray = requireContext().theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.colorPrimary)
        )

        try {
            colorPrimary = typedArray.getColor(0, 0)
        } finally {
            typedArray.recycle()
        }
        val typedArrayDark = requireContext().theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.colorPrimaryDark)
        )

        try {
            colorDark = typedArrayDark.getColor(0, 0)
        } finally {
            typedArrayDark.recycle()
        }

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

        val categories: MutableList<Pair<DocumentReference?, MutableList<Int>>> = mutableListOf()

        val tasks: List<Task> = if (today) {
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
        } else {
            listCategoryModel.add(
                CategoryModel(
                    getString(R.string.no_task),
                    "empty",
                    0
                )
            )

            progressBar.setPercentageExternal(100F)
        }
        rv.adapter = CategoryRowAdapter(listCategoryModel, ::onClickCategory, day)
    }

    private fun onClickCategory() {
        val fragment = ListTaches() // Remplacez par le fragment que vous souhaitez afficher
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }


    fun isToday(date: com.google.firebase.Timestamp?): Boolean {
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

    fun isOnWeek(date: com.google.firebase.Timestamp?): Boolean {
        if (date == null) return false

        val currentDate = Calendar.getInstance()

        if (currentDate.get(Calendar.DAY_OF_WEEK) == 1) {
            currentDate.add(Calendar.DAY_OF_WEEK, -2)
        }
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

    private fun takePhoto() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // La permission est déjà accordée, vous pouvez lancer l'appareil photo.
                launchCamera()
            }

            else -> {
                // La permission n'est pas accordée, demandez-la à l'utilisateur.
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = if (data != null && data.data != null) {
                data.data
            } else {
                // Si l'URI n'est pas disponible, utilisez l'URI de l'image précédemment défini
                // Vous pouvez le récupérer à partir de l'intent original de l'appareil photo
                // ou d'une autre manière en fonction de votre implémentation
                // par exemple, imageUri = Uri.fromFile(photoFile)
                null
            }

            val imageBitmap = data?.extras?.get("data") as Bitmap?

            if (imageUri != null && imageBitmap != null) {


                // Obtenez une référence au FirebaseStorage
                val storage = Firebase.storage

                // Créez une référence au dossier où vous souhaitez stocker l'image
                val storageRef = storage.reference.child("profile_picture")

                // Obtenez l'URI de votre nouvelle image (par exemple, depuis la galerie)

                // Remplacez le nom de l'image existante que vous souhaitez écraser
                val existingImageName = User.id+".jpg"

                // Créez une référence à l'image existante dans le stockage Firebase
                val existingImageRef = storageRef.child(existingImageName)

                existingImageRef.metadata.addOnSuccessListener { metadata ->
                    if (metadata != null && metadata.sizeBytes > 0) {
                        // L'image existe, écrasez-la avec la nouvelle image
                        existingImageRef.putFile(imageUri)
                            .addOnSuccessListener { taskSnapshot ->
                                // L'upload est réussi, vous pouvez obtenir l'URL de la nouvelle image
                                existingImageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val newImageUrl = uri.toString()
                                    // Faites quelque chose avec la nouvelle URL de l'image si nécessaire
                                }
                            }
                            .addOnFailureListener { exception ->
                                // L'upload a échoué, gérer l'erreur
                            }
                    } else {
                        // L'image n'existe pas, créez-la avec la nouvelle image
                        storageRef.putFile(imageUri)
                            .addOnSuccessListener { taskSnapshot ->
                                // L'upload est réussi, vous pouvez obtenir l'URL de la nouvelle image
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val newImageUrl = uri.toString()
                                    // Faites quelque chose avec la nouvelle URL de l'image si nécessaire
                                }
                            }
                    }
                }

                // La photo a été capturée avec succès. Vous pouvez récupérer l'image ici.
                // Faites quelque chose avec l'image capturée, par exemple, l'afficher dans votre ImageView.
                profilePhoto.setImageBitmap(imageBitmap)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        rv = view.findViewById(R.id.rv)
        progressBar = view.findViewById(R.id.progressBar)
        val buttonToday = view.findViewById<TextView>(R.id.tvToday)
        val buttonWeek = view.findViewById<TextView>(R.id.tvWeek)
        val searchBtn: com.google.android.material.floatingactionbutton.FloatingActionButton =
            view.findViewById(R.id.fab2)
        imagePhoto = view.findViewById(R.id.cameraImage)
        profilePhoto = view.findViewById(R.id.profileimage)

        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)

        setTasks()

        imagePhoto.setOnClickListener {
            takePhoto()
        }

        profilePhoto.setOnClickListener {
            takePhoto()
        }

        searchBtn.setOnClickListener {
            val fragment = ListTaches()
            val slideUp = Slide(Gravity.BOTTOM)
            slideUp.duration = 150 // Durée de l'animation en millisecondes
            fragment.enterTransition = slideUp
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }


        buttonToday.setOnClickListener {
            if (currentState != ButtonState.TODAY) {
                // Mettez à jour l'état et les couleurs
                currentState = ButtonState.TODAY
                buttonToday.setBackgroundColor(colorPrimary)
                buttonWeek.setBackgroundColor(colorDark)
                buttonToday.setTextColor(colorDark)
                buttonWeek.setTextColor(colorPrimary)
                // Appel de la fonction associée au clic sur "Today"
                onClickToday()
            }
        }
        buttonWeek.setOnClickListener {
            if (currentState != ButtonState.WEEK) {
                // Mettez à jour l'état et les couleurs
                currentState = ButtonState.WEEK
                buttonWeek.setBackgroundColor(colorPrimary)
                buttonToday.setBackgroundColor(colorDark)
                buttonWeek.setTextColor(colorDark)
                buttonToday.setTextColor(colorPrimary)
                // Appel de la fonction associée au clic sur "Week"
                onClickWeek()
            }
        }
        rv.layoutManager = LinearLayoutManager(requireContext())


        if (User.profile_picture != "") {
            Glide.with(this)
                .load(User.profile_picture)
                .into(profilePhoto)
        }

        return view
        // Inflate the layout for this fragment
    }

}