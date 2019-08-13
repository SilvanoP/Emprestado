package macaxeira.com.emprestado.features.itemdetail

import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.features.shared.BasePresenterImpl
import macaxeira.com.emprestado.utils.Utils
import java.util.*

class ItemDetailPresenter(private val repository: DataRepository) : BasePresenterImpl<ItemDetailContract.View>(),
        ItemDetailContract.Presenter {

    override fun loadData() {
        /*val item = repository.getSelectedItem()
        if (item?.id != null ) {
            view.get()?.setBorrow(!item.isMine)
            view.get()?.setLent(item.isMine)
            view.get()?.setDescription(item.description)
            if (item.returnDate.isNotEmpty())
                view.get()?.setReturnedDate(item.returnDate)
            if (item.person != null) {
                view.get()?.showPerson(true)
                val person: Person = item.person!!
                view.get()?.setPersonName(person.name)
                view.get()?.setPersonPhoto(person.photoUri)
            }
            view.get()?.setReturned(item.isReturned)
            view.get()?.setRemember(item.remember)
        }*/
    }

    override fun isBorrowPressed(isBorrow: Boolean) {
        //repository.setIsMine(!isBorrow)
        val textRes = if (isBorrow) R.string.borrow_from else R.string.lend_to
        view.get()?.changeContactText(textRes)
        view.get()?.setLent(!isBorrow)
    }

    override fun isLentPressed(isLent: Boolean) {
        //repository.setIsMine(isLent)
        val textRes = if (isLent) R.string.lend_to else R.string.borrow_from
        view.get()?.changeContactText(textRes)
        view.get()?.setBorrow(!isLent)
    }

    override fun isReturnedSelected(isReturned: Boolean) {
        //repository.setReturned(isReturned)
    }

    override fun shouldRemember(shouldRemember: Boolean) {
        //repository.setShouldRemember(shouldRemember)
    }

    override fun returnDatePressed() {
        /*val item = repository.getSelectedItem()
        val date = item?.returnDate
        val cal = Calendar.getInstance()

        if (date != null && date.isNotEmpty()) {
            cal.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date)
        }

        view.get()?.openDatePicker(cal)*/
    }

    override fun returnDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val date = Utils.fromCalendarToString(cal)
        //repository.setReturnedDate(date)
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
        /*if (!uri.isEmpty())
            disposable.add(repository.getPersonByUri(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            view.get()?.showPerson(true)
                            view.get()?.setPersonName(it.name)
                            view.get()?.setPersonPhoto(it.photoUri)
                        }, {
                    view.get()?.showErrorMessage(it)
                })
            )*/
    }

    override fun saveItem(description: String) {
        if (description.isEmpty()) {
            view.get()?.requiredFieldsEmpty()
        } else {
            /*repository.setDescription(description)
            disposable.add(repository.saveSelectedItem()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    Consumer {
                                        val item = repository.getSelectedItem()
                                        if (item != null && !item.returnDate.isEmpty()) {
                                            val alarmTime = "${item.returnDate} 10:00"
                                            val date = Utils.fromStringToTime(alarmTime)

                                            if (item.remember && item.person != null && !item.isReturned) {
                                                view.get()?.createAlarm(it.toInt(), date, item.person!!.name)
                                            } else {
                                                view.get()?.cancelAlarm(it.toInt())
                                            }
                                        }
                                        view.get()?.onSaveOrUpdateComplete()
                                    }
                            ))*/
        }
    }

}