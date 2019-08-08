package macaxeira.com.emprestado.features.listitem

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import macaxeira.com.emprestado.data.DataRepository
import macaxeira.com.emprestado.data.entities.Item
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ListItemPresenterTest {

    @Mock
    private lateinit var view: ListItemContract.View
    @Mock
    private lateinit var repository: DataRepository
    private lateinit var presenter: ListItemPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            scheduler  -> Schedulers.trampoline()
        }
        presenter = ListItemPresenter(repository)
    }

    @Test
    fun loadData() {
        val filter = 0
        `when`(repository.getFilterPreference()).then { filter }
        `when`(repository.getItemsByFilter(filter)).then { listOf<Item>()}

        presenter.loadData(false)

        verify(view).showEmptyList()
    }

    @Test
    fun onAddItem() {
    }

    @Test
    fun loadItemsByFilter() {
    }

    @Test
    fun onSwipeRefresh() {
    }

    @Test
    fun onItemsToRemove() {
    }

    @Test
    fun onItemsReturned() {
    }

    @Test
    fun onItemSelected() {
    }

    @Test
    fun removeItem() {
    }

    @Test
    fun restoreItem() {
    }

    @Test
    fun saveFilterPreference() {
    }
}