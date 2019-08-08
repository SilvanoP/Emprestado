package macaxeira.com.emprestado.data.entities

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlinx.android.parcel.Parcelize

data class Person(
    var name: String,
    @ColumnInfo(name = "photo_uri")
    var photoUri: String
    ) : Parcelable {

    @Ignore
    constructor(parcel: Parcel): this(parcel.readString()!!,
            parcel.readString()!!)

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeString(photoUri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<Person> {
        override fun createFromParcel(source: Parcel): Person {
            return Person(source)
        }

        override fun newArray(size: Int): Array<Person?> {
            return arrayOfNulls(size)
        }
    }
}