package macaxeira.com.emprestado.features.itemdetail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.features.alarm.NotificationScheduler
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class ItemDetailActivity : AppCompatActivity(), ItemDetailContract.View, View.OnClickListener,
        View.OnFocusChangeListener, ReturnDateDialog.ReturnDateDialogListener {

    companion object {
        private const val CONTACT_URI = "content://contacts"
        private const val PICK_CONTACT_REQUEST = 1001
        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1002
    }

    private val presenter: ItemDetailContract.Presenter by inject()

    private var person: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.setView(this)
        presenter.loadData()
    }

    override fun fillFields(item: Item?) {
        if (item != null) {
            itemDetailDescriptionEdit.setText(item.description)
            itemDetailBorrowToggle.isChecked = item.isMine
            itemDetailLendToggle.isChecked = item.isMine

            if (item.person != null) {
                fillPersonFields(item.person!!)
            }
            if (!TextUtils.isEmpty(item.returnDate)) {
                itemDetailReturnDateEdit.setText(item.returnDate)
            }
        }

        itemDetailReturnDateEdit.setOnClickListener(this)
        itemDetailDescriptionEdit.onFocusChangeListener = this
    }

    override fun fillPersonFields(person: Person) {
        this.person = person
    }

    @Suppress("UNUSED_PARAMETER")
    fun searchContact(v : View) {
        presenter.searchContacts()
    }

    override fun pickContact() {
        val pickContactIntent = Intent(Intent.ACTION_PICK)
        pickContactIntent.setDataAndType(Uri.parse(CONTACT_URI),ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE)
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (PICK_CONTACT_REQUEST == requestCode && resultCode == Activity.RESULT_OK && data != null) {
            presenter.getPersonByUri(data.data?.path)
        }
    }

    override fun requiredFieldsEmpty() {
        Toast.makeText(this, R.string.error_save_item, Toast.LENGTH_SHORT).show()
        itemDetailDescriptionEdit.error = getString(R.string.required_field_empty)
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
            android.R.id.home -> NavUtils.navigateUpFromSameTask(this)
        }
        return true
    }

    private fun saveItem() {
        val description = itemDetailDescriptionEdit.text.toString()
        val isMine = itemDetailLendToggle.isChecked
        val returnDate = itemDetailReturnDateEdit.text.toString()
        val isNotifiable = itemDetailRememberSwitch.isChecked

        presenter.saveItem(description, isMine, returnDate, isNotifiable, person)
    }

    override fun onSaveOrUpdateComplete() {
        finish()
    }

    override fun verifyPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.error_required_Permission)
                        .setNeutralButton(R.string.ok) { _, _ ->
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS),
                                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
                        }
                builder.create().show()
            }
        } else {
            presenter.searchContactPermissionVerified(true)
        }
    }

    override fun createAlarm(id: Int, time: Long, personName: String) {
        val text: String = if (itemDetailLendToggle.isChecked)
            getString(R.string.notification_return_lent, itemDetailDescriptionEdit.text.toString(),
                    personName)
        else
            getString(R.string.notification_return_borrowed, itemDetailDescriptionEdit.text.toString(),
                    personName)

        NotificationScheduler.setAlarm(this, id, time, text)
    }

    override fun cancelAlarm(id: Int) {
        NotificationScheduler.cancelAlarm(this, id)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS ->
                if (grantResults.isNotEmpty()) {
                    presenter.searchContactPermissionVerified(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                } else {
                    presenter.searchContactPermissionVerified(false)
                }
        }
    }

    override fun showErrorMessage(throwable: Throwable) {
        if (throwable is UnsupportedOperationException) {
            Toast.makeText(this, R.string.error_required_Permission, Toast.LENGTH_LONG).show()
        } else {
            throwable.printStackTrace()
            Toast.makeText(this, R.string.error_save_item, Toast.LENGTH_LONG).show()
        }
    }
}
