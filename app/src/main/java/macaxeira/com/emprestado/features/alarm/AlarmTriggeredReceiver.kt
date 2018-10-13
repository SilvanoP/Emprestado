package macaxeira.com.emprestado.features.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.utils.Constants

class AlarmTriggeredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.channel_name)
            val channelDescription = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, channelName,
                    importance).apply {
                description = channelDescription
            }

            manager.createNotificationChannel(channel)
        }

        val notification = intent.getParcelableExtra<Notification>(Constants.NOTIFICATION)

        val id = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
        manager.notify(id, notification)
    }
}
