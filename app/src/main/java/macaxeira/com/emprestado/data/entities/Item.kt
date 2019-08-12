package macaxeira.com.emprestado.data.entities

import androidx.room.*
import android.os.Parcel
import android.os.Parcelable

@Entity(foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE)]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var description: String = "",
    var quantity: Int = 0,
    @ColumnInfo(name = "is_mine")
    var isMine: Boolean = true,
    @ColumnInfo(name = "created_date")
    var createdDate: String = "",
    @ColumnInfo(name = "return_date")
    var returnDate: String = "",
    @ColumnInfo(name = "is_returned")
    var isReturned: Boolean = false,
    @ColumnInfo(name = "contact_uri")
    var contactUri: String = "",
    @ColumnInfo(name = "remember")
    var remember: Boolean = false,
    @Embedded
    var person: Person? = null,
    @ColumnInfo(name = "user_id")
    var userId: Long? = null
    ) : Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    @Ignore
    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as Long?,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt() != 0,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt() != 0,
            parcel.readString()!!,
            parcel.readInt() != 0,
            parcel.readParcelable(Person::class.java.classLoader),
            parcel.readValue(Long::class.java.classLoader) as Long?
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeValue(id)
        dest?.writeString(description)
        dest?.writeInt(quantity)
        dest?.writeInt(if (isMine) 1 else 0)
        dest?.writeString(createdDate)
        dest?.writeString(returnDate)
        dest?.writeInt(if (isReturned) 1 else 0)
        dest?.writeString(contactUri)
        dest?.writeInt(if (remember) 1 else 0)
        dest?.writeParcelable(person, flags)
        dest?.writeValue(userId)
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