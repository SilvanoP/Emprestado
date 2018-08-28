package macaxeira.com.emprestado.data

import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.ItemType
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class DataRepository(private val dataSourceLocal: DataSource, private val prefs: SharedPreferences) : DataSource {

    private var cachedItems: MutableList<Item> = mutableListOf()
    private var cachedPeople: MutableList<Person> = mutableListOf()

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

    override fun saveItem(item: Item): Completable {
        // FIXME Temporary
        return Completable.complete()
        /*return dataSourceLocal.saveItem(item).doOnComplete {
            cachedItems.add(item)
        }*/
    }

    override fun savePerson(person: Person): Completable {
        return dataSourceLocal.savePerson(person).doOnComplete {
            cachedPeople.add(person)
        }
    }

    override fun removeItem(item: Item): Completable {
        // FIXME Temporary
        return Completable.complete()
        /*return dataSourceLocal.removeItem(item).doOnComplete{
            cachedItems.remove(item)
        }*/
    }

    override fun getAllItems(): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Single.just(cachedItems)
        }

        return dataSourceLocal.getAllItems().doAfterSuccess {
            cachedItems = it.toMutableList()
        }
    }

    override fun getItemsByOwner(isMine: Boolean): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter {
                it -> it.isMine == isMine
            }.toList()
        }

        return dataSourceLocal.getItemsByOwner(isMine)
    }

    override fun getItemsByReturned(isReturned: Boolean): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter {
                it -> it.isReturned == isReturned
            }.toList()
        }

        return dataSourceLocal.getItemsByReturned(isReturned)
    }

    fun getFilterPrefence(): Int {
        return prefs.getInt(Constants.PREFS_FILTER, -1)
    }

    fun saveFilterPreference(filter: Int) {
        prefs.edit()
                .putInt(Constants.PREFS_FILTER, filter)
                .apply()
    }
}