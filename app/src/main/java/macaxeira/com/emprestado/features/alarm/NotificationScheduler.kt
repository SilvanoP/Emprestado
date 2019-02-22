package macaxeira.com.emprestado.features.alarm

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.utils.Constants

object NotificationScheduler {

    @JvmStatic
    fun setAlarm(context: Context, id: Int, time: Long, text: String) {
        val notification = createNotification(context, text)

        val notificationIntent = Intent(context, AlarmTriggeredReceiver::class.java)
        notificationIntent.putExtra(Constants.NOTIFICATION_ID, id)
        notificationIntent.putExtra(Constants.NOTIFICATION, notification)
        val pi = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, time, pi)
    }

    private fun createNotification(context: Context, text: String) : Notification {
        val builder: Notification.Builder?
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder = Notification.Builder(context)
        } else {
            builder = Notification.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
        }

        builder.setSmallIcon(R.drawable.ic_checked)
                .setContentTitle(context.getString(R.string.return_date))
                .setContentText(text)

        return builder.build()
    }
}