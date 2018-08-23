package macaxeira.com.emprestado.features.listitem

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), ListItemContract.View {

    private val presenter: ListItemContract.Presenter by inject()
    private var adapter: ItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingActionButtonAdd.setOnClickListener {
            //TODO go to item detail activity
        }

        mainItemsRecycler.layoutManager = LinearLayoutManager(this)
        presenter.getAllItems()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menuMainFilter -> {
                val dialog = FilterDialog()
                dialog.show(supportFragmentManager, "FilterDialog")
            }
        }
        return true
    }

    override fun showItems(items: List<Item>) {
        if (adapter == null) {
            adapter = ItemsAdapter(this, items.toMutableList())
        } else {
            adapter!!.items = items.toMutableList()
        }

        mainItemsRecycler.adapter = adapter
    }

    override fun onItemRemoved() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showErrorMessage(throwable: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
