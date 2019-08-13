package macaxeira.com.emprestado.features.shared

import java.lang.ref.WeakReference

abstract class BasePresenterImpl<V: BaseView> :BasePresenter<V> {

    protected lateinit var view: WeakReference<V>

    override fun setView(view: V) {
        this.view = WeakReference(view)
    }

    override fun onDispose() {
        // TODO Not needed anymore?
    }
}