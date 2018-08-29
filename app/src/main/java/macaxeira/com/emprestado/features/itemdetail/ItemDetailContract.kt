package macaxeira.com.emprestado.features.itemdetail

import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView

interface ItemDetailContract {

    interface View : BaseView {
        fun onSaveOrUpdateComplete()
        fun fillPersonFields(person: Person)
        fun fillFields(item: Item?)
    }

    interface Presenter: BasePresenter<View> {
        fun loadData()
        fun saveItem()
        fun getPersonById(personId: Long)
    }
}