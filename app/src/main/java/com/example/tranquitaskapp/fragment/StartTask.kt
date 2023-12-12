package com.example.tranquitaskapp.fragment

import ScreenStateReceiver
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.TacheModel
import com.example.tranquitaskapp.data.Task
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.interfaces.MainActivityListener
import com.example.tranquitaskapp.ui.CustomPopup
import com.google.firebase.Timestamp

class StartTask(private val task: TacheModel) : Fragment(), ScreenStateReceiver.ScreenStateListener {

    private lateinit var screenStateReceiver: ScreenStateReceiver

    private var bottomBarListener: BottomBarVisibilityListener? = null
    private lateinit var textViewTimer: TextView
    private lateinit var buttonBack: Button
    private lateinit var buttonSaveQuit: Button
    private lateinit var countdownTimer: CountDownTimer
    private val db = MyFirebase.getFirestoreInstance()
    val part1 = (task.duration.toLong() * 60000)
    val part2 = (100 - task.done) / 100.0
    private val initialMillis: Long = (part1 * part2).toLong()// 30 secondes
    private var timeLeftMillis: Long = initialMillis
    private var timerRunning = false

    private lateinit var seekBar : SeekBar
    private lateinit var seekBarTask : SeekBar
    private lateinit var tvBreak : TextView
    private lateinit var tvStart : TextView
    private lateinit var tvValidate : TextView

    private var areSeekBarsVisible = true  // Variable pour suivre l'état des SeekBars

    private var isStopOnce = false
    private var isSreenOFF_once = false
    private var mainActivityListener: MainActivityListener? = null

    private val CHANNEL_ID = "myChannel01"


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivityListener) {
            mainActivityListener = context
        }

    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private fun onClickBack(){
        this.context?.let {
            CustomPopup.showPopup(
                context = it,
                getString(R.string.quit_task),
                object :
                    CustomPopup.PopupClickListener {
                    override fun onPopupButtonClick() {
                        replaceFragment(ListTaches())
                    }
                }
            )
        }
    }
    private fun onCLickSaveQuit(){
        // sauvegarder la progression de la tâche et retour page principale
        Toast.makeText(this.context, "sauvegarde pas encore", Toast.LENGTH_SHORT).show()
        val pourcentage: Double = 100.0 - (timeLeftMillis/initialMillis.toFloat())*100
        Log.d("POURCENTAGE","${pourcentage}")
        val taskRef = task.ref
        task.done = pourcentage.toInt()
        taskRef.update("done", pourcentage.toInt())
            .addOnSuccessListener {
                // La mise à jour a réussi
                Log.d("Update", "La tache a ete modifiée")
                replaceFragment(Home())
            }
            .addOnFailureListener { e ->
                // Gérer les erreurs lors de la mise à jour
                Log.e("Update", "La tache n'a pas été modifiée : $e")
            }
        replaceFragment(Home())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("StartTask", "onViewCreated")

        Log.d("POURCENTAGE","${timeLeftMillis}")
        Log.d("POURCENTAGE","${initialMillis}")
        Log.d("POURCENTAGE","${task.duration}")
        Log.d("POURCENTAGE","${task.done}")
        Log.d("POURCENTAGE","part 1 : ${part1}")
        Log.d("POURCENTAGE","part 1 : ${part2}")

        val view = inflater.inflate(R.layout.fragment_start_task, container, false)
        val buttonBack = view.findViewById<ImageView>(R.id.back2)
        val buttonSaveQuit = view.findViewById<Button>(R.id.button_save_quit)
        textViewTimer = view.findViewById(R.id.countdown)

        val seekBar = view.findViewById<SeekBar>(R.id.slider)
        val seekBarTask = view.findViewById<SeekBar>(R.id.slidertask)
        val tvBreak = view.findViewById<TextView>(R.id.tvBreak)
        val tvStart = view.findViewById<TextView>(R.id.tvStart)
        val tvValidate = view.findViewById<TextView>(R.id.tvValidate)
        screenStateReceiver = ScreenStateReceiver(this)


            if (areSeekBarsVisible) {
                seekBar.visibility = View.VISIBLE
                tvStart.visibility = View.VISIBLE
                tvBreak.visibility = View.GONE
                tvValidate.visibility = View.GONE
                seekBarTask.visibility = View.GONE
            }

        buttonSaveQuit.visibility = View.GONE

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
                        startTimer()
                        buttonSaveQuit.visibility = View.GONE

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
                    if (it.progress == 100) {
                        timerRunning = false
                        onClickValidate()
                    } else if (it.progress == 0) {
                        pauseTimer()
                        if(task.isDivisible){
                            buttonSaveQuit.visibility = View.VISIBLE
                        }else{
                            buttonSaveQuit.visibility = View.INVISIBLE
                        }
                        seekBar.visibility = View.VISIBLE
                        tvStart.visibility = View.VISIBLE
                        tvBreak.visibility = View.GONE
                        tvValidate.visibility = View.GONE
                        seekBarTask.visibility = View.GONE
                        seekBar.progress = 0
                    } else if (it.progress != it.min) {
                        buttonSaveQuit.visibility = View.GONE
                        seekBarTask.progress = 50
                    }

                }}
        })
        seekBar.setOnClickListener {
            seekBar.progress = 5
        }

        buttonBack.setOnClickListener {
            onClickBack()
        }
        buttonSaveQuit.setOnClickListener {
            onCLickSaveQuit()
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

        updateTimer()


        // Notification
        createNotificationChannel()


        // Enregistrement du BroadcastReceiver
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        activity?.registerReceiver(screenStateReceiver, filter)
        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)
        return view
    }


    private fun createNotificationChannel() {
        Log.d("StartTask", "createNotificationChannel")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyChannel"
            val descriptionText = "My Notification Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Enregistrer le canal avec le gestionnaire de notification
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context) {
        Log.d("StartTask", "showNotification")

        // Construire la notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Fin du minuteur")
            .setContentText("Ta tâche est terminée ! Viens récupérer tes coins !")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Afficher la notification
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("StartTask", "error with permission notification")
                return
            }
            notify(1, builder.build())
        }
    }


    private fun addCoinToUser(){
        val transactionCollection = db.collection("transaction")
        val userReference = db.collection("user").document(User.id)

        val transactionData = hashMapOf(
            "amount" to task.duration - timeLeftMillis/60000,
            "categorie" to task.category,
            "date" to Timestamp.now(),
            "user" to userReference,
        )
        val updateUser = hashMapOf(
            "coins" to User.coins + (task.duration - timeLeftMillis/60000)
        )
        transactionCollection.add(transactionData)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error adding in transactionCollection : $e")
            }

        userReference.update(updateUser as Map<String, Any>)
            .addOnSuccessListener {
                User.coins += (task.duration - timeLeftMillis/60000)
                mainActivityListener?.refreshCoins()
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error adding in transactionCollection : $e")
            }
    }

    private fun onClickValidate(){
        val taskRef = task.ref
        task.done = 100
        taskRef.update("done", 100)
            .addOnSuccessListener {
                // La mise à jour a réussi
                Log.d("Update", "La tache a ete modifiée")
                Toast.makeText(this.context, "Vous avez gagné ${task.duration - timeLeftMillis/60000} coins", Toast.LENGTH_SHORT).show()
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
        countdownTimer = object : CountDownTimer(timeLeftMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                timerRunning = false
                updateTimer()
                textViewTimer.text = getString(R.string.end)
                showNotification(requireContext())
            }
        }.start()

        timerRunning = true
        updateTimer()
    }

    private fun pauseTimer() {
        countdownTimer.cancel()
        timerRunning = false
        updateTimer()
        Log.d("StartTask", "Button clicked")
    }

    private fun cancelTimer() {
        countdownTimer.cancel()
        timerRunning = false
        timeLeftMillis = initialMillis
        updateTimer()
    }


    private fun updateTimer() {
        val hours = ((timeLeftMillis / 1000) / 3600).toInt()
        val minutes = (((timeLeftMillis / 1000) % 3600) / 60).toInt()
        val seconds = ((timeLeftMillis / 1000) % 60).toInt()

        val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        textViewTimer.text = timeFormatted

        // buttonStart.text = if (timerRunning) "Pause" else "Start"
    }


    // Gérer le blockage d'écran
    override fun onResume() {
        super.onResume()

        val seekBar = view?.findViewById<SeekBar>(R.id.slider)
        val seekBarTask = view?.findViewById<SeekBar>(R.id.slidertask)
        val tvBreak = view?.findViewById<TextView>(R.id.tvBreak)
        val tvStart = view?.findViewById<TextView>(R.id.tvStart)
        val tvValidate = view?.findViewById<TextView>(R.id.tvValidate)


        if (isStopOnce and timerRunning and !isSreenOFF_once){
            Toast.makeText(this.context, "Vous avez quitté l'application donc vous ne gagnez pas de coins", Toast.LENGTH_LONG).show()
            cancelTimer()
            seekBar?.visibility = View.VISIBLE
            seekBarTask?.visibility = View.GONE
            tvStart?.visibility = View.VISIBLE
            tvBreak?.visibility = View.GONE
            tvValidate?.visibility = View.GONE
            seekBar?.progress = 0
        }
        if (isSreenOFF_once){
            isSreenOFF_once = false
        }
    }

    override fun onStop() {
        super.onStop()
        if (timerRunning) {
            isStopOnce = true
            Toast.makeText(this.context, "Vous avez quitté la page donc vous ne gagnez pas de coins", Toast.LENGTH_LONG)
                .show()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Désenregistrement du BroadcastReceiver
        activity?.unregisterReceiver(screenStateReceiver)
    }

    // Méthodes de l'interface ScreenStateListener
    override fun onScreenOff() {
        // L'écran est verrouillé, l'utilisateur peut avoir quitté l'application
        isSreenOFF_once = true
    }

    override fun onScreenOn() {
        // L'écran est déverrouillé, l'utilisateur peut être revenu à l'application
        // isSreenOFF_once = false
    }



}