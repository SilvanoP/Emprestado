package macaxeira.com.emprestado.data.database

import io.reactivex.Completable
import io.reactivex.Single
import macaxeira.com.emprestado.data.DataSource
import macaxeira.com.emprestado.data.entities.Item

class DataSourceLocal(val database: EmprestadoDatabase) : DataSource {

    override fun saveItem(item: Item): Single<Long> {
        return Single.fromCallable {
            database.dataDAO().insertOrUpdateItem(item)
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

    override fun getItemById(id: Int): Single<Item> {
        return database.dataDAO().loadItemById(id)
    }
    override fun getAllItems(): Single<List<Item>> {
        return database.dataDAO().loadAllItems()
    }

    override fun getItemsByOwner(isMine: Boolean): Single<List<Item>> {
        return database.dataDAO().loadItemsByMine(isMine)
    }

    override fun getItemsByReturned(isReturned: Boolean): Single<List<Item>> {
        return database.dataDAO().loadItemsByReturned(isReturned)
    }

}