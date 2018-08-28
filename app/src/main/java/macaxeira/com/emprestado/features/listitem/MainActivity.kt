package macaxeira.com.emprestado.features.listitem

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.itemdetail.ItemDetailActivity
import macaxeira.com.emprestado.utils.Constants
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), ListItemContract.View, ItemsAdapter.ItemsAdapterListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, FilterDialog.FilterDialogListener {

    private val presenter: ListItemContract.Presenter by inject()

    private var adapter: ItemsAdapter? = null
    private var actionMode: ActionMode? = null
    private var actionModeCallback = ActionModeCallback()
    private var items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.setView(this)

        floatingActionButtonAdd.setOnClickListener {
            val intent = Intent(this, ItemDetailActivity::class.java)
            startActivity(intent)
        }

        mainSwipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }

        mainItemsRecycler.layoutManager = LinearLayoutManager(this)
        mainItemsRecycler.itemAnimator = DefaultItemAnimator()
        mainItemsRecycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val itemTouchHelperCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mainItemsRecycler)
        onRefresh()
    }

    private fun onRefresh() {
        mainSwipeRefreshLayout.isRefreshing = true
        presenter.getFilterPreference()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menuMainFilter -> {
                val dialog = FilterDialog.newInstance(-1)
                dialog.show(supportFragmentManager, "FilterDialog")
            }
        }
        return true
    }

    override fun showItems(items: List<Item>) {
        this.items = items.toMutableList()

        mainSwipeRefreshLayout.isRefreshing = false
        if (adapter == null) {
            adapter = ItemsAdapter(this, items.toMutableList(), this)
        } else {
            adapter!!.items = items.toMutableList()
        }

        mainItemsRecycler.adapter = adapter
    }

    override fun onClickItem(position: Int) {
        val item = items[position]
        val intent = Intent(this, ItemDetailActivity::class.java)
        intent.putExtra(Constants.ITEM_ARGUMENT, item)
        startActivity(intent)
    }

    override fun onLongClickItem(position: Int) {
        toggleSelection(position)
    }

    override fun onIconClicked(position: Int) {
        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback)
        }
        adapter?.toggleSelection(position)
        val count = adapter?.selectedItems?.size()

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int?) {
        if (viewHolder is ItemsAdapter.ItemViewHolder) {
            val deletedItem = items[viewHolder.adapterPosition]
            val deletedPosition = viewHolder.adapterPosition

            adapter?.removeItem(deletedPosition)
            presenter.removeItem(deletedItem)
            items.removeAt(deletedPosition)
            val snackbar = Snackbar.make(mainFrameLayout, R.string.item_swiped, Snackbar.LENGTH_LONG)
            snackbar.setAction(R.string.undo)  {
                items.add(deletedPosition, deletedItem)
                adapter?.restoreItem(deletedItem, deletedPosition)
                presenter.restoreItem(deletedItem)
            }
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
    }

    override fun filter(filter: Int) {
        if (filter == -1) return

        presenter.saveFilterPreference(filter)
        mainSwipeRefreshLayout.isRefreshing = true

        when(filter) {
            R.id.dialogFilterButtonAll -> {
                presenter.getAllItems()
                title = getString(R.string.all_loans)
            }
            R.id.dialogFilterButtonBorrowed ->  {
                presenter.getItemsByOwner(false)
                title = getString(R.string.borrowed)
            }
            R.id.dialogFilterButtonLent -> {
                presenter.getItemsByOwner(true)
                title = getString(R.string.lent)
            }
            R.id.dialogFilterButtonReturned -> {
                presenter.getItemsByReturned(true)
                title = getString(R.string.returned)
            }
        }
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
            adapter?.clearSelections()
            actionMode = null
            mainItemsRecycler.post {
                // Runnable
                adapter?.resetAnimationIndex()
            }
        }

    }
}
