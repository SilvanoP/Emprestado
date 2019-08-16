package macaxeira.com.emprestado.features.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import macaxeira.com.emprestado.R

class LoginActivity : AppCompatActivity(), LoginContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun loginAccepter() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loginDenied() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showErrorMessage(throwable: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
