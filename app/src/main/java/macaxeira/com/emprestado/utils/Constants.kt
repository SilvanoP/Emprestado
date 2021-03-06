package macaxeira.com.emprestado.utils

object Constants {
    const val ACTION_RETURNED ="android.intent.action.ACTION_RETURNED"
    const val ITEM_ID = "ITEM_ID"
    const val NOTIFICATION = "NOTIFICATION"
    const val NOTIFICATION_CHANNEL_ID = "RETURN_DATE_NOTIFICATION"
    const val NOTIFICATION_ID = "NOTIFICATION_ID"
    const val NOTIFICATION_ITEM_RETURNED ="ACTION_RETURNED"

    const val PREFS_FILTER = "FILTER_PREFERENCE"
    const val PREFS_NAME = "macaxeira.com.emprestado.Preference"
    const val PREFS_USER_ID = "USER_ID_PREFERENCE"

    object Database {
        const val COLLECTION_ITEM = "items"
        const val COLLECTION_USER = "users"
    }
}