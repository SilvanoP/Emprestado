package macaxeira.com.emprestado.data.database.sql

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ItemDO::class], version = 2, exportSchema = false)
abstract class EmprestadoDatabase: RoomDatabase() {
    abstract fun dataDAO(): DataDAO
}