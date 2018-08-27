package macaxeira.com.emprestado.features.listitem

import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView

interface ListItemContract {

    interface View : BaseView {
        fun showItems(items: List<Item>)
    }

    interface Presenter : BasePresenter<View> {
        fun getAllItems()
        fun getItemsByFilter(filter: Boolean)
        fun removeItem(item: Item)
        fun restoreItem(item: Item)
    }
}