package macaxeira.com.emprestado.features.itemdetail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_detail.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.features.alarm.NotificationScheduler
import macaxeira.com.emprestado.utils.CircleTransform
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
    private var personUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.setView(this)
        presenter.loadData()
    }

    override fun fillFields(item: Item?) {
        itemDetailReturnDateEdit.setOnClickListener(this)
        itemDetailDescriptionEdit.onFocusChangeListener = this

        itemDetailBorrowToggle.setOnCheckedChangeListener { _, isChecked ->
            itemDetailLendToggle.isChecked = !isChecked
            changeContactText()
        }
        itemDetailLendToggle.setOnCheckedChangeListener { _, isChecked ->
            itemDetailBorrowToggle.isChecked = !isChecked
            changeContactText()
        }

        if (item != null) {
            itemDetailDescriptionEdit.setText(item.description)
            itemDetailBorrowToggle.isChecked = !item.isMine
            itemDetailLendToggle.isChecked = item.isMine

            if (!TextUtils.isEmpty(item.returnDate)) {
                itemDetailReturnDateEdit.setText(item.returnDate)
            }

            changeContactText()
            if (item.person != null) {
                fillPersonFields(item.person!!)
            } else if (!item.contactUri.isEmpty()) {
                presenter.getPersonByUri(item.contactUri)
            }

            itemDetailRememberSwitch.isChecked = item.remember
        }
    }

    private fun changeContactText() {
        if (itemDetailBorrowToggle.isChecked) {
            itemDetailContactText.text = getString(R.string.borrow_from)
        } else {
            itemDetailContactText.text = getString(R.string.lend_to)
        }
    }

    override fun fillPersonFields(person: Person) {
        this.person = person

        itemDetailContactContainer.visibility = View.VISIBLE

        itemDetailUserNameText.text = person.name
        if (!person.photoUri.isEmpty()) {
            Picasso.get().load(Uri.parse(person.photoUri))
                    .transform(CircleTransform())
                    .into(itemDetailUserPhotoImage)
        } else {
            itemDetailUserPhotoImage.setImageResource(R.drawable.default_person_round)
        }
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
            personUri = data.data?.toString() ?: ""
            presenter.getPersonByUri(personUri)
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
        val remember = itemDetailRememberSwitch.isChecked

        presenter.saveItem(description, isMine, returnDate, remember, person, personUri)
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
