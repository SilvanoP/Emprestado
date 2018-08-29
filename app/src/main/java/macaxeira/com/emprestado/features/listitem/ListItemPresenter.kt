package macaxeira.com.emprestado.features.listitem

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenterImpl

class ListItemPresenter(private val repository: DataRepository) : BasePresenterImpl<ListItemContract.View>(),
        ListItemContract.Presenter {

    override fun getAllItems() {
        disposable.add(repository.getAllItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            view.get()?.showItems(it)
                        },
                        {
                            view.get()?.showErrorMessage(it)
                        }
                ))
    }

    override fun getFilterPreference() {
        val filter = repository.getFilterPreference()
        view.get()?.filter(filter)
    }

    override fun getItemsByOwner(isMine: Boolean) {
        disposable.add(repository.getItemsByOwner(isMine)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            view.get()?.showItems(it)
                        },
                        {
                            view.get()?.showErrorMessage(it)
                        }
                ))
    }

    override fun getItemsByReturned(isReturned: Boolean) {
        disposable.add(repository.getItemsByReturned(isReturned)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            view.get()?.showItems(it)
                        },
                        {
                            view.get()?.showErrorMessage(it)
                        }
                ))
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