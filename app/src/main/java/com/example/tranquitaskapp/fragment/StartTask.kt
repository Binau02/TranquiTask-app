package com.example.tranquitaskapp.fragment

import ScreenStateReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.tranquitaskapp.data.Task
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.firebase.Timestamp

class StartTask(private val task: Task) : Fragment(), ScreenStateReceiver.ScreenStateListener {

    private lateinit var screenStateReceiver: ScreenStateReceiver

    private var bottomBarListener: BottomBarVisibilityListener? = null
    private lateinit var textViewTimer: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonValider: Button
    private lateinit var countdownTimer: CountDownTimer
    private val db = MyFirebase.getFirestoreInstance()
    private val initialMillis: Long = (task.duree * 60000).toLong() // 30 secondes
    private var timeLeftMillis: Long = initialMillis
    private var timerRunning = false

    private var isStopOnce = false
    private var isSreenOFF_once = false

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

        // Initialisation du BroadcastReceiver
        screenStateReceiver = ScreenStateReceiver(this)

        // Bouton
        buttonStart.setOnClickListener {
            startTimer()
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
            timerRunning = false
            onClickValidate()
        }

        updateTimer()

        //Toast.makeText(this.context, "OC | screen off : ${isSreenOFF_once}", Toast.LENGTH_SHORT).show()


        return view
    }


    private fun addCoinToUser(){
        val transactionCollection = db.collection("transaction")
        val userReference = db.collection("user").document(User.id)

        val transactionData = hashMapOf(
            "amount" to task.duree,
            "categorie" to task.categorie,
            "date" to Timestamp.now(),
            "user" to userReference,
        )
        transactionCollection.add(transactionData)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error adding in transactionCollection : $e")
            }
    }

    private fun onClickValidate(){
        val taskRef = task.ref
        taskRef.update("done", 100)
            .addOnSuccessListener {
                // La mise à jour a réussi
                Log.d("Update", "La tache a ete modifiée")
                Toast.makeText(this.context, "Vous avez gagné ${task.duree} coins", Toast.LENGTH_SHORT).show()
                addCoinToUser()
                val fragment = Home()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameLayout, fragment)?.commit()
            }
            .addOnFailureListener { e ->
                // Gérer les erreurs lors de la mise à jour
                Toast.makeText(this.context, "La tache n'a pas pu être validé !", Toast.LENGTH_SHORT).show()
                Log.e("Update", "La tache n'a pas été modifiée : $e")
            }
    }

    private fun startTimer() {
        val packageName = this.context?.packageName

        countdownTimer = object : CountDownTimer(timeLeftMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                timerRunning = false
                updateTimer()
                textViewTimer.text = getString(resources.getIdentifier("end", "string", packageName))
                // onClickValidate()
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

    private fun cancelTimer() {
        countdownTimer.cancel()
        timerRunning = false
        timeLeftMillis = initialMillis

        buttonStart.visibility = View.VISIBLE
        buttonValider.visibility = View.INVISIBLE
        buttonPause.visibility = View.INVISIBLE

        updateTimer()
    }

    private fun updateTimer() {
        val minutes = (timeLeftMillis / 1000) / 60
        val seconds = (timeLeftMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        textViewTimer.text = timeFormatted

        // buttonStart.text = if (timerRunning) "Pause" else "Start"
    }

    override fun onStart() {
        super.onStart()
        // Toast.makeText(this.context, "OS_1 | screen off : ${isSreenOFF_once}", Toast.LENGTH_SHORT).show()
        if (isStopOnce and timerRunning){
            Toast.makeText(this.context, "Vous avez quitté l'application donc vous ne gagnez pas de coins", Toast.LENGTH_LONG).show()
            cancelTimer()
        }

        if (isSreenOFF_once){
            Toast.makeText(this.context, "écran OFF", Toast.LENGTH_LONG).show()
            // cancelTimer()
        }

        // Enregistrement du BroadcastReceiver
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        activity?.registerReceiver(screenStateReceiver, filter)

        // Toast.makeText(this.context, "OS_2 | screen off : ${isSreenOFF_once}", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        if (timerRunning) {
            isStopOnce = true
            Toast.makeText(this.context, "Vous avez quitté la page donc vous ne gagnez pas de coins", Toast.LENGTH_LONG)
                .show()
        }
        // Désenregistrement du BroadcastReceiver
        activity?.unregisterReceiver(screenStateReceiver)
    }

    // Méthodes de l'interface ScreenStateListener
    override fun onScreenOff() {
        // L'écran est verrouillé, l'utilisateur peut avoir quitté l'application
        isSreenOFF_once = true
        Toast.makeText(this.context, "off", Toast.LENGTH_SHORT).show()
    }

    override fun onScreenOn() {
        // L'écran est déverrouillé, l'utilisateur peut être revenu à l'application
        isSreenOFF_once = false
        Toast.makeText(this.context, "on", Toast.LENGTH_SHORT).show()
    }

}