package macaxeira.com.emprestado.data.database

import android.arch.persistence.room.*
import io.reactivex.Observable
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

@Dao
interface DataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePerson(person: Person): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateItem(item: Item): Long
    @Update
    fun updateItems(vararg items: Item)

    @Delete
    fun removePerson(person: Person)
    @Delete
    fun removeItem(item: Item)
    @Delete
    fun removeItems(vararg items: Item)

    @Query("SELECT * FROM Item")
    fun loadAllItems(): Single<List<Item>>
    @Query("SELECT * FROM Person")
    fun loadAllPeople(): Single<List<Person>>
    @Query("SELECT * FROM Item WHERE is_mine = :isMine")
    fun loadItemsByMine(isMine: Boolean): Single<List<Item>>
    @Query("SELECT * FROM Item WHERE is_returned = :isReturned")
    fun loadItemsByReturned(isReturned: Boolean): Single<List<Item>>
    @Query("SELECT * FROM Person WHERE id = :personId")
    fun loadPersonById(personId: Long): Person
}