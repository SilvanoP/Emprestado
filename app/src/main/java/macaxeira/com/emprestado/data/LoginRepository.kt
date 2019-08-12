package macaxeira.com.emprestado.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import macaxeira.com.emprestado.data.entities.User
import macaxeira.com.emprestado.utils.Utils

class LoginRepository(private val context: Context,
                      private val firebaseAuth: FirebaseAuth,
                      private val dataSourceLocal: DataSource,
                      private val prefs: SharedPreferences) {

    fun saveUser(username: String, password:String, email:String, isGoogleAccount: Boolean): Single<Long> {
        val user = User(null, username, password, email, isGoogleAccount)
        return dataSourceLocal.saveUser(user)
    }

    fun updateUser(user:User, email:String): Single<Long> {
        user.email = email
        return dataSourceLocal.saveUser(user)
    }

    fun removeUser(user: User): Completable {
        return dataSourceLocal.removeUser(user)
    }

    fun updateItemsToUser(user: User): Completable {
        return dataSourceLocal.getItemsWithoutUser()
                .toObservable()
                .flatMap {
                    Observable.fromIterable(it)
                }.flatMap {
                    it.userId = user.id
                    Observable.just(it)
                }.toList()
                .flatMapCompletable {
                    dataSourceLocal.updateItems(it)
                }
    }

    fun isLoggedIn(): Single<User> {
        if (Utils.isOnline(context)) {
            TODO("Implement firebase")
        }

        return Single.just(null)
    }

    fun loginWithUsernameAndPassword(username: String, password: String): Maybe<User> {
        if (Utils.isOnline(context)) {
            TODO("Implement firebase")
            /*firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener {
                if (it.isSuccessful) {

                }
            }*/
        }

        return dataSourceLocal.verifyLogin(username, password)
    }

    fun loginWithGoogle(): Single<User> {
        if (Utils.isOnline(context)) {
            TODO("Implement firebase")
        }

        return Single.error(UnsupportedOperationException("Not supported while offline"))
    }
}