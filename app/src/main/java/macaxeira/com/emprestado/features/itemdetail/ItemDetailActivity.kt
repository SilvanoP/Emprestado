package macaxeira.com.emprestado.features.itemdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.utils.Constants
import org.koin.android.ext.android.inject

class ItemDetailActivity : AppCompatActivity(), ItemDetailContract.View {

    private val presenter: ItemDetailContract.Presenter by inject()
    private var item: Item? = null
    private var person: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        presenter.setView(this)

        if (intent.extras.size() > 0) {
            item = intent.extras[Constants.ITEM_ARGUMENT] as Item
            fillFields()
        }
    }

    private fun fillFields() {
        if (item?.personId != null) {
            presenter.getPersonById(item!!.personId!!)
        }
    }

    override fun fillPersonFields(person: Person) {

    }

    override fun onSaveOrUpdateComplete() {

    }

    override fun showErrorMessage(throwable: Throwable) {

    }
}
