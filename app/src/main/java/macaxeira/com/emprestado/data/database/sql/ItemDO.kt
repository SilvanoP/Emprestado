package macaxeira.com.emprestado.data.database.sql

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Item")
class ItemDO(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var description: String,
        var quantity: Int,
        @ColumnInfo(name = "is_mine")
        var isMine: Boolean,
        @ColumnInfo(name = "created_date")
        var createdDate: String,
        @ColumnInfo(name = "return_date")
        var returnDate: String,
        @ColumnInfo(name = "is_returned")
        var isReturned: Boolean,
        @ColumnInfo(name = "contact_uri")
        var contactUri: String,
        @ColumnInfo(name = "remember")
        var remember: Boolean,
        @ColumnInfo(name = "name")
        var personName: String,
        @ColumnInfo(name = "photo_uri")
        val photoUri: String
)