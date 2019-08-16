package macaxeira.com.emprestado.data.database

import com.google.firebase.auth.FirebaseUser
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

    @JvmStatic
    fun fromUserToData(user: User): HashMap<String, Any?> {
        return hashMapOf<String, Any?>(
                "userName" to user.userName,
                "password" to user.password,
                "email" to user.email,
                "isGoogleAccount" to user.isGoogleAccount
        )
    }

    @JvmStatic
    fun fromFirebaseUserToUser(fireUser: FirebaseUser): User {
        var isGoogleAccount = false
        fireUser.let {
            for (profile in it.providerData) {
                val providerId = profile.providerId
                if (profile.providerId.contains("google")) {
                    isGoogleAccount = true
                    break
                }
            }
        }
        return User(fireUser.uid, fireUser.displayName!!, fireUser.email!!, fireUser.email!!, isGoogleAccount)
    }
}