import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tranquitaskapp.R

class TimerActivity : AppCompatActivity() {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var textViewTimer: TextView
    private lateinit var buttonStart: Button

    private val initialTimeInMillis: Long = 60000 // 60 secondes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)

        textViewTimer = findViewById(R.id.countdown)
        buttonStart = findViewById(R.id.button_start2)
        countDownTimer = createTimer(initialTimeInMillis)

        buttonStart.setOnClickListener {
            startTimer()
        }
    }

    private fun createTimer(timeInMillis: Long): CountDownTimer {
        return object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimer(millisUntilFinished)
            }

            override fun onFinish() {
                updateTimer(0)
                // Ajoutez ici le code à exécuter lorsque le minuteur se termine
                showToast("Fin du minuteur")
            }
        }
    }

    private fun startTimer() {
        countDownTimer.start()
        buttonStart.isEnabled = false
    }

    private fun updateTimer(timeInMillis: Long) {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        textViewTimer.text = timeFormatted
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
