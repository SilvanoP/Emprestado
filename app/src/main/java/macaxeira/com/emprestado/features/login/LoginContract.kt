package macaxeira.com.emprestado.features.login

import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView

interface LoginContract {
    interface View: BaseView {
        fun loginAccepter()
        fun loginDenied()
    }

    interface Presenter: BasePresenter<View> {
        fun loadData()
        fun loginWithUsernameAndPassword(username:String, password:String)
        fun loginWithGoogle()
    }
}