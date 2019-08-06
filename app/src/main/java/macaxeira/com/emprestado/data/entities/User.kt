package macaxeira.com.emprestado.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class User(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val login: String,
        val password: String,
        val isGoogleAccount: Boolean) : Parcelable {
}