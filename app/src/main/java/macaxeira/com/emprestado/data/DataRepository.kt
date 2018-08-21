package macaxeira.com.emprestado.data

import io.reactivex.Completable
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

class DataRepository(val dataSourceLocal: DataSource) : DataSource {

    override fun saveItem(item: Item): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun savePerson(person: Person): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeItem(item: Item): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllItems(): Single<List<Item>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemsByFilter(filter: Map<String, String>): Single<List<Item>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}