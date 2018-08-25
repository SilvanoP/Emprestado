package macaxeira.com.emprestado.data.database

import android.arch.persistence.room.*
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

@Dao
interface DataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePerson(person: Person)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateItem(item: Item)

    @Delete
    fun removePerson(person: Person)
    @Delete
    fun removeItem(item: Item)

    @Query("SELECT * FROM Item")
    fun loadAllItems(): Single<List<Item>>
    @Query("SELECT * FROM Person")
    fun loadAllPeople(): Single<List<Person>>
    @Query("SELECT * FROM Item WHERE is_mine = :isMine")
    fun loadItemsByMine(isMine: Boolean): Single<List<Item>>
}