package macaxeira.com.emprestado.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person

@Database(entities = [Item::class, Person::class], version = 1)
abstract class EmprestadoDatabase : RoomDatabase() {
    abstract fun dataDAO() : DataDAO
}