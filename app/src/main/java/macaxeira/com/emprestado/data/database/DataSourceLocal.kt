package macaxeira.com.emprestado.data.database

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import macaxeira.com.emprestado.data.DataSource
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

class DataSourceLocal(val database: EmprestadoDatabase) : DataSource {

    override fun saveItem(item: Item): Single<Long> {
        return Single.fromCallable {
            val personId = database.dataDAO().insertOrUpdatePerson(item.person!!)
            item.personId = personId
            database.dataDAO().insertOrUpdateItem(item)
        }
    }

    override fun savePerson(person: Person): Completable {
        return Completable.fromAction {
            database.dataDAO().insertOrUpdatePerson(person)
        }
    }

    override fun updateItems(items: List<Item>): Completable {
        return Completable.fromAction {
            database.dataDAO().updateItems(*items.toTypedArray())
        }
    }

    override fun removeItem(item: Item): Completable {
        return Completable.fromAction {
            database.dataDAO().removeItem(item)
        }
    }

    override fun removeItems(items: List<Item>): Completable {
        return Completable.fromAction {
            database.dataDAO().removeItems(*items.toTypedArray())
        }
    }

    override fun getAllItems(): Single<List<Item>> {
        return database.dataDAO().loadAllItems()
                .toObservable()
                .flatMapIterable {
                    it
                }
                .flatMap ({
                    val personId = it.personId
                    Observable.just(database.dataDAO().loadPersonById(personId!!))
                },{
                    it, p -> it.person = p
                    it
                }).toList()
    }

    override fun getPersonById(personId: Long): Single<Person> {
        return Single.just(database.dataDAO().loadPersonById(personId))
    }

    override fun getItemsByOwner(isMine: Boolean): Single<List<Item>> {
        return database.dataDAO().loadItemsByMine(isMine)
    }

    override fun getItemsByReturned(isReturned: Boolean): Single<List<Item>> {
        return database.dataDAO().loadItemsByReturned(isReturned)
    }

}