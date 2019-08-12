package macaxeira.com.emprestado.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.DataSource
import macaxeira.com.emprestado.data.LoginRepository
import macaxeira.com.emprestado.data.database.DataSourceLocal
import macaxeira.com.emprestado.data.database.EmprestadoDatabase
import macaxeira.com.emprestado.data.database.emprestadoMigrations
import macaxeira.com.emprestado.features.itemdetail.ItemDetailActivity
import macaxeira.com.emprestado.features.itemdetail.ItemDetailContract
import macaxeira.com.emprestado.features.itemdetail.ItemDetailPresenter
import macaxeira.com.emprestado.features.listitem.ListItemContract
import macaxeira.com.emprestado.features.listitem.ListItemPresenter
import macaxeira.com.emprestado.features.listitem.MainActivity
import macaxeira.com.emprestado.utils.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dbName = "emprestado_db"

val EmprestadoModule = module {
    scope(named<MainActivity>()) {
        scoped { ListItemPresenter(get()) as ListItemContract.Presenter }
    }
    factory<ListItemContract.Presenter> { ListItemPresenter(get()) }
    scope(named<ItemDetailActivity>()) {
        scoped { ItemDetailPresenter(get()) as ItemDetailContract.Presenter }
    }
}

val FirebaseModule = module {
    single { FirebaseAuth.getInstance() }
}

val RepositoryModule = module {
    single { LoginRepository(androidContext(), get(), get(), get())}
    single { DataRepository(androidContext(), get(), get()) }
    single { androidContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE) as SharedPreferences}
    single { Room.databaseBuilder(androidContext(), EmprestadoDatabase::class.java, dbName).addMigrations(*emprestadoMigrations).build() }
    single { DataSourceLocal(get()) as DataSource}
}

val emprestadoModules = listOf(FirebaseModule, RepositoryModule, EmprestadoModule)