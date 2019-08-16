package macaxeira.com.emprestado.features.shared

import macaxeira.com.emprestado.data.entities.Item

interface ItemCallback {
    fun receiveItem(item: Item)
}