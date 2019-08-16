package macaxeira.com.emprestado.data

import android.content.Context
import android.content.SharedPreferences
import android.provider.SyncStateContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import macaxeira.com.emprestado.data.database.UserMapper
import macaxeira.com.emprestado.data.entities.User
import macaxeira.com.emprestado.features.shared.UserCallback
import macaxeira.com.emprestado.utils.Constants

class LoginRepository(private val context: Context,
                      private val firebaseAuth: FirebaseAuth,
                      private val database: FirebaseFirestore,
                      private val prefs: SharedPreferences) {

    fun saveUser(username: String, password:String, email:String, isGoogleAccount: Boolean) {
        val user = User("", username, password, email, isGoogleAccount)
        val userData = UserMapper.fromUserToData(user)
        database.collection(Constants.Database.COLLECTION_USER).add(userData)
                .addOnSuccessListener {
                    user.id = it.id
                }
    }

    fun updateUser(user:User, email:String) {
        user.email = email

    }

    fun removeUser(user: User) {

    }

    fun updateItemsToUser(user: User) {

    }

    fun isLoggedIn(): User? {
        val user = firebaseAuth.currentUser
        user ?: return null
        return UserMapper.fromFirebaseUserToUser(user)
    }

    fun loginWithUsernameAndPassword(username: String, password: String) {

    }

    fun loginWithGoogle() {

    }
}