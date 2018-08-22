package macaxeira.com.emprestado.data.entities

import macaxeira.com.emprestado.R

enum class ItemType(val res: Int) {
    BOOK(R.string.book),
    CD(R.string.cd),
    DVD(R.string.dvd),
    CLOTHE(R.string.clothe),
    MONEY(R.string.money),
    OTHER(R.string.other)
}