package macaxeira.com.emprestado.data.entities

import android.os.Parcel
import android.os.Parcelable

class Person() : Parcelable {

    var id: Long = -1
    var name: String = ""
    var telephone: String = ""
    var email: String = ""

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        telephone = parcel.readString()
        email = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(id)
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