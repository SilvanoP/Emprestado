package macaxeira.com.emprestado.features.listitem

import macaxeira.com.emprestado.data.entities.Item

interface ListItemCallback {
    fun returnItems(items: List<Item>)
    fun error(error: Throwable)
}