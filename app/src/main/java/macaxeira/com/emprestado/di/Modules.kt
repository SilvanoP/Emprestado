package macaxeira.com.emprestado.di

import macaxeira.com.emprestado.data.database.DataSourceLocal
import macaxeira.com.emprestado.features.listitem.ListItemContract
import macaxeira.com.emprestado.features.listitem.ListItemPresenter
import org.koin.dsl.module.applicationContext

val EmprestadoModule = applicationContext {

    factory { ListItemPresenter(get()) as ListItemContract.Presenter }
}

val RepositoryModule = applicationContext {

    bean("dataSourceLocal") { DataSourceLocal(get())}
}

val emprestadoModules = listOf(RepositoryModule, EmprestadoModule)