package macaxeira.com.emprestado.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

class DataRepository(private val dataSourceLocal: DataSource) : DataSource {

    private var cachedItems: MutableList<Item> = mutableListOf()
    private var cachedPeople: MutableList<Person> = mutableListOf()

    override fun saveItem(item: Item): Completable {
        return dataSourceLocal.saveItem(item).doOnComplete {
            cachedItems.add(item)
        }
    }

    override fun savePerson(person: Person): Completable {
        return dataSourceLocal.savePerson(person).doOnComplete {
            cachedPeople.add(person)
        }
    }

    override fun removeItem(item: Item): Completable {
        return dataSourceLocal.removeItem(item).doOnComplete{
            cachedItems.remove(item)
        }
    }

    override fun getAllItems(): Single<List<Item>> {
        return dataSourceLocal.getAllItems().doAfterSuccess {
            cachedItems = it.toMutableList()
        }
    }

    override fun getItemsByFilter(filter: Boolean): Single<List<Item>> {
        if (cachedItems.size > 0) {
            return Observable.fromIterable(cachedItems).flatMap {
                Observable.just(it)
            }.filter {
                it -> it.isMine == filter
            }.toList()
        }

        return dataSourceLocal.getItemsByFilter(filter)
    }
}