package macaxeira.com.emprestado.data.entities

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

class Person(
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    var photo: Bitmap? = null,
    var photoUri: String = ""
    ) : Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        phone = parcel.readString()
        email = parcel.readString()
        photo = parcel.readParcelable(Bitmap::class.java.classLoader)
        photoUri = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeString(phone)
        dest?.writeString(email)
        dest?.writeString(photoUri)
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