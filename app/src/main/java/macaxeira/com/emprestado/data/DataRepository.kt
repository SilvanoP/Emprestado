package macaxeira.com.emprestado.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.database.ItemMapper
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.User
import macaxeira.com.emprestado.features.listitem.ListItemCallback
import macaxeira.com.emprestado.utils.Constants

class DataRepository(private val context: Context, private val database: FirebaseFirestore, private val prefs: SharedPreferences) {

    private var source = Source.CACHE
    private var selectedItem: Item? = null
    private var user: User? = null

    fun setUser(user: User?) {
        this.user = user
    }

    // SHARED PREFERENCE METHODS

    fun getFilterPreference(): Int {
        return prefs.getInt(Constants.PREFS_FILTER, -1)
    }

    fun saveFilterPreference(filter: Int) {
        prefs.edit()
                .putInt(Constants.PREFS_FILTER, filter)
                .apply()
    }

    // LIST ITEM

    fun onRefreshCachedItems() {
        source = Source.SERVER
    }

    private fun onCachedUpdated() {
        if (source == Source.SERVER)
            source = Source.CACHE
    }

    fun onItemSelected(item: Item) {
        selectedItem = item
    }

    fun onRefreshSelectedItem() {
        selectedItem = null
    }

    fun getItemsByFilter(filter: Int, callback: ListItemCallback) {
        when (filter) {
            R.id.dialogFilterButtonBorrowed ->
                getItemsByOwner(callback,false)
            R.id.dialogFilterButtonLent ->
                getItemsByOwner(callback,true)
            R.id.dialogFilterButtonReturned ->
                getItemsByReturned(callback)
            R.id.dialogFilterButtonAll ->
                getAllItemsByUser(callback)
        }
    }

    private fun getAllItemsByUser(callback: ListItemCallback) {
        database.collection(Constants.Database.COLLECTION_ITEM)
                .whereEqualTo("userId", user?.id)
                .orderBy("createdDate")
                .get(source)
                .addOnSuccessListener {
                    onCachedUpdated()
                    val items = ItemMapper.fromDocumentsToItems(it)
                    callback.returnItems(items)
                }
                .addOnFailureListener {
                    callback.error(it)
                }
    }

    private fun getItemsByOwner(callback: ListItemCallback, isMine: Boolean) {
        database.collection(Constants.Database.COLLECTION_ITEM)
                .whereEqualTo("userId", user?.id)
                .whereEqualTo("isMine", isMine)
                .get(source)
                .addOnSuccessListener {
                    onCachedUpdated()
                    val items = ItemMapper.fromDocumentsToItems(it)
                    callback.returnItems(items)
                }
                .addOnFailureListener {
                    callback.error(it)
                }
    }

    private fun getItemsByReturned(callback: ListItemCallback) {
        database.collection(Constants.Database.COLLECTION_ITEM)
                .whereEqualTo("userId", user?.id)
                .whereEqualTo("isReturned", true)
                .get(source)
                .addOnSuccessListener {
                    onCachedUpdated()
                    val items = ItemMapper.fromDocumentsToItems(it)
                    callback.returnItems(items)
                }
                .addOnFailureListener {
                    callback.error(it)
                }
    }

    fun removeItem(item: Item, callback: ListItemCallback) {
        database.collection(Constants.Database.COLLECTION_ITEM)
                .document(item.id)
                .delete()
                .addOnFailureListener {
            callback.error(it)
        }
    }

    fun removeItems(items: List<Item>, callback: ListItemCallback) {
        val batch = database.batch()
        for (item in items) {
            val document = database.collection(Constants.Database.COLLECTION_ITEM).document(item.id)
            batch.delete(document)
        }

        batch.commit()
                .addOnSuccessListener {
                    callback.returnItems(items)
                }
                .addOnFailureListener {
                    callback.error(it)
                }
    }

    fun restoreItem(item: Item, callback: ListItemCallback) {
        val data = ItemMapper.fromItemToDocument(item)
        database.collection(Constants.Database.COLLECTION_ITEM)
                .document(item.id)
                .set(data)
                .addOnFailureListener {
                    callback.error(it)
                }
    }

    fun onItemsReturned(items: List<Item>, callback: ListItemCallback) {
        val batch = database.batch()
        for (item in items) {
            val document = database.collection(Constants.Database.COLLECTION_ITEM).document(item.id)
            batch.update(document, "isReturned", true)
        }

        batch.commit()
                .addOnSuccessListener {
                    callback.returnItems(items)
                }
                .addOnFailureListener {
                    callback.error(it)
                }
    }
/*    fun saveItem(item: Item): Single<Long> {
        if (item.createdDate.isEmpty()) {
            item.createdDate = Utils.fromCalendarToString(Calendar.getInstance())
        }
        return dataSourceLocal.saveItem(item).doOnSuccess {
            updateCachedItems(listOf(item))
        }
    }

    fun saveSelectedItem(): Single<Long> {
        if (selectedItem == null)
            return Single.error(UnsupportedOperationException("No item selected!"))

        return dataSourceLocal.saveItem(selectedItem!!).doOnSuccess {
            updateCachedItems(listOf(selectedItem!!))
        }
    }

    fun updateItem(item: Item): Completable {
        return dataSourceLocal.updateItems(listOf(item)).doFinally {
            selectedItem = item
            //updateCachedItems(listOf(item))
            onRefreshCachedItems()
        }
    }

    fun updateItems(items: List<Item>): Completable {
        return dataSourceLocal.updateItems(items).doOnComplete {
            updateCachedItems(items)
        }
    }

    private fun updateCachedItems(items: List<Item>) {
        if (cachedItems.isNotEmpty()) {
            for (i in items) {
                val index = cachedItems.indexOf(i)
                if (index != -1) {
                    cachedItems.removeAt(index)
                    cachedItems.add(index, i)
                } else {
                    cachedItems.add(i)
                }
            }
        }
    }

    fun removeItem(item: Item): Completable {
        return dataSourceLocal.removeItem(item).doOnComplete {
            cachedItems.remove(item)
        }
    }*/

    /*fun getSelectedItem(): Item? {
        if (selectedItem == null)
            selectedItem = Item()
        return selectedItem
    }

    fun getItemById(id: Int): Single<Item> {
        return dataSourceLocal.getItemById(id)
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
    }*/

    /*// Item Detail

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
    }*/
}