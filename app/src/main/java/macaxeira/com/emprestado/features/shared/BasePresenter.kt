package macaxeira.com.emprestado.features.shared

interface BasePresenter<V : BaseView> {

    fun setView(view: V)
}