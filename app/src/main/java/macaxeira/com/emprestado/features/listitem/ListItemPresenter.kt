package macaxeira.com.emprestado.features.listitem

import android.util.SparseArray
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenterImpl
import macaxeira.com.emprestado.utils.Utils

class ListItemPresenter(private val repository: DataRepository) : BasePresenterImpl<ListItemContract.View>(),
        ListItemContract.Presenter {

    override fun loadData() {
        val filter = repository.getFilterPreference()
        view.get()?.isRefreshing(true)
        view.get()?.changeTitle(filter)
        disposable.add(repository.getItemsByFilter(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.get()?.isRefreshing(false)
                    if (it.isEmpty()) {
                        view.get()?.showEmptyList()
                    } else {
                        view.get()?.showItems(it)
                    }
                }, {
                    it.printStackTrace()
                    view.get()?.isRefreshing(false)
                    view.get()?.showErrorMessage(it)
                    view.get()?.showEmptyList()
                }
                ))
    }

    override fun onAddItem() {
        repository.onRefreshSelectedItem()
        view.get()?.callNextActivity()
    }

    override fun loadItemsByFilter(filter: Int) {
        view.get()?.isRefreshing(true)
        view.get()?.changeTitle(filter)
        disposable.add(
                repository.getItemsByFilter(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.get()?.isRefreshing(false)
                    view.get()?.showItems(it)
                }, {
                    it.printStackTrace()
                    view.get()?.isRefreshing(false)
                    view.get()?.showErrorMessage(it)
                }
                ))
    }

    override fun onSwipeRefresh(filter: Int) {
        view.get()?.isRefreshing(true)
        disposable.add(repository.getItemsByFilter(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.get()?.isRefreshing(false)
                    view.get()?.showItems(it)
                }, {
                    it.printStackTrace()
                    view.get()?.isRefreshing(false)
                    view.get()?.showErrorMessage(it)
                }
                ))
    }

    override fun onItemsToRemove(items: SparseArray<Item>) {
        val list = Utils.fromSparseToList(items)
        disposable.add(repository.removeItems(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (items.size() > 1) {
                        view.get()?.removeSelectedItems(list)
                    } else{
                        view.get()?.removeItem(items.keyAt(0))
                    }
                    view.get()?.displaySnackBar(items)
                },{
                    it.printStackTrace()
                    view.get()?.showErrorMessage(it)
                })
        )
    }

    override fun onItemSelected(item: Item) {
        repository.onItemSelected(item)
        view.get()?.callNextActivity()
    }

    override fun removeItem(item: Item) {
        disposable.add(repository.removeItem(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { },
                        {
                            it.printStackTrace()
                            view.get()?.showErrorMessage(it)
                        }
                ))
    }

    override fun restoreItem(item: Item) {
        disposable.add(repository.saveItem(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { },
                        {
                            it.printStackTrace()
                            view.get()?.showErrorMessage(it)
                        }
                ))
    }

    override fun saveFilterPreference(filter: Int) {
        repository.saveFilterPreference(filter)
    }

}