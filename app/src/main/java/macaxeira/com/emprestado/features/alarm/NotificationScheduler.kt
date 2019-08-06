package macaxeira.com.emprestado.features.alarm

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.features.itemdetail.ItemDetailActivity
import macaxeira.com.emprestado.utils.Constants

object NotificationScheduler {

    @JvmStatic
    fun setAlarm(context: Context, id: Int, time: Long, text: String) {
        val notification = createNotification(context, text)

        val notificationIntent = Intent(context, AlarmTriggeredReceiver::class.java)
        notificationIntent.putExtra(Constants.NOTIFICATION_ID, id)
        notificationIntent.putExtra(Constants.NOTIFICATION, notification)
        val pi = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, time, pi)
    }

    @JvmStatic
    fun cancelAlarm(context: Context, id: Int) {
        val notificationIntent = Intent(context, AlarmTriggeredReceiver::class.java)
        notificationIntent.putExtra(Constants.NOTIFICATION_ID, id)
        val pi = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel( pi)
    }

    private fun createNotification(context: Context, text: String) : Notification {
        val itemIntent = Intent(context, ItemDetailActivity::class.java)
        val pendingItemIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(itemIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID).apply {
            setContentIntent(pendingItemIntent)
        }

        builder.setSmallIcon(R.drawable.ic_checked)
                .setContentTitle(context.getString(R.string.return_date))
                .setContentText(text)

        return builder.build()
    }
}