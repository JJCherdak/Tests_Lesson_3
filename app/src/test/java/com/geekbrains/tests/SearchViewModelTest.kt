package com.geekbrains.tests

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.search.SchedulerProviderStub
import com.geekbrains.tests.repository.FakeGitHubRepository
import com.geekbrains.tests.view.search.ScreenState
import com.geekbrains.tests.view.search.SearchViewModel
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SearchViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private lateinit var searchViewModel: SearchViewModel

    @Mock
    private lateinit var repository: FakeGitHubRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchViewModel = SearchViewModel(repository)
    }


    @Test
    fun coroutines_TestReturnValueIsNotNull() {
        testCoroutineRule.runBlockingTest {
            val observer = Observer<ScreenState> {}
            val liveData = searchViewModel.subscribeToLiveData()

            `when`(repository.searchGithubAsync(SEARCH_QUERY)).thenReturn(
                SearchResponse(1, listOf())
            )

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGitHub(SEARCH_QUERY)
                Assert.assertNotNull(liveData.value)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    @Test
    fun liveData_TestReturnValueIsNotNull() {
        //Создаем обсервер. В лямбде мы не вызываем никакие методы - в этом нет необходимости
        //так как мы проверяем работу LiveData и не собираемся ничего делать с данными, которые она возвращает
        val observer = Observer<ScreenState> {}
        //Получаем LiveData
        val liveData = searchViewModel.subscribeToLiveData()

        //При вызове Репозитория возвращаем шаблонные данные
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    1,
                    listOf()
                )
            )
        )

        try {
            //Подписываемся на LiveData без учета жизненного цикла
            liveData.observeForever(observer)
            searchViewModel.searchGitHub(SEARCH_QUERY)
            //Убеждаемся, что Репозиторий вернул данные и LiveData передала их Наблюдателям
            Assert.assertNotNull(liveData.value)
        } finally {
            //Тест закончен, снимаем Наблюдателя
            liveData.removeObserver(observer)
        }
    }

    @Test
    fun coroutines_TestReturnValueIsError() {
        testCoroutineRule.runBlockingTest {
            val observer = Observer<ScreenState> {}
            val liveData = searchViewModel.subscribeToLiveData()

            `when`(repository.searchGithubAsync(SEARCH_QUERY)).thenReturn(
                SearchResponse(null, listOf())
            )

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGitHub(SEARCH_QUERY)

                val value: ScreenState.Error = liveData.value as ScreenState.Error
                Assert.assertEquals(value.error.message, ERROR_TEXT)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    @Test
    fun coroutines_TestException() {
        testCoroutineRule.runBlockingTest {
            val observer = Observer<ScreenState> {}
            val liveData = searchViewModel.subscribeToLiveData()

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGitHub(SEARCH_QUERY)

                val value: ScreenState.Error = liveData.value as ScreenState.Error
                Assert.assertEquals(value.error.message, EXCEPTION_TEXT)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    companion object {
        private const val SEARCH_QUERY = "some query"
        private const val ERROR_TEXT = "error"
        private const val EXCEPTION_TEXT = "Response is null or unsuccessful"
    }
}