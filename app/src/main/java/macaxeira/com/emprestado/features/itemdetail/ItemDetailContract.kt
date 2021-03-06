package macaxeira.com.emprestado.features.itemdetail

import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView
import java.util.*

interface ItemDetailContract {

    interface View : BaseView {
        fun onSaveOrUpdateComplete()
        fun setBorrow(isBorrowed: Boolean)
        fun setLent(isLent: Boolean)
        fun setDescription(description: String)
        fun setReturnedDate(returnDate: String)
        fun showPerson(isVisible: Boolean)
        fun setPersonName(name: String)
        fun setPersonPhoto(photoUri: String)
        fun changeContactText(textRes: Int)
        fun setReturned(isReturned: Boolean)
        fun setRemember(remember: Boolean)
        fun requiredFieldsEmpty()
        fun openDatePicker(returnDate: Calendar)
        fun verifyPermissions()
        fun pickContact()
    }

    interface Presenter: BasePresenter<View> {
        fun loadData()
        fun isBorrowPressed(isBorrow: Boolean)
        fun isLentPressed(isLent: Boolean)
        fun returnDatePressed()
        fun returnDateSelected(year: Int, month: Int, dayOfMonth: Int)
        fun searchContacts()
        fun searchContactPermissionVerified(hasPermission: Boolean)
        fun getPersonByUri(uri: String)
        fun isReturnedSelected(isReturned: Boolean)
        fun shouldRemember(shouldRemember: Boolean)
        fun saveItem(description: String)
    }
}