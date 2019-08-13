package macaxeira.com.emprestado.data

import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.User

interface DataSource {

    fun saveUser(user: User)
    fun removeUser(user: User)
    fun verifyLogin(username:String, password:String)

    fun saveItem(item: Item)
    fun updateItems(items: List<Item>)

    fun removeItem(item: Item)
    fun removeItems(items: List<Item>)

    fun getItemById(id: Int)
    fun getAllItems()
    fun getItemsWithoutUser()
    fun getAllItemsByUser(user: User)
    fun getItemsByOwner(isMine: Boolean)
    fun getItemsByReturned(isReturned: Boolean)
}