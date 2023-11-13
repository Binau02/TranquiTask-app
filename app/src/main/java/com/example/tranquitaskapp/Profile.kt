package com.example.tranquitaskapp

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    /*
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
     */

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var countdownTimer: CountDownTimer
    private lateinit var textViewTimer: TextView
    private lateinit var buttonStart: Button
    private val initialMillis: Long = 30000 // 30 secondes
    private var timeLeftMillis: Long = initialMillis
    private var timerRunning = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
     */
    fun onClickModifProfile(){
        Toast.makeText(this.context, "Le bouton Modifier profil a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickScenery(){
        Toast.makeText(this.context, "Le bouton Modifier Décor a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickShop(){
        Toast.makeText(this.context, "Le bouton Boutique a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickStart(){
        if (timerRunning) {
            pauseTimer()
            Toast.makeText(this.context, "Le bouton Pause a été cliqué !", Toast.LENGTH_SHORT).show()
        } else {
            startTimer()
            Toast.makeText(this.context, "Le bouton Start a été cliqué !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(timeLeftMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                timerRunning = false
                updateTimer()
                Toast.makeText(context, "Fin du minuteur", Toast.LENGTH_SHORT).show()
            }
        }.start()

        timerRunning = true
        updateTimer()
    }

    private fun pauseTimer() {
        countdownTimer.cancel()
        timerRunning = false
        updateTimer()
    }

    private fun updateTimer() {
        val minutes = (timeLeftMillis / 1000) / 60
        val seconds = (timeLeftMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        textViewTimer.text = timeFormatted

        buttonStart.text = if (timerRunning) "Pause" else "Start"
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val buttonModifProfile = view.findViewById<Button>(R.id.button_modif)
        val buttonScenery = view.findViewById<Button>(R.id.button_scenery)
        val buttonShop = view.findViewById<Button>(R.id.button_shop)
        val pseudo = view.findViewById<TextView>(R.id.tv_pseudo)
        buttonStart = view.findViewById(R.id.button_start)
        textViewTimer = view.findViewById(R.id.countdown)

        pseudo.text = User.username
        buttonModifProfile.setOnClickListener {
            onClickModifProfile()
        }
        buttonScenery.setOnClickListener {
            onClickScenery()
        }
        buttonShop.setOnClickListener {
            onClickShop()
        }
        buttonStart.setOnClickListener {
            onClickStart()
        }
        // updateTimer()

        return view
        // Inflate the layout for this fragment
    }

/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

 */
}