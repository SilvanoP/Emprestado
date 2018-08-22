package macaxeira.com.emprestado.features.listitem

import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView

interface ListItemContract {

    interface View : BaseView {
        fun showItem(items: List<Item>)
        fun onItemRemoved()
    }

    interface Presenter : BasePresenter<View> {
        fun getAllItems()
        fun getItemsByFilter(filter: Map<String,String>)
        fun removeItem(item: Item)
    }
}