package macaxeira.com.emprestado.features.listitem

import android.util.SparseArray
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView

interface ListItemContract {

    interface View : BaseView {
        fun changeTitle(filter: Int)
        fun isRefreshing(refreshing: Boolean)
        fun showEmptyList()
        fun showItems(items: List<Item>)
        fun updateItems()
        fun removeItem(position: Int)
        fun removeSelectedItems(deletedItems: List<Item>)
        fun cancelAlarm(deletedItems: List<Item>)
        fun displaySnackBar(deletedItems: SparseArray<Item>)
        fun callNextActivity()
    }

    interface Presenter : BasePresenter<View> {
        fun loadData(changedItems: Boolean)
        fun onAddItem()
        fun loadItemsByFilter(filter:Int)
        fun onSwipeRefresh(filter: Int)
        fun onItemsToRemove(itemsSparse: SparseArray<Item>)
        fun onItemsReturned(itemsSparse: SparseArray<Item>)
        fun onItemSelected(item: Item)
        fun removeItem(item: Item)
        fun restoreItem(item: Item)
        fun saveFilterPreference(filter: Int)
    }
}