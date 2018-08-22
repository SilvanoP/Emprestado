package macaxeira.com.emprestado.features.itemdetail

import macaxeira.com.emprestado.features.shared.BasePresenter
import macaxeira.com.emprestado.features.shared.BaseView

interface ItemDetailContract {

    interface View : BaseView {
        fun onSaveOrUpdateComplete()
    }

    interface Presenter: BasePresenter<View> {
        fun saveItem()
    }
}