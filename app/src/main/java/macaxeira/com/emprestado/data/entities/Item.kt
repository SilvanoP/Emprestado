package macaxeira.com.emprestado.data.entities

import android.os.Parcel
import android.os.Parcelable

class Item() : Parcelable {

    var id: Long = -1
    var itemType: ItemType? = null
    var description: String = ""
    var quantity: Int = 0
    var isMine: Boolean = true
    var createdDate: String = ""
    var returnDate: String = ""

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        itemType = ItemType.valueOf(parcel.readString())
        description = parcel.readString()
        quantity = parcel.readInt()
        isMine = parcel.readInt() != 0
        createdDate = parcel.readString()
        returnDate = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(id)
        dest?.writeString(itemType?.name)
        dest?.writeString(description)
        dest?.writeInt(quantity)
        dest?.writeInt(if (isMine) 1 else 0)
        dest?.writeString(createdDate)
        dest?.writeString(returnDate)
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