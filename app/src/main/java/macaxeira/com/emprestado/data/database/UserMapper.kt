package macaxeira.com.emprestado.data.database

import com.google.firebase.firestore.DocumentSnapshot
import macaxeira.com.emprestado.data.entities.User

object UserMapper {

    @JvmStatic
    private fun fromMapToUser(map: Map<String, Any>?, id: String): User {
        return User(id, map!!["userName"] as String, map["password"] as String, map["email"] as String,
                map["isGoogleAccount"] as Boolean)
    }

    @JvmStatic
    fun fromDocumentSnapshotToUSer(document: DocumentSnapshot): User {
        return fromMapToUser(document.data, document.id)
    }
}