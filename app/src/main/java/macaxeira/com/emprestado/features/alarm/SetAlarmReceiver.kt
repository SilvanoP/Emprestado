package macaxeira.com.emprestado.features.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.utils.Constants
import macaxeira.com.emprestado.utils.Utils
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SetAlarmReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private const val NO_FILTER = 0
    }

    private val repository: DataRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val so: SingleObserver<List<Item>> = object :SingleObserver<List<Item>> {
                override fun onSuccess(t: List<Item>) {
                    setNotifications(t, context)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onSubscribe(d: Disposable) {}
            }

            repository.getItemsByFilter(NO_FILTER)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(so)
        } else if (intent.action == Constants.ACTION_RETURNED) {
            val id = intent.getIntExtra(Constants.NOTIFICATION_ITEM_RETURNED, -1)
            repository.getItemById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( Consumer {
                        updateItemReturned(it)
                    })
        }
    }

    private fun setNotifications(items: List<Item>, context: Context) {
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

    private fun updateItemReturned(item: Item) {
        item.isReturned = true
        repository.updateItems(listOf(item))
    }
}
