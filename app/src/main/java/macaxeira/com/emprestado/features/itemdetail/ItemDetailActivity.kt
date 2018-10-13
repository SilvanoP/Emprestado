package macaxeira.com.emprestado.features.itemdetail

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
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
import macaxeira.com.emprestado.data.entities.ItemType
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.features.alarm.AlarmTriggeredReceiver
import macaxeira.com.emprestado.utils.Constants
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
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            data.data.also { contactUri ->
                contentResolver.query(contactUri, projection, null, null, null).apply {
                    moveToFirst()

                    val numberCol = getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    itemDetailPersonPhoneEdit.setText(getString(numberCol))

                    val nameCol = getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    itemDetailPersonNameEdit.setText(getString(nameCol))

                    close()
                }
            }

            val id = data.data.lastPathSegment
            contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                    arrayOf<String>(id), null).apply {
                moveToFirst()

                val emailCol = getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                itemDetailPersonEmailEdit.setText(getString(emailCol))

                close()
            }
        }
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
        val returnDate = itemDetailReturnDateEdit.text.toString()

        presenter.saveItem(description, itemType, isMine, personName, personEmail, personPhone, returnDate)
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

    override fun createAlarm(id: Int) {
        val notification = createNotification()

        val notificationIntent = Intent(this, AlarmTriggeredReceiver::class.java)
        notificationIntent.putExtra(Constants.NOTIFICATION_ID, id)
        notificationIntent.putExtra(Constants.NOTIFICATION, notification)
        val pi = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val returnDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .parse(itemDetailReturnDateEdit.text.toString())

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, returnDate.time, pi)
    }

    private fun createNotification() : Notification {
        val builder: Notification.Builder?
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder = Notification.Builder(this)
        } else {
            builder = Notification.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
        }

        val text: String
        if (itemDetailMineCheckbox.isChecked) {
            text = getString(R.string.notification_return_lent, itemDetailDescriptionEdit.text.toString(),
                    itemDetailPersonNameEdit.text.toString())
        } else {
            text = getString(R.string.notification_return_borrowed, itemDetailDescriptionEdit.text.toString(),
                    itemDetailPersonNameEdit.text.toString())
        }

        builder.setSmallIcon(R.drawable.ic_checked)
                .setContentTitle(getString(R.string.return_date))
                .setContentText(text)

        return builder.build()
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
