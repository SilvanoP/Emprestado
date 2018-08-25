package macaxeira.com.emprestado.features.listitem

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.itemdetail.ItemDetailActivity
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), ListItemContract.View, ItemsAdapter.ItemsAdapterListener {

    private val presenter: ListItemContract.Presenter by inject()
    private var adapter: ItemsAdapter? = null
    private var actionMode: ActionMode? = null
    private var actionModeCallback = ActionModeCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.setView(this)

        floatingActionButtonAdd.setOnClickListener {
            val intent = Intent(this, ItemDetailActivity::class.java)
            startActivity(intent)
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
            adapter = ItemsAdapter(this, items.toMutableList(), this)
        } else {
            adapter!!.items = items.toMutableList()
        }

        mainItemsRecycler.adapter = adapter
    }

    override fun onLongClickItem(position: Int) {
        actionMode ?: startSupportActionMode(actionModeCallback)
        adapter?.toggleSelection(position)
        val count = adapter?.selectedItems?.size()

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    override fun onItemRemoved() {

    }

    override fun showErrorMessage(throwable: Throwable) {

    }

    inner class ActionModeCallback : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            if (item?.itemId == R.id.menuMainDelete) {
                return true
            }

            return false
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_main_action, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            mainItemsRecycler.post {
                // Runnable
            }
        }

    }
}
