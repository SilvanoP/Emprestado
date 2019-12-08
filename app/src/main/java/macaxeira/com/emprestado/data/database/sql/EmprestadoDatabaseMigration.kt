package macaxeira.com.emprestado.data.database.sql

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // drop table user
        database.execSQL("DROP TABLE `USER`")
        // remove column userID from Item
        removeColumnUserIdFromItem(database)
    }

    private fun removeColumnUserIdFromItem(database: SupportSQLiteDatabase) {
        database.execSQL(createSqlForNewTableWithoutColumnUserId())
        database.execSQL(createSqlToTransferDataFromOldItemToItemNew())
        database.execSQL("DROP TABLE Item")
        database.execSQL("ALTER  TABLE Item_New RENAME TO Item")
    }

    private fun createSqlForNewTableWithoutColumnUserId() : String = """
            CREATE TABLE Item_New (
                        id INTEGER PRIMARY KEY NOT NULL,
                        description TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        isMine INTEGER NOT NULL,
                        createdDate TEXT NOT NULL,
                        returnDate TEXT NOT NULL,
                        isReturned INTEGER NOT NULL,
                        contactUri TEXT NOT NULL,
                        remember INTEGER NOT NULL,
                        personName TEXT NOT NULL,
                        photoUri TEXT NOT NULL,
                        )
        """.trimIndent()

    private fun createSqlToTransferDataFromOldItemToItemNew(): String = """
        INSERT INTO Item_New (id,description,quantity,isMine,createdDate,returnDate,isReturned,contactUri,remember,personName,photoUri)
        SELECT id,description,quantity,isMine,createdDate,returnDate,isReturned,contactUri,remember,personName,photoUri FROM Item
    """.trimIndent()
}

val emprestadoMigrations = arrayOf(MIGRATION_1_2)