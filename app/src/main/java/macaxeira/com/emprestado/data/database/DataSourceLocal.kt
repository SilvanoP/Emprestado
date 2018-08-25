package macaxeira.com.emprestado.data.database

import io.reactivex.Completable
import io.reactivex.Single
import macaxeira.com.emprestado.data.DataSource
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

class DataSourceLocal(val database: EmprestadoDatabase) : DataSource {


    override fun saveItem(item: Item): Completable {
        return Completable.fromAction {
            database.dataDAO().insertOrUpdateItem(item)
        }
    }

    override fun savePerson(person: Person): Completable {
        return Completable.fromAction {
            database.dataDAO().insertOrUpdatePerson(person)
        }
    }

    override fun removeItem(item: Item): Completable {
        return Completable.fromAction {
            database.dataDAO().removeItem(item)
        }
    }

    override fun getAllItems(): Single<List<Item>> {
        return database.dataDAO().loadAllItems()
    }

    override fun getItemsByFilter(filter: Boolean): Single<List<Item>> {
        return database.dataDAO().loadItemsByMine(filter)
    }

}