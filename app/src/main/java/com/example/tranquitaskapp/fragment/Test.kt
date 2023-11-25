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


class Test : Fragment() {
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




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.test, container, false)


        return view
        // Inflate the layout for this fragment
    }

}