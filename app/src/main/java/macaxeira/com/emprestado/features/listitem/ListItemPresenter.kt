package macaxeira.com.emprestado.features.listitem

import macaxeira.com.emprestado.data.DataRepository
import java.lang.ref.WeakReference

class ListItemPresenter(dataRepository: DataRepository) : ListItemContract.Presenter {

    lateinit var view: WeakReference<ListItemContract.View>

    override fun getAllItems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemsByFilter(fitler: Map<String, String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setView(view: ListItemContract.View) {
        this.view = WeakReference(view)
    }

}