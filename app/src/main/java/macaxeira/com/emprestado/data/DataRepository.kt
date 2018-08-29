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

    init {
        // FIXME Temporary
        val p = Person()
        p.id = 1
        p.name = "Test"
        p.email = "test@test.com"
        p.telephone = "982828393"

        val i = Item()
        i.description = "Testing description"
        i.isMine = true
        i.itemType = ItemType.BOOK
        i.quantity = 1
        i.personId  = 1
        i.returnDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        cachedItems.add(i)
    }

    fun saveItem(item: Item): Completable {
        return dataSourceLocal.saveItem(item).doOnComplete {
            cachedItems.add(item)
        }
    }

    /*fun savePerson(person: Person): Completable {
        return dataSourceLocal.savePerson(person).doOnComplete {
            cachedPeople.add(person)
        }
    }*/

    fun removeItem(item: Item): Completable {
        return dataSourceLocal.removeItem(item).doOnComplete{
            cachedItems.remove(item)
        }
    }

    fun getAllItems(): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Single.just(cachedItems)
        }

        return dataSourceLocal.getAllItems().doAfterSuccess {
            cachedItems = it.toMutableList()
        }
    }

    fun getItemsByOwner(isMine: Boolean): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter {
                it -> it.isMine == isMine
            }.toList()
        }

        return dataSourceLocal.getItemsByOwner(isMine)
    }

    fun getItemsByReturned(isReturned: Boolean): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter {
                it -> it.isReturned == isReturned
            }.toList()
        }

        return dataSourceLocal.getItemsByReturned(isReturned)
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
        when(filter) {
            R.id.dialogFilterButtonBorrowed ->  {
                return getItemsByOwner(false)
            }
            R.id.dialogFilterButtonLent -> {
                return getItemsByOwner(true)
            }
            R.id.dialogFilterButtonReturned -> {
                return getItemsByReturned(true)
            }
        }

        return getAllItems()
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