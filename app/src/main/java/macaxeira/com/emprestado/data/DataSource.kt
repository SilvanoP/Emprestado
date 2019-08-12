package macaxeira.com.emprestado.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.data.entities.User

interface DataSource {

    fun saveUser(user: User): Single<Long>
    fun removeUser(user: User): Completable
    fun verifyLogin(username:String, password:String): Maybe<User>

    fun saveItem(item: Item): Single<Long>
    fun updateItems(items: List<Item>): Completable

    fun removeItem(item: Item): Completable
    fun removeItems(items: List<Item>): Completable

    fun getItemById(id: Int): Single<Item>
    fun getAllItems(): Single<List<Item>>
    fun getItemsWithoutUser(): Single<List<Item>>
    fun getAllItemsByUser(user: User): Single<List<Item>>
    fun getItemsByOwner(isMine: Boolean): Single<List<Item>>
    fun getItemsByReturned(isReturned: Boolean): Single<List<Item>>
}