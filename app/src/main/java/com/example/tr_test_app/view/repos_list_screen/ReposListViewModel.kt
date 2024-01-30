package com.example.tr_test_app.view.repos_list_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr_test_app.DataConnect
import com.example.tr_test_app.model.PageInfo
import com.example.tr_test_app.intent.ReposListIntent
import com.example.tr_test_app.view.repos_list_screen.models.ReposListResponse
import com.example.tr_test_app.viewstate.ViewState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class ReposListViewModel : ViewModel() {

    val intent = Channel<ReposListIntent>(Channel.UNLIMITED)

    private val _viewState = MutableLiveData<ViewState>(ViewState.Inactive)
    val viewState: LiveData<ViewState> get() = _viewState

    private var pageInfo: PageInfo? = null

    private val dataConnect by lazy { DataConnect() }

    private val viewStateResponse: ViewState.Response<*>?
        get() = _viewState.value as? ViewState.Response<*>

    init {
        handleIntent()
    }

    private fun fetchData() {
        if (_viewState.value != ViewState.Inactive) return
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _viewState.value = try {
                val updatedData = dataConnect.loadRepositoriesList()
                pageInfo = updatedData?.pageInfo
                ViewState.Response(
                    ReposListResponse(
                        repositories = updatedData?.repositories,
                        loading = false,
                        hasNextPage = updatedData?.pageInfo?.hasNewPage ?: false
                    )
                )
            } catch (e: Exception) {
                ViewState.Error(e.localizedMessage)
            }
        }
    }

    private fun loadNextPageData() {
        viewModelScope.launch {
            val data = (viewStateResponse?.response as? ReposListResponse) ?: return@launch
            _viewState.value = ViewState.Response(data.copy(loading = true))
            val oldRepositoriesList = data.repositories ?: listOf()
            _viewState.value = try {
                val updatedData = dataConnect.loadRepositoriesListByPage(
                    endCursor = pageInfo?.endCursor
                )
                pageInfo = updatedData?.pageInfo
                val newRepositoriesList = updatedData?.repositories ?: listOf()
                ViewState.Response(
                    ReposListResponse(
                        repositories = oldRepositoriesList + newRepositoriesList,
                        loading = false,
                        hasNextPage = updatedData?.pageInfo?.hasNewPage ?: false
                    )
                )
            } catch (e: Exception) {
                ViewState.Error(e.localizedMessage)
            }
        }
    }

    private fun searchData(query: String) {
        if (query.isEmpty()) {
            resetData()
            return
        }
        viewModelScope.launch {
            val data = (viewStateResponse?.response as? ReposListResponse) ?: return@launch
            _viewState.value = ViewState.Response(data.copy(loading = true))
            _viewState.value = try {
                val updatedData = dataConnect.loadRepositoriesList(query)
                pageInfo = null
                ViewState.Response(
                    ReposListResponse(
                        repositories = updatedData?.repositories,
                        loading = false,
                        hasNextPage = pageInfo?.hasNewPage ?: false
                    )
                )
            } catch (e: Exception) {
                ViewState.Error(e.localizedMessage)
            }
        }
    }

    private fun reloadData() {
        _viewState.value = ViewState.Inactive
        loadData()
    }

    private fun resetData() {
        val data = (viewStateResponse?.response as? ReposListResponse) ?: return
        _viewState.value = ViewState.Response(data.copy(loading = true))
        loadData()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is ReposListIntent.FetchData -> fetchData()
                    is ReposListIntent.SearchItem -> searchData(it.query)
                    is ReposListIntent.LoadNextPage -> loadNextPageData()
                    is ReposListIntent.ReloadData -> reloadData()
                }
            }
        }
    }
}