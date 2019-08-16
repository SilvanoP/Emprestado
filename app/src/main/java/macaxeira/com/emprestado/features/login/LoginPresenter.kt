package macaxeira.com.emprestado.features.login

import macaxeira.com.emprestado.data.LoginRepository
import macaxeira.com.emprestado.features.shared.BasePresenterImpl

class LoginPresenter(repository: LoginRepository): BasePresenterImpl<LoginContract.View>(),
        LoginContract.Presenter {

    override fun loadData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loginWithUsernameAndPassword(username: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loginWithGoogle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}