package macaxeira.com.emprestado.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.DataSource
import macaxeira.com.emprestado.data.database.DataSourceLocal
import macaxeira.com.emprestado.data.database.EmprestadoDatabase
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
        scoped<ListItemContract.Presenter> { ListItemPresenter(get()) }
    }
    scope(named<ItemDetailActivity>()) {
        scoped<ItemDetailContract.Presenter> { ItemDetailPresenter(get()) }
    }
    single { androidContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE) as SharedPreferences}
}

val RepositoryModule = module {
    single { DataRepository(androidContext(), get(), get()) }
    single { Room.databaseBuilder(androidContext(), EmprestadoDatabase::class.java, dbName).build() }
    single(named("dataSourceLocal")) { DataSourceLocal(get()) as DataSource}
}

val emprestadoModules = listOf(RepositoryModule, EmprestadoModule)