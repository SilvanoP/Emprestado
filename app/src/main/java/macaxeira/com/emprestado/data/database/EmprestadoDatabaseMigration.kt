package macaxeira.com.emprestado.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

val emprestadoMigrations = arrayOf(MIGRATION_1_2)