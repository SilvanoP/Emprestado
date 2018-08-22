package macaxeira.com.emprestado.data

import io.reactivex.Completable
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

class DataRepository(private val dataSourceLocal: DataSource) : DataSource {

    private var cachedItems: MutableList<Item> = mutableListOf()

    override fun saveItem(item: Item): Completable {
        return dataSourceLocal.saveItem(item).doOnComplete {
            cachedItems.add(item)
        }
    }

    override fun savePerson(person: Person): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeItem(item: Item): Completable {
        return dataSourceLocal.removeItem(item).doOnComplete{
            cachedItems.remove(item)
        }
    }

    override fun getAllItems(): Single<List<Item>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemsByFilter(filter: Map<String, String>): Single<List<Item>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}