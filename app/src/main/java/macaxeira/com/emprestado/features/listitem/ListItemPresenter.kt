package macaxeira.com.emprestado.features.listitem

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenterImpl

class ListItemPresenter(private val repository: DataRepository) : BasePresenterImpl<ListItemContract.View>(),
        ListItemContract.Presenter {

    override fun loadData() {
        view.get()?.isRefreshing(true)
        val filter = repository.getFilterPreference()
        disposable.add(repository.getItemsByFilter(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.get()?.isRefreshing(false)
                    view.get()?.showItems(it)
                }, {
                    view.get()?.isRefreshing(false)
                    view.get()?.showErrorMessage(it)
                }
                ))
    }

    override fun onAddItem() {
        repository.onRefreshSelectedItem()
        view.get()?.callNextActivity()
    }

    override fun loadItemsByFilter(filter: Int) {
        view.get()?.isRefreshing(true)
        disposable.add(repository.getItemsByFilter(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.get()?.isRefreshing(false)
                    view.get()?.showItems(it)
                }, {
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
                    view.get()?.isRefreshing(false)
                    view.get()?.showErrorMessage(it)
                }
                ))
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
                            view.get()?.showErrorMessage(it)
                        }
                ))
    }

    override fun saveFilterPreference(filter: Int) {
        repository.saveFilterPreference(filter)
    }

}