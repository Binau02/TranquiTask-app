package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
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
import io.grpc.okhttp.internal.framed.FrameReader


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

        val tvBreak = view.findViewById<TextView>(R.id.tvBreak)
        val tvStart = view.findViewById<TextView>(R.id.tvStart)
        val tvValidate = view.findViewById<TextView>(R.id.tvValidate)

        val seekBar = view.findViewById<SeekBar>(R.id.slider)
        val seekBarTask = view.findViewById<SeekBar>(R.id.slidertask)



        // Initialisez la visibilité des SeekBars
        seekBar.visibility = View.VISIBLE
        tvStart.visibility = View.VISIBLE
        tvBreak.visibility = View.GONE
        tvValidate.visibility = View.GONE
        seekBarTask.visibility = View.GONE

        seekBar.max = 75
        seekBar.progress = 5
        seekBarTask.progress = 50

        // Ajoutez une variable pour suivre le dernier temps que l'utilisateur a touché le slider
        var lastTouchTime: Long = 5

        val handler = Handler()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Actions à effectuer lorsque la progression change
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Actions à effectuer lorsque l'utilisateur commence à glisser
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    if (it.progress == it.max) {

                    seekBar.visibility = View.GONE
                    tvStart.visibility = View.GONE
                    tvBreak.visibility = View.VISIBLE
                    tvValidate.visibility = View.VISIBLE
                    seekBarTask.visibility = View.VISIBLE
                    seekBarTask.progress = 50
                }
            }}
        })

        seekBarTask.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBarTask: SeekBar?, progress: Int, fromUser: Boolean) {
                // Actions à effectuer lorsque la progression change
            }

            override fun onStartTrackingTouch(seekBarTask: SeekBar?) {
                // Actions à effectuer lorsque l'utilisateur commence à glisser
            }

            override fun onStopTrackingTouch(seekBarTask: SeekBar?) {

                seekBarTask?.let {
                    if (it.progress == it.max) {
                        seekBar.visibility = View.VISIBLE
                        tvStart.visibility = View.VISIBLE
                        tvBreak.visibility = View.GONE
                        tvValidate.visibility = View.GONE
                        seekBarTask.visibility = View.GONE
                        seekBar.progress = 0
                    } else if (it.progress == it.min) {
                        seekBarTask.progress = 0
                    } else if (it.progress != it.min) {
                        seekBarTask.progress = 50
                    }

                }}
            })
        seekBar.setOnClickListener {
            seekBar.progress = 5
        }

        seekBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                lastTouchTime = System.currentTimeMillis()
            }
            false
        }
        seekBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // L'utilisateur a touché le slider, enregistrez le temps actuel
                lastTouchTime = System.currentTimeMillis()
            }
            false
        }
        val checkInterval = 100L
        handler.postDelayed(object : Runnable {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastTouchTime

                if (elapsedTime > checkInterval && seekBar.progress != seekBar.max) {
                    seekBar.progress = 5
                }

                handler.postDelayed(this, checkInterval)
            }
        }, checkInterval)

        return view
    }
}