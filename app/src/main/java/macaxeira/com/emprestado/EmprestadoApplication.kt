package macaxeira.com.emprestado

import android.app.Application
import macaxeira.com.emprestado.di.emprestadoModules
import org.koin.android.ext.android.startKoin

class EmprestadoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, emprestadoModules)
    }
}