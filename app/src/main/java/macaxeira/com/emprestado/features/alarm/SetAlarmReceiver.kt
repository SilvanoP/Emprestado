package macaxeira.com.emprestado.features.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.User
import macaxeira.com.emprestado.features.listitem.ListItemCallback
import macaxeira.com.emprestado.features.shared.ItemCallback
import macaxeira.com.emprestado.features.shared.UserCallback
import macaxeira.com.emprestado.utils.Constants
import macaxeira.com.emprestado.utils.Utils
import org.koin.core.KoinComponent
import org.koin.core.inject

class SetAlarmReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private const val NO_FILTER = -1
    }

    private val repository: DataRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            repository.getUserFromPreference(object: UserCallback {
                override fun receiveUser(user: User) {
                    repository.getItemsByFilter(NO_FILTER, object : ListItemCallback{
                        override fun returnItems(items: List<Item>) {
                            setNotifications(items, context)
                        }

                        override fun error(error: Throwable) {
                            error.printStackTrace()
                        }
                    })
                }
            })
        } else if (intent.action == Constants.ACTION_RETURNED) {
            val id = intent.getIntExtra(Constants.NOTIFICATION_ITEM_RETURNED, -1)
            val itemId = intent.getStringExtra(Constants.ITEM_ID)
            repository.getItemById(itemId, object: ItemCallback {
                override fun receiveItem(item: Item) {
                    updateItemReturned(context, item, id)
                }
            })
        }
    }

    private fun setNotifications(items: List<Item>, context: Context) {
        for (item in items) {
            if (item.remember && !item.isReturned) {
                val text: String = if (item.isMine)
                    context.getString(R.string.notification_return_lent, item.description, item.personName)
                else
                    context.getString(R.string.notification_return_borrowed, item.description, item.personName)

                val notificationId = Utils.fromStringTimestampToInt(item.createdDate)
                val date = Utils.fromStringToTime(item.returnDate)

                NotificationScheduler.setAlarm(context, notificationId, item.id, date, text)
            }
        }
    }

    private fun updateItemReturned(context: Context, item: Item, notificationId: Int) {
        item.isReturned = true
        repository.updateItemReturned(item)

        NotificationScheduler.dismissNotification(context, notificationId)
    }
}
