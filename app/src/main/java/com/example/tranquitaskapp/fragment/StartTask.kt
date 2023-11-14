package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.TacheModel
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener

class StartTask(private val task: TacheModel) : Fragment() {

    private var bottomBarListener: BottomBarVisibilityListener? = null
    private lateinit var textViewTimer: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonValider: Button
    private lateinit var countdownTimer: CountDownTimer
    private val db = MyFirebase.getFirestoreInstance()
    private val initialMillis: Long = (task.duration * 600).toLong() // 30 secondes
    private var timeLeftMillis: Long = initialMillis
    private var timerRunning = false

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start_task, container, false)
        buttonStart = view.findViewById(R.id.button_start)
        buttonValider = view.findViewById(R.id.button_valider)
        buttonPause = view.findViewById(R.id.button_pause)
        textViewTimer = view.findViewById(R.id.countdown)
        buttonValider.visibility = View.INVISIBLE
        buttonPause.visibility = View.INVISIBLE


        buttonStart.setOnClickListener {
            onClickStart()
            buttonStart.visibility = View.INVISIBLE
            buttonValider.visibility = View.VISIBLE
            buttonPause.visibility = View.VISIBLE
        }
        buttonPause.setOnClickListener {
            pauseTimer()
            buttonStart.visibility = View.VISIBLE
            buttonValider.visibility = View.INVISIBLE
            buttonPause.visibility = View.INVISIBLE
        }
        buttonValider.setOnClickListener {
            onClickValidate()
        }

        val minutes = (timeLeftMillis / 1000) / 60
        val seconds = (timeLeftMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        textViewTimer.text = timeFormatted


        return view
    }


    private fun onClickStart(){
        if (timerRunning) {
            pauseTimer()
            Toast.makeText(this.context, "Le bouton Pause a été cliqué !", Toast.LENGTH_SHORT).show()
        } else {
            startTimer()
            Toast.makeText(this.context, "Le bouton Start a été cliqué !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickValidate(){
        val taskRef = db.collection("tache").document(task.id)

        taskRef.update("done", 100)
            .addOnSuccessListener {
                // La mise à jour a réussi
                Log.d("Update", "La tache a ete modifiée")
            }
            .addOnFailureListener { e ->
                // Gérer les erreurs lors de la mise à jour
                Log.e("Update", "La tache n'a pas été modifiée")
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
                onClickValidate()
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

}