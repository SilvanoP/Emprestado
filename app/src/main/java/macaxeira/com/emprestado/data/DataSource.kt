package macaxeira.com.emprestado.data

import io.reactivex.Completable
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

interface DataSource {

    fun saveItem(item: Item): Completable
    fun savePerson(person: Person): Completable

    fun removeItem(item: Item): Completable

    fun getAllItems(): Single<List<Item>>
    fun getItemsByFilter(filter: Map<String, String>): Single<List<Item>>
}