package macaxeira.com.emprestado.data

import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleSource
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.ItemType
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class DataRepository(private val dataSourceLocal: DataSource, private val prefs: SharedPreferences) {

    private var cachedItems: MutableList<Item> = mutableListOf()
    private var cachedPeople: MutableList<Person> = mutableListOf()
    private var selectedItem: Item? = null

    fun saveItem(item: Item): Single<Long> {
        return dataSourceLocal.saveItem(item).doOnSuccess {
            cachedItems.add(selectedItem!!)
        }
    }

    fun saveItem(description: String, itemType: ItemType, isMine: Boolean, personName: String,
                 personEmail: String, personPhone: String, returnDate: String): Single<Long> {
        if (selectedItem == null) {
            selectedItem = Item()
        }

        selectedItem!!.description = description
        selectedItem!!.itemType = itemType
        selectedItem!!.isMine = isMine
        selectedItem!!.returnDate = returnDate

        var person = Person()
        if (selectedItem!!.person != null) {
            person = selectedItem!!.person!!
        }
        person.name = personName
        person.phone = personPhone
        person.email = personEmail
        selectedItem!!.person = person

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
            }.filter { it ->
                it.isMine == isMine
            }.toList()
        }

        return dataSourceLocal.getItemsByOwner(isMine)
    }

    private fun getItemsByReturned(isReturned: Boolean): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter { it ->
                it.isReturned == isReturned
            }.toList()
        }

        return dataSourceLocal.getItemsByReturned(isReturned)
    }

    fun getPersonById(personId: Long): Single<Person> {
        return Maybe.fromAction<Person> {
            var p: Person? = null
            for (person in cachedPeople) {
                if (person.id == personId) {
                    p = person
                }
            }

            if (p != null) {
                Single.just(p)
            } else {
                Maybe.empty<Person>()
            }
        }.switchIfEmpty(SingleSource {
            dataSourceLocal.getPersonById(personId)
        })
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