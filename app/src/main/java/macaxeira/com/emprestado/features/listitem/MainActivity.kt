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
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.itemdetail.ItemDetailActivity
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), ListItemContract.View, ItemsAdapter.ItemsAdapterListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, FilterDialog.FilterDialogListener {

    private val presenter: ListItemContract.Presenter by inject()

    private var actionMode: ActionMode? = null
    private var actionModeCallback = ActionModeCallback()
    private var filter = -1
    private var items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.setView(this)

        floatingActionButtonAdd.setOnClickListener {
            presenter.onAddItem()
        }

        mainSwipeRefreshLayout.setOnRefreshListener {
            presenter.onSwipeRefresh(filter)
        }

        setupRecyclerView()

        presenter.loadData()
    }

    private fun setupRecyclerView() {
        mainItemsRecycler.layoutManager = LinearLayoutManager(this)
        mainItemsRecycler.itemAnimator = DefaultItemAnimator()
        mainItemsRecycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val adapter = ItemsAdapter(this, items, this)
        mainItemsRecycler.adapter = adapter

        // Item swipe
        val itemTouchHelperCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mainItemsRecycler)
    }

    override fun changeTitle(filter: Int) {
        this.filter = filter
        title = getString(R.string.all_loans)

        when(filter) {
            R.id.dialogFilterButtonBorrowed ->  title = getString(R.string.borrowed)
            R.id.dialogFilterButtonLent -> title = getString(R.string.lent)
            R.id.dialogFilterButtonReturned -> title = getString(R.string.returned)
        }
    }

    override fun isRefreshing(refreshing: Boolean) {
        mainSwipeRefreshLayout.isRefreshing = refreshing
    }

    override fun callNextActivity() {
        val intent = Intent(this, ItemDetailActivity::class.java)
        startActivity(intent)
    }

    override fun showEmptyList() {
        mainItemsRecycler.visibility = View.GONE
        mainEmptyList.visibility = View.VISIBLE
    }

    override fun showItems(items: List<Item>) {
        mainItemsRecycler.visibility = View.VISIBLE
        mainEmptyList.visibility = View.GONE

        this.items = items.toMutableList()

        val adapter = mainItemsRecycler.adapter as ItemsAdapter
        adapter.items = this.items
        adapter.notifyDataSetChanged()
    }

    override fun onClickItem(position: Int) {
        val item = items[position]
        presenter.onItemSelected(item)
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

        val adapter = mainItemsRecycler.adapter as ItemsAdapter
        adapter.toggleSelection(position)
        val count = adapter.selectedItems.size()

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

            val adapter = mainItemsRecycler.adapter as ItemsAdapter
            adapter.removeItem(deletedPosition)

            presenter.removeItem(deletedItem)
            items.removeAt(deletedPosition)
            displaySnackBar(deletedItem, deletedPosition)
        }
    }

    private fun displaySnackBar(deletedItem: Item, deletedPosition: Int) {
        val snackBar = Snackbar.make(mainFrameLayout, R.string.item_swiped, Snackbar.LENGTH_LONG)
        snackBar.setAction(R.string.undo)  {
            items.add(deletedPosition, deletedItem)
            val adapter = mainItemsRecycler.adapter as ItemsAdapter
            adapter.restoreItem(deletedItem, deletedPosition)
            presenter.restoreItem(deletedItem)
        }
        snackBar.setActionTextColor(Color.YELLOW)
        snackBar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menuMainFilter -> openFilterDialog()
        }
        return true
    }

    private fun openFilterDialog() {
        val dialog = FilterDialog.newInstance(filter)
        dialog.show(supportFragmentManager, "FilterDialog")
    }

    override fun filter(filter: Int) {
        presenter.loadItemsByFilter(filter)
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
            val adapter = mainItemsRecycler.adapter as ItemsAdapter
            adapter.clearSelections()
            actionMode = null
            mainItemsRecycler.post {
                // Runnable
                adapter.resetAnimationIndex()
            }
        }

    }
}
