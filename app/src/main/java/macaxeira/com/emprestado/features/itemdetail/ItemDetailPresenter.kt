package macaxeira.com.emprestado.features.itemdetail

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.features.shared.BasePresenterImpl
import macaxeira.com.emprestado.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class ItemDetailPresenter(private val repository: DataRepository) : BasePresenterImpl<ItemDetailContract.View>(),
        ItemDetailContract.Presenter {

    override fun loadData() {
        val item = repository.getSelectedItem()
        if (item?.id != null ) {
            view.get()?.setBorrow(!item.isMine)
            view.get()?.setLent(item.isMine)
            if (item.returnDate.isNotEmpty())
                view.get()?.setReturnedDate(item.returnDate)
            if (item.person != null) {
                val person: Person = item.person!!
                view.get()?.setPersonName(person.name)
                view.get()?.setPersonPhoto(person.photoUri)
            }
            view.get()?.setRemember(item.remember)
        }
    }

    override fun isMineSelected(isMine: Boolean) {
        repository.setIsMine(isMine)
        val textRes = if (isMine) R.string.lend_to else R.string.borrow_from
        view.get()?.changeContactText(textRes)
    }

    override fun isReturnedSelected(isReturned: Boolean) {
        repository.setReturned(isReturned)
    }

    override fun shouldRemember(shouldRemember: Boolean) {
        repository.setShouldRemember(shouldRemember)
    }

    override fun returnDatePressed() {
        val item = repository.getSelectedItem()
        val date = item?.returnDate
        val cal = Calendar.getInstance()

        if (date != null && date.isNotEmpty()) {
            cal.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date)
        }

        view.get()?.openDatePicker(cal)
    }

    override fun returnDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val date = Utils.fromCalendarToString(cal)
        repository.setReturnedDate(date)
        view.get()?.setReturnedDate(date)
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

    override fun getPersonByUri(uri: String) {
        if (!uri.isEmpty())
            disposable.add(repository.getPersonByUri(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            view.get()?.setPersonName(it.name)
                            view.get()?.setPersonPhoto(it.photoUri)
                        }, {
                    view.get()?.showErrorMessage(it)
                })
            )
    }

    override fun saveItem(description: String) {
        if (description.isEmpty()) {
            view.get()?.requiredFieldsEmpty()
        } else {
            repository.setDescription(description)
            disposable.add(repository.saveSelectedItem()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    Consumer {
                                        val item = repository.getSelectedItem()
                                        if (item != null && !item.returnDate.isEmpty()) {
                                            val alarmTime = "${item.returnDate} 10:00"
                                            val date = Utils.fromStringToTime(alarmTime)

                                            if (item.remember && item.person != null) {
                                                view.get()?.createAlarm(it.toInt(), date, item.person!!.name)
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