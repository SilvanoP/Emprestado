package macaxeira.com.emprestado.data.database

import android.arch.persistence.room.TypeConverter
import macaxeira.com.emprestado.data.entities.ItemType

object RoomConverters {

        @TypeConverter
        @JvmStatic
        fun enumToString(itemType: ItemType?) : String? {
            return itemType?.name
        }

        @TypeConverter
        @JvmStatic
        fun stringToEnum(itemType: String?) : ItemType? {
            return itemType?.let {
                ItemType.valueOf(it)
            }
        }
}