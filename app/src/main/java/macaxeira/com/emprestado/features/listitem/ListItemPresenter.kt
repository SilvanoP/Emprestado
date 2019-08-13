package macaxeira.com.emprestado.features.listitem

import android.util.SparseArray
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenterImpl
import macaxeira.com.emprestado.utils.Utils

class ListItemPresenter(private val repository: DataRepository) : BasePresenterImpl<ListItemContract.View>(),
        ListItemContract.Presenter {

    override fun loadData(changedItems: Boolean) {
        val filter = repository.getFilterPreference()
        view.get()?.isRefreshing(true)
        view.get()?.changeTitle(filter)
        if (changedItems) repository.onRefreshCachedItems()
        repository.setUser(null)
        repository.getItemsByFilter(filter, object: ListItemCallback {
            override fun returnItems(items: List<Item>) {
                view.get()?.isRefreshing(false)
                if (items.isEmpty()) {
                    view.get()?.showEmptyList()
                } else {
                    view.get()?.showItems(items)
                }
            }

            override fun error(error: Throwable) {
                error.printStackTrace()
                view.get()?.isRefreshing(false)
                view.get()?.showErrorMessage(error)
                view.get()?.showEmptyList()
            }
        })
    }

    override fun onAddItem() {
        repository.onRefreshSelectedItem()
        view.get()?.callNextActivity()
    }

    override fun loadItemsByFilter(filter: Int) {
        view.get()?.changeTitle(filter)
        onSwipeRefresh(filter)
    }

    override fun onSwipeRefresh(filter: Int) {
        view.get()?.isRefreshing(true)
        repository.getItemsByFilter(filter, object: ListItemCallback {
            override fun returnItems(items: List<Item>) {
                view.get()?.isRefreshing(false)
                view.get()?.showItems(items)
            }

            override fun error(error: Throwable) {
                error.printStackTrace()
                view.get()?.isRefreshing(false)
                view.get()?.showErrorMessage(error)
            }
        })
    }

    override fun onItemsToRemove(itemsSparse: SparseArray<Item>) {
        val list = Utils.fromSparseToList(itemsSparse)
        repository.removeItems(list, object: ListItemCallback {
            override fun returnItems(items: List<Item>) {
                view.get()?.cancelAlarm(list)
                if (items.size > 1) {
                    view.get()?.removeSelectedItems(list)
                } else{
                    view.get()?.removeItem(itemsSparse.keyAt(0))
                }
                view.get()?.displaySnackBar(itemsSparse)
            }

            override fun error(error: Throwable) {
                error.printStackTrace()
                view.get()?.showErrorMessage(error)
            }
        })
    }

    override fun onItemsReturned(itemsSparse: SparseArray<Item>) {
        val list = Utils.fromSparseToList(itemsSparse)
        repository.onItemsReturned(list, object: ListItemCallback {
            override fun returnItems(items: List<Item>) {
                view.get()?.updateItems()
            }

            override fun error(error: Throwable) {
                error.printStackTrace()
                view.get()?.showErrorMessage(error)
            }
        })
    }

    override fun onItemSelected(item: Item) {
        repository.onItemSelected(item)
        view.get()?.callNextActivity()
    }

    override fun removeItem(item: Item) {
        repository.removeItem(item, object: ListItemCallback {
            override fun returnItems(items: List<Item>) { }

            override fun error(error: Throwable) {
                error.printStackTrace()
                view.get()?.showErrorMessage(error)
            }
        })
    }

    override fun restoreItem(item: Item) {
        repository.restoreItem(item, object: ListItemCallback {
            override fun returnItems(items: List<Item>) { }

            override fun error(error: Throwable) {
                error.printStackTrace()
                view.get()?.showErrorMessage(error)
            }
        })
    }

    override fun saveFilterPreference(filter: Int) {
        repository.saveFilterPreference(filter)
    }

}