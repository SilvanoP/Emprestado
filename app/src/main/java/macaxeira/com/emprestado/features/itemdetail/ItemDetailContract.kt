package macaxeira.com.emprestado.features.itemdetail

import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.ItemType
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView
import java.util.*

interface ItemDetailContract {

    interface View : BaseView {
        fun onSaveOrUpdateComplete()
        fun fillPersonFields(person: Person)
        fun fillFields(item: Item?)
        fun openDatePicker(returnDate: Calendar)
    }

    interface Presenter: BasePresenter<View> {
        fun loadData()
        fun setReturnDate()
        fun saveItem(description: String, itemType: ItemType, isMine: Boolean, personName: String,
                     personEmail: String, personPhone: String)
        fun getPersonById(personId: Long)
    }
}