package macaxeira.com.emprestado.features.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

abstract class BasePresenterImpl<V: BaseView> :BasePresenter<V>, CoroutineScope {

    protected lateinit var view: WeakReference<V>
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    protected val jobs = ArrayList<Job>()
    protected infix fun ArrayList<Job>.add(job: Job) { this.add(job) }

    override fun setView(view: V) {
        this.view = WeakReference(view)
    }

    override fun onDispose() {
        jobs.forEach { if(!it.isCancelled) it.cancel() }
    }
}