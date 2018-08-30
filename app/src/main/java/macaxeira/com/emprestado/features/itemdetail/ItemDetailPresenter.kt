package macaxeira.com.emprestado.features.itemdetail

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.ItemType
import macaxeira.com.emprestado.features.shared.BasePresenterImpl
import java.text.SimpleDateFormat
import java.util.*

class ItemDetailPresenter(private val repository: DataRepository) : BasePresenterImpl<ItemDetailContract.View>(),
        ItemDetailContract.Presenter {

    override fun loadData() {
        val item = repository.getSelectedItem()
        view.get()?.fillFields(item)
    }

    override fun setReturnDate() {
        val item = repository.getSelectedItem()
        val date = item?.returnDate
        val cal = Calendar.getInstance()

        if (date != null && date != "") {
            cal.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date)
        }

        view.get()?.openDatePicker(cal)
    }

    override fun getPersonById(personId: Long) {
        disposable.add(
                repository.getPersonById(personId)
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

    override fun saveItem(description: String, itemType: ItemType, isMine: Boolean, personName: String,
                          personEmail: String, personPhone: String) {
        disposable.add(
                repository.saveItem(description, itemType, isMine, personName, personEmail, personPhone)
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