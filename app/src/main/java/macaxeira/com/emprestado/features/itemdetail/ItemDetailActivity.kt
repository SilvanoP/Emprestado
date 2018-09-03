package macaxeira.com.emprestado.features.itemdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_item_detail.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.ItemType
import macaxeira.com.emprestado.data.entities.Person
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class ItemDetailActivity : AppCompatActivity(), ItemDetailContract.View, View.OnClickListener,
        View.OnFocusChangeListener, ReturnDateDialog.ReturnDateDialogListener {

    private val presenter: ItemDetailContract.Presenter by inject()
    private var adapter: TypesListAdapter? = null

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
            if (!TextUtils.isEmpty(item.returnDate)) {
                itemDetailReturnDateEdit.setText(item.returnDate)
            }
        }

        itemDetailReturnDateEdit.setOnClickListener(this)
        itemDetailDescriptionEdit.onFocusChangeListener = this
        itemDetailPersonNameEdit.onFocusChangeListener = this
    }

    override fun fillPersonFields(person: Person) {
        itemDetailPersonNameEdit.setText(person.name)
        itemDetailPersonEmailEdit.setText(person.email)
        itemDetailPersonPhoneEdit.setText(person.phone)
    }

    override fun requiredFieldsEmpty() {
        Toast.makeText(this, R.string.error_save_item, Toast.LENGTH_SHORT).show()
        itemDetailDescriptionEdit.error = getString(R.string.required_field_empty)
        itemDetailPersonNameEdit.error = getString(R.string.required_field_empty)
    }

    override fun openDatePicker(returnDate: Calendar) {
        val dialog = ReturnDateDialog.newInstance(returnDate)
        dialog.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
        itemDetailReturnDateEdit.setText(date)
    }

    override fun onClick(v: View?) {
        presenter.setReturnDate()
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        val edit = v as EditText
        if (!hasFocus && TextUtils.isEmpty(edit.text)) {
            edit.error = getString(R.string.required_field_empty)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuItemDetailSave -> saveItem()
        }
        return true
    }

    private fun saveItem() {
        val description = itemDetailDescriptionEdit.text.toString()
        val itemType = itemDetailTypeSpinner.selectedItem as ItemType
        val isMine = !itemDetailMineCheckbox.isChecked
        val personName = itemDetailPersonNameEdit.text.toString()
        val personEmail = itemDetailPersonEmailEdit.text.toString()
        val personPhone = itemDetailPersonPhoneEdit.text.toString()

        presenter.saveItem(description, itemType, isMine, personName, personEmail, personPhone)
    }

    override fun onSaveOrUpdateComplete() {
        finish()
    }

    override fun showErrorMessage(throwable: Throwable) {
        throwable.printStackTrace()
        Toast.makeText(this, R.string.error_save_item, Toast.LENGTH_LONG).show()
    }
}
