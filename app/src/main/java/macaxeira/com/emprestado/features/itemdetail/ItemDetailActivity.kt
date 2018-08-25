package macaxeira.com.emprestado.features.itemdetail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import macaxeira.com.emprestado.R

class ItemDetailActivity : AppCompatActivity(), ItemDetailContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
    }

    override fun onSaveOrUpdateComplete() {

    }

    override fun showErrorMessage(throwable: Throwable) {

    }
}
