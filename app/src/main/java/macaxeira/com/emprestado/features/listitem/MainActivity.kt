package macaxeira.com.emprestado.features.listitem

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.features.alarm.AlarmTriggeredReceiver
import macaxeira.com.emprestado.features.itemdetail.ItemDetailActivity
import macaxeira.com.emprestado.utils.Constants
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), ListItemContract.View, ItemsAdapter.ItemsAdapterListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, FilterDialog.FilterDialogListener {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1001
    }

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

        verifyPermissions()
        presenter.loadData(false)
    }

    override fun onResume() {
        super.onResume()

        presenter.loadData(true)
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

    override fun updateItems() {
        mainItemsRecycler.adapter?.notifyDataSetChanged()
    }

    override fun removeItem(position: Int) {
        val adapter = mainItemsRecycler.adapter as ItemsAdapter
        adapter.removeItem(position)
        if (items.size > position) {
            items.removeAt(position)
        }
    }

    override fun removeSelectedItems(deletedItems: List<Item>) {
        val adapter = mainItemsRecycler.adapter as ItemsAdapter
        adapter.removeItems(deletedItems)
        items.removeAll(deletedItems)
    }

    override fun cancelAlarm(deletedItems: List<Item>) {
        for (item in deletedItems) {
            if (!TextUtils.isEmpty(item.returnDate)) {
                val notificationIntent = Intent(this, AlarmTriggeredReceiver::class.java)
                notificationIntent.putExtra(Constants.NOTIFICATION_ID, item.id)
                val pi = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pi)
            }
        }
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
            val sparse = SparseArray<Item>()
            sparse.put(deletedPosition, deletedItem)
            presenter.onItemsToRemove(sparse)
        }
    }

    override fun displaySnackBar(deletedItems: SparseArray<Item>) {
        val snackBar = Snackbar.make(mainFrameLayout, R.string.item_swiped, Snackbar.LENGTH_LONG)
        snackBar.setAction(R.string.undo)  {
            val adapter = mainItemsRecycler.adapter as ItemsAdapter

            var index = 0
            while (index < deletedItems.size()) {
                val deletedPosition = deletedItems.keyAt(index)
                val deletedItem = deletedItems[deletedPosition]
                items.add(deletedPosition, deletedItem)
                adapter.restoreItem(deletedItem, deletedPosition)
                presenter.restoreItem(deletedItem)
                index++
            }
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
        Toast.makeText(this, R.string.error_load_item, Toast.LENGTH_SHORT).show()
    }

    private fun verifyPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        }
    }

    inner class ActionModeCallback : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            val adapter = mainItemsRecycler.adapter as ItemsAdapter
            val selectedItems = SparseArray<Item>()
            val sparse = adapter.selectedItems

            if (item?.itemId == R.id.menuMainDelete) {
                var index = 0
                while (index < sparse.size()) {
                    val deletedPosition = sparse.keyAt(index)
                    selectedItems.put(deletedPosition, items[deletedPosition])
                    index++
                }

                presenter.onItemsToRemove(selectedItems)
                return true
            }

            if (item?.itemId == R.id.menuMainReturned) {
                var index = 0
                while (index < sparse.size()) {
                    val selectedPosition = sparse.keyAt(index)
                    val selectedItem = items[selectedPosition]
                    selectedItem.isReturned = true
                    selectedItems.put(selectedPosition, selectedItem)
                    index++
                }

                presenter.onItemsReturned(selectedItems)
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
