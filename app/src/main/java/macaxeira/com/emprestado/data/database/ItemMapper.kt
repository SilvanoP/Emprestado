package macaxeira.com.emprestado.data.database

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import macaxeira.com.emprestado.data.entities.Item

object ItemMapper {

    @JvmStatic
    fun fromDocumentToItem(document: QueryDocumentSnapshot): Item {
        val map = document.data
        return Item(document.id, map["description"] as String, map["quantity"] as Int,
                map["isMine"] as Boolean, map["createdDate"] as String, map["returnDate"] as String,
                map["isReturned"] as Boolean, map["contactUri"] as String, map["remember"] as Boolean,
                map["userId"] as String, map["personName"] as String, map["photoUri"] as String)
    }

    @JvmStatic
    fun fromDocumentsToItems(documents: QuerySnapshot): MutableList<Item> {
        val items = mutableListOf<Item>()
        for (document in documents) {
            val item = fromDocumentToItem(document)
            items.add(item)
        }

        return items
    }

    @JvmStatic
    fun fromItemToDocument(item: Item): HashMap<String, Any?> {
        val documentData = hashMapOf<String, Any?>(
                "description" to item.description,
                "quantity" to item.quantity,
                "isMine" to item.isMine,
                "createdDate" to item.createdDate,
                "returnDate" to item.returnDate,
                "isReturned" to item.isReturned,
                "contactUri" to item.contactUri,
                "remember" to item.remember,
                "userId" to item.userId,
                "personName" to item.personName,
                "photoUri" to item.photoUri
        )

        return documentData
    }
}