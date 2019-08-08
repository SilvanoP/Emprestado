package macaxeira.com.emprestado.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class User(
        @PrimaryKey(autoGenerate = true)
        var id: Long,
        var login: String,
        var password: String,
        var email: String,
        var isGoogleAccount: Boolean) : Parcelable