package macaxeira.com.emprestado.di

import android.arch.persistence.room.Room
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.DataSource
import macaxeira.com.emprestado.data.database.DataSourceLocal
import macaxeira.com.emprestado.data.database.EmprestadoDatabase
import macaxeira.com.emprestado.features.listitem.ListItemContract
import macaxeira.com.emprestado.features.listitem.ListItemPresenter
import org.koin.dsl.module.applicationContext

val dbName = "emprestado_db"

val EmprestadoModule = applicationContext {

    factory { ListItemPresenter(get()) as ListItemContract.Presenter }
}

val RepositoryModule = applicationContext {

    bean { DataRepository(get()) }
    bean { Room.databaseBuilder(get(), EmprestadoDatabase::class.java, dbName).build() }
    bean("dataSourceLocal") { DataSourceLocal(get()) as DataSource}
}

val emprestadoModules = listOf(RepositoryModule, EmprestadoModule)