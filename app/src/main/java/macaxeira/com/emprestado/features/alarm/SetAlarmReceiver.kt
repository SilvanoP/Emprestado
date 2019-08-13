package macaxeira.com.emprestado.features.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.utils.Constants
import macaxeira.com.emprestado.utils.Utils
import org.koin.core.KoinComponent
import org.koin.core.inject

class SetAlarmReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private const val NO_FILTER = 0
    }

    private val repository: DataRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            /*repository.getItemsByFilter(NO_FILTER)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(so)*/
        } else if (intent.action == Constants.ACTION_RETURNED) {
            val id = intent.getIntExtra(Constants.NOTIFICATION_ITEM_RETURNED, -1)
           /* repository.getItemById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( Consumer {
                        updateItemReturned(context, it)
                    })*/
        }
    }

    private fun setNotifications(items: List<Item>, context: Context) {
        for (item in items) {
            if (item.remember && !item.isReturned) {
                val text: String = if (item.isMine)
                    context.getString(R.string.notification_return_lent, item.description, item.personName)
                else
                    context.getString(R.string.notification_return_borrowed, item.description, item.personName)

                val date = Utils.fromStringToTime(item.returnDate)

                NotificationScheduler.setAlarm(context, item.id!!.toInt(), date, text)
            }
        }
    }

    private fun updateItemReturned(context: Context, item: Item) {
        item.isReturned = true
        /*repository.updateItem(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()*/

        NotificationScheduler.dismissNotification(context, item.id!!.toInt())
    }
}
