package macaxeira.com.emprestado.features.itemdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_item_detail.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.ItemType
import macaxeira.com.emprestado.data.entities.Person
import org.koin.android.ext.android.inject

class ItemDetailActivity : AppCompatActivity(), ItemDetailContract.View {

    private val presenter: ItemDetailContract.Presenter by inject()
    private var adapter:TypesListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.setView(this)
        presenter.loadData()
    }

    override fun fillFields(item: Item?) {
        if (adapter == null) {
            val types = ItemType.values().toList()
            adapter = TypesListAdapter(this, android.R.layout.simple_list_item_1, types)
            itemDetailTypeSpinner.adapter = adapter
        }

        if (item != null) {
            itemDetailTypeSpinner.setSelection(adapter!!.getPosition(item.itemType))
            itemDetailDescriptionEdit.setText(item.description)
            itemDetailMineCheckbox.isChecked = item.isMine
            if (item.person != null) {
                fillPersonFields(item.person!!)
            }
        }
    }

    override fun fillPersonFields(person: Person) {
        itemDetailPersonNameEdit.setText(person.name)
        itemDetailPersonEmailEdit.setText(person.email)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menuItemDetailSave -> saveItem()
        }
        return true
    }

    private fun saveItem() {

    }

    override fun onSaveOrUpdateComplete() {

    }

    override fun showErrorMessage(throwable: Throwable) {

    }
}
