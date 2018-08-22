package macaxeira.com.emprestado.data.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(foreignKeys = [ForeignKey(entity = Person::class, parentColumns = arrayOf("id"),
        childColumns = arrayOf("person_id"), onDelete = ForeignKey.CASCADE)])
class Item() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    @ColumnInfo(name = "item_type")
    var itemType: ItemType? = null
    var description: String = ""
    var quantity: Int = 0
    @ColumnInfo(name = "is_mine")
    var isMine: Boolean = true
    @ColumnInfo(name = "created_date")
    var createdDate: String = ""
    @ColumnInfo(name = "return_date")
    var returnDate: String = ""
    @ColumnInfo(name = "person_id")
    var personId: Long? = null

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this() {
        val tempId = parcel.readLong()
        id = if(tempId == -1L) null else tempId
        itemType = ItemType.valueOf(parcel.readString())
        description = parcel.readString()
        quantity = parcel.readInt()
        isMine = parcel.readInt() != 0
        createdDate = parcel.readString()
        returnDate = parcel.readString()
        val tempPersonId = parcel.readLong()
        personId = if(tempPersonId == -1L) null else tempPersonId
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(id ?: -1)
        dest?.writeString(itemType?.name)
        dest?.writeString(description)
        dest?.writeInt(quantity)
        dest?.writeInt(if (isMine) 1 else 0)
        dest?.writeString(createdDate)
        dest?.writeString(returnDate)
        dest?.writeLong(personId ?: -1)
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(source: Parcel): Item {
            return Item(source)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}