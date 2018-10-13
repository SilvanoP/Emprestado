package macaxeira.com.emprestado.features.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

        }
    }
}
