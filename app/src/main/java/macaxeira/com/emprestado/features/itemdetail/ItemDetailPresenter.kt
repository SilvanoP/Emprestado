package macaxeira.com.emprestado.features.itemdetail

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.shared.BasePresenterImpl

class ItemDetailPresenter(private val repository: DataRepository) : BasePresenterImpl<ItemDetailContract.View>(),
        ItemDetailContract.Presenter {

    override fun getPersonById(personId: Long) {
        disposable.add(repository.getPersonById(personId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            view.get()?.fillPersonFields(it)
                        }, {
                            view.get()?.showErrorMessage(it)
                        }
                ))
    }

    override fun saveItem() {
        val item = Item()
        disposable.add(repository.saveItem(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            view.get()?.onSaveOrUpdateComplete()
                        },
                        {
                            view.get()?.showErrorMessage(it)
                        }
                ))
    }

}