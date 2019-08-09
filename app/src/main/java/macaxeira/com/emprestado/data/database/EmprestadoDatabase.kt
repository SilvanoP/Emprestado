package macaxeira.com.emprestado.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.data.entities.User

@Database(entities = [Item::class, User::class], version = 1, exportSchema = false)
abstract class EmprestadoDatabase : RoomDatabase() {
    abstract fun dataDAO() : DataDAO
}