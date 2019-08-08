package macaxeira.com.emprestado.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.utils.Constants

class DataRepository(private val context: Context, private val dataSourceLocal: DataSource, private val prefs: SharedPreferences) {

    private var cachedItems: MutableList<Item> = mutableListOf()
    private var selectedItem: Item? = null

    fun saveItem(item: Item): Single<Long> {
        return dataSourceLocal.saveItem(item).doOnSuccess {
            val index = cachedItems.indexOf(selectedItem!!)
            if (index != -1) {
                cachedItems.removeAt(index)
                cachedItems.add(index, selectedItem!!)
            } else {
                cachedItems.add(selectedItem!!)
            }
        }
    }

    fun saveSelectedItem(): Single<Long> {
        if (selectedItem == null)
            return Single.error(UnsupportedOperationException("No item selected!"))

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

    fun updateItem(item: Item): Completable {
        return dataSourceLocal.updateItems(listOf(item))
    }

    fun updateItems(items: List<Item>): Completable {
        return dataSourceLocal.updateItems(items).doOnComplete {
            if (cachedItems.isNotEmpty()) {
                for (item in items) {
                    val index = cachedItems.indexOf(item)
                    cachedItems.removeAt(index)
                    cachedItems.add(index, item)
                }
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

    fun onRefreshCachedItems() {
        cachedItems.clear()
    }

    fun getSelectedItem(): Item? {
        if (selectedItem == null)
            selectedItem = Item()
        return selectedItem
    }

    fun getItemById(id: Int): Single<Item> {
        return dataSourceLocal.getItemById(id)
    }

    fun getItemsByFilter(filter: Int): Single<List<Item>> {
        when (filter) {
            R.id.dialogFilterButtonBorrowed ->
                return getItemsByOwner(false)
            R.id.dialogFilterButtonLent ->
                return getItemsByOwner(true)
            R.id.dialogFilterButtonReturned ->
                return getItemsByReturned()
        }

        return getAllItems()
    }

    private fun getAllItems(): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Single.just(cachedItems)
        }

        return dataSourceLocal.getAllItems()
                .doAfterSuccess {
                    cachedItems = it.toMutableList()
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

    private fun getItemsByReturned(): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter {
                it.isReturned
            }.toList()
        }

        return dataSourceLocal.getItemsByReturned(true)
                .toObservable()
                .flatMap { Observable.fromIterable(it)}
                .flatMap { item ->
                    if (!item.contactUri.isEmpty()) {
                        item.person = queryContactByUri(item.contactUri)
                    }
                    Observable.just(item)
                }
                .toList()
    }

    fun getPersonByUri(personUri: String): Single<Person> {
        if (selectedItem?.contactUri.equals(personUri) && selectedItem?.person != null)
            return Single.just(selectedItem!!.person)

        return Single.fromCallable {
            queryContactByUri(personUri)
        }
    }

    private fun queryContactByUri(personUri: String) : Person? {
        val uri = Uri.parse(personUri)

        val name: String
        var photoUri: String?

        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        uri.also { contactUri ->
            context.contentResolver.query(contactUri, projection, null, null, null)?.apply {
                moveToFirst()

                val nameCol = getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                name = getString(nameCol)

                val photoCol = getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
                photoUri = if (getString(photoCol) == null) "" else getString(photoCol)

                selectedItem?.person = Person(name, photoUri!!)
                close()
            }
        }

        return selectedItem?.person
    }

    fun getFilterPreference(): Int {
        return prefs.getInt(Constants.PREFS_FILTER, -1)
    }

    fun saveFilterPreference(filter: Int) {
        prefs.edit()
                .putInt(Constants.PREFS_FILTER, filter)
                .apply()
    }

    // Item Detail

    fun setIsMine(isMine: Boolean) {
        selectedItem?.isMine = isMine
    }

    fun setDescription(description: String) {
        selectedItem?.description = description
    }

    fun setReturnedDate(returnedDate: String) {
        selectedItem?.returnDate = returnedDate
    }

    fun setReturned(isReturned: Boolean) {
        selectedItem?.isReturned = isReturned
    }

    fun setShouldRemember(shouldRemember: Boolean) {
        selectedItem?.remember = shouldRemember
    }
}