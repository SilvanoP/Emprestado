package macaxeira.com.emprestado.features.itemdetail

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.features.shared.BasePresenterImpl
import macaxeira.com.emprestado.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class ItemDetailPresenter(private val repository: DataRepository) : BasePresenterImpl<ItemDetailContract.View>(),
        ItemDetailContract.Presenter {

    override fun loadData() {
        val item = repository.getSelectedItem()
        view.get()?.fillFields(item)
    }

    override fun setReturnDate() {
        val item = repository.getSelectedItem()
        val date = item?.returnDate
        val cal = Calendar.getInstance()

        if (date != null && date != "") {
            cal.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date)
        }

        view.get()?.openDatePicker(cal)
    }

    override fun searchContacts() {
        view.get()?.verifyPermissions()
    }

    override fun searchContactPermissionVerified(hasPermission: Boolean) {
        if (hasPermission) {
            view.get()?.pickContact()
        } else {
            view.get()?.showErrorMessage(UnsupportedOperationException("Search Contacts not allowed."))
        }
    }

    override fun getPersonByUri(uri: String?) {
        if (uri != null)
            disposable.add(repository.getPersonByUri(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            view.get()?.fillPersonFields(it)
                        }, {
                    view.get()?.showErrorMessage(it)
                })
            )
    }

    override fun saveItem(description: String, isMine: Boolean, personName: String,
                          personEmail: String, personPhone: String, returnDate: String, isNotifiable: Boolean) {
        if (description.isEmpty() || personName.isEmpty()) {
            view.get()?.requiredFieldsEmpty()
        } else {
            disposable.add(repository.saveItem(description, isMine, personName, personEmail,
                    personPhone, returnDate)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    Consumer {
                                        if (!returnDate.isEmpty()) {
                                            val alarmTime = "$returnDate 10:00"
                                            val date = Utils.fromStringToTime(alarmTime)

                                            if (isNotifiable) {
                                                view.get()?.createAlarm(it.toInt(), date)
                                            } else {
                                                view.get()?.cancelAlarm(it.toInt())
                                            }
                                        }
                                        view.get()?.onSaveOrUpdateComplete()
                                    }
                            ))
        }
    }

}