package macaxeira.com.emprestado.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import io.reactivex.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.utils.Constants

class DataRepository(private val context: Context, private val dataSourceLocal: DataSource, private val prefs: SharedPreferences) {

    private var cachedItems: MutableList<Item> = mutableListOf()
    private var cachedPeople: MutableList<Person> = mutableListOf()
    private var selectedItem: Item? = null
    private var selectedPerson: Person? = null

    fun saveItem(item: Item): Single<Long> {
        return dataSourceLocal.saveItem(item).doOnSuccess {
            cachedItems.add(selectedItem!!)
        }
    }

    fun saveItem(description: String, isMine: Boolean, returnDate: String): Single<Long> {
        if (selectedItem == null) {
            selectedItem = Item()
        }

        selectedItem!!.description = description
        selectedItem!!.isMine = isMine
        selectedItem!!.returnDate = returnDate

        return dataSourceLocal.saveItem(selectedItem!!).doOnSuccess {
            val index = cachedItems.indexOf(selectedItem!!)
            if (index != -1) {
                cachedItems.removeAt(index)
                cachedItems.add(index, selectedItem!!)
            } else {
                cachedItems.add(selectedItem!!)
            }
        }
    }

    fun updateItems(items: List<Item>): Completable {
        return dataSourceLocal.updateItems(items).doOnComplete {
            for (item in items) {
                val index = cachedItems.indexOf(item)
                cachedItems.removeAt(index)
                cachedItems.add(index, item)
            }
        }
    }

    fun removeItem(item: Item): Completable {
        return dataSourceLocal.removeItem(item).doOnComplete {
            cachedItems.remove(item)
        }
    }

    fun removeItems(items: List<Item>): Completable {
        return dataSourceLocal.removeItems(items).doOnComplete {
            cachedItems.removeAll(items)
        }
    }

    fun onItemSelected(item: Item) {
        selectedItem = item
    }

    fun onRefreshSelectedItem() {
        selectedItem = null
    }

    fun getSelectedItem(): Item? {
        return selectedItem
    }

    fun getItemsByFilter(filter: Int): Single<List<Item>> {
        when (filter) {
            R.id.dialogFilterButtonBorrowed ->
                return getItemsByOwner(false)
            R.id.dialogFilterButtonLent ->
                return getItemsByOwner(true)
            R.id.dialogFilterButtonReturned ->
                return getItemsByReturned(true)
        }

        return getAllItems()
    }

    private fun getAllItems(): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Single.just(cachedItems)
        }

        return dataSourceLocal.getAllItems().flatMap {
            cachedItems = it.toMutableList()
            Single.just(it)
        }
    }

    private fun getItemsByOwner(isMine: Boolean): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter {
                it.isMine == isMine
            }.toList()
        }

        return dataSourceLocal.getItemsByOwner(isMine)
    }

    private fun getItemsByReturned(isReturned: Boolean): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter {
                it.isReturned == isReturned
            }.toList()
        }

        return dataSourceLocal.getItemsByReturned(isReturned)
    }

    fun getPersonByUri(personUri: String): Single<Person> {
        val uri = Uri.parse(personUri)

        if (selectedItem?.contactUri.equals(uri.path) && selectedPerson != null)
            return Single.just(selectedPerson)

        return Single.fromCallable {
            val name: String
            val photo: Bitmap
            val photoUri: String

            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            uri.also { contactUri ->
                context.contentResolver.query(contactUri, projection, null, null, null).apply {
                    moveToFirst()

                    //val numberCol = getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    //itemDetailPersonPhoneEdit.setText(getString(numberCol))

                    val nameCol = getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    name = getString(nameCol)

                    val photoCol = getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
                    photoUri = getString(photoCol)

                    val afd = context.contentResolver.openAssetFileDescriptor(Uri.parse(photoUri), "r")
                    photo = afd?.fileDescriptor.let {
                        BitmapFactory.decodeFileDescriptor(it, null, null)
                    }

                    close()
                }
            }

            Person(name, "", "", photo)
        }
    }



    fun getFilterPreference(): Int {
        return prefs.getInt(Constants.PREFS_FILTER, -1)
    }

    fun saveFilterPreference(filter: Int) {
        prefs.edit()
                .putInt(Constants.PREFS_FILTER, filter)
                .apply()
    }
}