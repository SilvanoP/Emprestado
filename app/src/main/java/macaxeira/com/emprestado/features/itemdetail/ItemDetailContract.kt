package macaxeira.com.emprestado.features.itemdetail

import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView
import java.util.*

interface ItemDetailContract {

    interface View : BaseView {
        fun onSaveOrUpdateComplete()
        fun fillPersonFields(person: Person)
        fun fillFields(item: Item?)
        fun requiredFieldsEmpty()
        fun openDatePicker(returnDate: Calendar)
        fun verifyPermissions()
        fun pickContact()
        fun createAlarm(id: Int, time: Long, personName: String)
        fun cancelAlarm(id: Int)
    }

    interface Presenter: BasePresenter<View> {
        fun loadData()
        fun setReturnDate()
        fun searchContacts()
        fun searchContactPermissionVerified(hasPermission: Boolean)
        fun getPersonByUri(uri: String)
        fun saveItem(description: String, isMine: Boolean, returnDate: String, remember: Boolean,
                     person: Person?, personUri: String)
    }
}