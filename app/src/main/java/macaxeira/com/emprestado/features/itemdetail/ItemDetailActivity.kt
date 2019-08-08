package macaxeira.com.emprestado.features.itemdetail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_detail.*
import macaxeira.com.emprestado.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        init()
        presenter.setView(this)
        presenter.loadData()
    }

    fun init() {
        itemDetailReturnDateEdit.setOnClickListener(this)
        itemDetailDescriptionEdit.onFocusChangeListener = this

        itemDetailBorrowToggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.isMineSelected(!isChecked)
        }
        itemDetailLendToggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.isMineSelected(isChecked)
        }

        itemDetailReturnedCheck.setOnCheckedChangeListener { _, isChecked ->
            presenter.isReturnedSelected(isChecked)
        }

        itemDetailRememberSwitch.setOnCheckedChangeListener { _, isChecked ->
            presenter.shouldRemember(isChecked)
        }
    }

    override fun setBorrow(isBorrowed: Boolean) {
        if (itemDetailBorrowToggle.isChecked != isBorrowed) {
            itemDetailBorrowToggle.isChecked = isBorrowed
        }
    }

    override fun setLent(isLent: Boolean) {
        if (itemDetailLendToggle.isChecked != isLent) {
            itemDetailLendToggle.isChecked = isLent
        }
    }

    override fun setDescription(description: String) {
        itemDetailDescriptionEdit.setText(description)
    }

    override fun setReturnedDate(returnDate: String) {
        itemDetailReturnDateEdit.setText(returnDate)
    }

    override fun showPerson(isVisible: Boolean) {
        itemDetailContactContainer.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun setPersonName(name: String) {
        itemDetailUserNameText.text = name
    }

    override fun setPersonPhoto(photoUri: String) {
        if (!photoUri.isEmpty()) {
            Picasso.get().load(Uri.parse(photoUri))
                    .transform(CircleTransform())
                    .into(itemDetailUserPhotoImage)
        } else {
            itemDetailUserPhotoImage.setImageResource(R.drawable.default_person_round)
        }
    }

    override fun changeContactText(textRes: Int) {
        itemDetailContactText.text = getString(textRes)
    }

    override fun setReturned(isReturned: Boolean) {
        itemDetailReturnedCheck.isChecked = isReturned
    }

    override fun setRemember(remember: Boolean) {
        itemDetailRememberSwitch.isChecked = remember
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
            val personUri = data.data?.toString() ?: ""
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
        presenter.returnDateSelected(year, month, dayOfMonth)
    }

    override fun onClick(v: View?) {
        presenter.returnDatePressed()
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

        presenter.saveItem(description)
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
