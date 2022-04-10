package com.geekbrains.tests

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.search.SearchPresenter
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.view.search.ViewSearchContract
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import com.geekbrains.tests.presenter.search.SchedulerProviderStub
import io.reactivex.Observable
import org.junit.Test
import org.mockito.Mockito

class SearchPresenterTestRx {
    private lateinit var presenter: SearchPresenter
    @Mock
    private lateinit var repository: GitHubRepository
    @Mock
    private lateinit var viewContract: ViewSearchContract
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = SearchPresenter(viewContract, repository, SchedulerProviderStub())
    }

    @Test //Проверим вызов метода searchGitHub() у нашего Репозитория
    fun searchGitHub_Test() {
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    1,
                    listOf()
                )
            )
        )

        presenter.searchGitHub(SEARCH_QUERY)
        Mockito.verify(repository, Mockito.times(1)).searchGithub(SEARCH_QUERY)
    }

    @Test //Проверяем как обрабатывается ошибка запроса
    fun handleRequestError_Test() {
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.error(Throwable(ERROR_TEXT))
        )

        presenter.searchGitHub(SEARCH_QUERY)
        Mockito.verify(viewContract, Mockito.times(1)).displayError("error")
    }

    @Test //Проверяем как обрабатываются неполные данные
    fun handleResponseError_TotalCountIsNull() {
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    null,
                    listOf()
                )
            )
        )

        presenter.searchGitHub(SEARCH_QUERY)
        Mockito.verify(viewContract, Mockito.times(1)).displayError("Search results or total count are null")
    }

    @Test //Проверим порядок вызова методов viewContract при ошибке
    fun handleResponseError_TotalCountIsNull_ViewContractMethodOrder() {
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    null,
                    listOf()
                )
            )
        )

        presenter.searchGitHub(SEARCH_QUERY)

        val inOrder = Mockito.inOrder(viewContract)
        inOrder.verify(viewContract).displayLoading(true)
        inOrder.verify(viewContract).displayError("Search results or total count are null")
        inOrder.verify(viewContract).displayLoading(false)
    }

    @Test //Теперь проверим успешный ответ сервера
    fun handleResponseSuccess() {
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    42,
                    listOf()
                )
            )
        )

        presenter.searchGitHub(SEARCH_QUERY)
        Mockito.verify(viewContract, Mockito.times(1)).displaySearchResults(listOf(), 42)
    }

    companion object {
        private const val SEARCH_QUERY = "some query"
        private const val ERROR_TEXT = "error"
    }


}