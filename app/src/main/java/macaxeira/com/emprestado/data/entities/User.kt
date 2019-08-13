package macaxeira.com.emprestado.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        var id: String,
        var userName: String,
        var password: String,
        var email: String,
        var isGoogleAccount: Boolean) : Parcelable