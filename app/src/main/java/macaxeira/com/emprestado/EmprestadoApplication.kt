package macaxeira.com.emprestado

import android.app.Application
import macaxeira.com.emprestado.di.emprestadoModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EmprestadoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@EmprestadoApplication)
            modules(emprestadoModules)
        }
    }
}