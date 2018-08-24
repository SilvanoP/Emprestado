package macaxeira.com.emprestado.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity
class Person() : Parcelable {

    @PrimaryKey
    var id: Long? = null
    var name: String = ""
    var telephone: String = ""
    var email: String = ""

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this() {
        val tempId = parcel.readLong()
        id = if (tempId == -1L) null else tempId
        name = parcel.readString()
        telephone = parcel.readString()
        email = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(id ?: -1)
        dest?.writeString(name)
        dest?.writeString(telephone)
        dest?.writeString(email)
    }

    companion object CREATOR : Parcelable.Creator<Person> {
        override fun createFromParcel(source: Parcel): Person {
            return Person(source)
        }

        override fun newArray(size: Int): Array<Person?> {
            return arrayOfNulls(size)
        }
    }
}