package macaxeira.com.emprestado.features.shared

import io.reactivex.disposables.CompositeDisposable
import java.lang.ref.WeakReference

abstract class BasePresenterImpl<V: BaseView> :BasePresenter<V> {

    protected var disposable = CompositeDisposable()
    protected lateinit var view: WeakReference<V>

    override fun setView(view: V) {
        this.view = WeakReference(view)
    }

    override fun onDispose() {
        disposable.dispose()
    }
}