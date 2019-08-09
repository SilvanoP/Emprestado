package macaxeira.com.emprestado.data.database

import androidx.room.*
import io.reactivex.Observable
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.data.entities.User

@Dao
interface DataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateUser(user: User): Long
    @Update
    fun updateUser(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateItem(item: Item): Long
    @Update
    fun updateItems(vararg items: Item)
    @Delete
    fun removeItem(item: Item)
    @Delete
    fun removeItems(vararg items: Item)
    @Query("SELECT * FROM Item Where id = :id")
    fun loadItemById(id: Int): Single<Item>
    @Query("SELECT * FROM Item")
    fun loadAllItems(): Single<List<Item>>
    @Query("SELECT * FROM Item WHERE is_mine = :isMine")
    fun loadItemsByMine(isMine: Boolean): Single<List<Item>>
    @Query("SELECT * FROM Item WHERE is_returned = :isReturned")
    fun loadItemsByReturned(isReturned: Boolean): Single<List<Item>>
}