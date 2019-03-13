package macaxeira.com.emprestado.di

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.DataSource
import macaxeira.com.emprestado.data.database.DataSourceLocal
import macaxeira.com.emprestado.data.database.EmprestadoDatabase
import macaxeira.com.emprestado.features.itemdetail.ItemDetailContract
import macaxeira.com.emprestado.features.itemdetail.ItemDetailPresenter
import macaxeira.com.emprestado.features.listitem.ListItemContract
import macaxeira.com.emprestado.features.listitem.ListItemPresenter
import macaxeira.com.emprestado.utils.Constants
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

val dbName = "emprestado_db"

val EmprestadoModule = applicationContext {

    factory { ListItemPresenter(get()) as ListItemContract.Presenter }
    factory { ItemDetailPresenter(get()) as ItemDetailContract.Presenter }
    bean { androidApplication().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE) as SharedPreferences}
}

val RepositoryModule = applicationContext {

    bean { DataRepository(androidApplication(), get(), get()) }
    bean { Room.databaseBuilder(androidApplication(), EmprestadoDatabase::class.java, dbName).build() }
    bean("dataSourceLocal") { DataSourceLocal(get()) as DataSource}
}

val emprestadoModules = listOf(RepositoryModule, EmprestadoModule)