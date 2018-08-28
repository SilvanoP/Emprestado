package macaxeira.com.emprestado.features.listitem

import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView

interface ListItemContract {

    interface View : BaseView {
        fun showItems(items: List<Item>)
        fun filter(filter: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun getAllItems()
        fun getFilterPreference()
        fun getItemsByOwner(isMine: Boolean)
        fun getItemsByReturned(isReturned: Boolean)
        fun removeItem(item: Item)
        fun restoreItem(item: Item)
        fun saveFilterPreference(filter: Int)
    }
}