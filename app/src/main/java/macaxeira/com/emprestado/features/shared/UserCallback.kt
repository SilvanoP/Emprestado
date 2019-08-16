package macaxeira.com.emprestado.features.shared

import macaxeira.com.emprestado.data.entities.User

interface UserCallback {
    fun receiveUser(user: User)
}