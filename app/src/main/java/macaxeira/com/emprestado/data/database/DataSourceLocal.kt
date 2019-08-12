package macaxeira.com.emprestado.data.database

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import macaxeira.com.emprestado.data.DataSource
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.User

class DataSourceLocal(val database: EmprestadoDatabase) : DataSource {

    override fun saveUser(user: User): Single<Long> {
        return Single.fromCallable {
            database.dataDAO().insertOrUpdateUser(user)
        }
    }

    override fun removeUser(user: User): Completable {
        return Completable.fromAction {
            database.dataDAO().removeUser(user)
        }
    }

    override fun verifyLogin(username: String, password: String): Maybe<User> {
        return database.dataDAO().loadUserByUsernameAndPassword(username, password)
    }

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

    override fun getItemsWithoutUser(): Single<List<Item>> {
        return database.dataDAO().loadItemsWithoutUser()
    }

    override fun getAllItemsByUser(user: User): Single<List<Item>> {
        return database.dataDAO().loadAllItemsByUser(user.id!!)
    }

    override fun getItemsByOwner(isMine: Boolean): Single<List<Item>> {
        return database.dataDAO().loadItemsByMine(isMine)
    }

    override fun getItemsByReturned(isReturned: Boolean): Single<List<Item>> {
        return database.dataDAO().loadItemsByReturned(isReturned)
    }

}