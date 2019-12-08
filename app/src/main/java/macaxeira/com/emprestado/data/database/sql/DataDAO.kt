package macaxeira.com.emprestado.data.database.sql

import androidx.room.*

@Dao
interface DataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItem(item: ItemDO): Long
    @Update
    suspend fun updateItems(vararg items: ItemDO)
    @Delete
    suspend fun removeItem(item: ItemDO)
    @Delete
    suspend fun removeItems(vararg items: ItemDO)
    @Query("SELECT * FROM Item WHERE id = :id")
    suspend fun loadItemById(id: Int): ItemDO
    @Query("SELECT * FROM Item")
    suspend fun loadAllItems(): List<ItemDO>
    @Query("SELECT * FROM Item WHERE is_mine = :isMine")
    suspend fun loadItemsByMine(isMine: Boolean): List<ItemDO>
    @Query("SELECT * FROM Item WHERE is_returned = :isReturned")
    suspend fun loadItemsByReturned(isReturned: Boolean): List<ItemDO>
}