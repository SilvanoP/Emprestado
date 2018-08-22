package macaxeira.com.emprestado.features.itemdetail

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import java.lang.ref.WeakReference

class ItemDetailPresenter(val repository: DataRepository) : ItemDetailContract.Presenter {

    lateinit var view: WeakReference<ItemDetailContract.View>

    override fun saveItem() {
        val item = Item()
        repository.saveItem(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { view.get()?.onSaveOrUpdateComplete() }, // onComplete
                        { view.get()?.showErrorMessage(throwable = Throwable("TESTE"))} // onError
                )
    }

    override fun setView(view: ItemDetailContract.View) {
        this.view = WeakReference(view)
    }

}