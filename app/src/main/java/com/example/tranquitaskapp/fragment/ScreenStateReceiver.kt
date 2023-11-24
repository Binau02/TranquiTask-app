import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ScreenStateReceiver(private val listener: ScreenStateListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            // L'écran est verrouillé
            listener.onScreenOff()
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            // L'écran est déverrouillé
            listener.onScreenOn()
        }
    }

    interface ScreenStateListener {
        fun onScreenOff()
        fun onScreenOn()
    }
}
