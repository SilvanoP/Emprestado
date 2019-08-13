package macaxeira.com.emprestado.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    var id: String = "",
    var description: String = "",
    var quantity: Int = 0,
    var isMine: Boolean = true,
    var createdDate: String = "",
    var returnDate: String = "",
    var isReturned: Boolean = false,
    var contactUri: String = "",
    var remember: Boolean = false,
    var userId: String = "",
    var personName: String = "",
    var photoUri: String = ""
    ) : Parcelable