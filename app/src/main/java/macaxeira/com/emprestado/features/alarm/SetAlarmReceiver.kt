package macaxeira.com.emprestado.features.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.utils.Utils
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SetAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val repository: DataRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val items = repository.getItemsByFilter(0).blockingGet()

            for (item in items) {
                if (item.remember && !item.isReturned) {
                    val text: String = if (item.isMine)
                        context.getString(R.string.notification_return_lent, item.description, item.person?.name)
                    else
                        context.getString(R.string.notification_return_borrowed, item.description,
                                item.person?.name)

                    val date = Utils.fromStringToTime(item.returnDate)

                    NotificationScheduler.setAlarm(context, item.id!!.toInt(), date, text)
                }
            }
        }
    }
}
