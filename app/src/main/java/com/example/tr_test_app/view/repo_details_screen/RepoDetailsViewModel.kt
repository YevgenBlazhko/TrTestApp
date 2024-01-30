package com.example.tr_test_app.view.repo_details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr_test_app.DataConnect
import com.example.tr_test_app.intent.RepoDetailsIntent
import com.example.tr_test_app.viewstate.ViewState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class RepoDetailsViewModel : ViewModel() {

    val intent = Channel<RepoDetailsIntent>(Channel.UNLIMITED)

    private val _viewState = MutableLiveData<ViewState>(ViewState.Inactive)

    val viewState: LiveData<ViewState> get() = _viewState

    private val dataConnect by lazy { DataConnect() }

    init {
        handleIntent()
    }

    private fun fetchData(repoId: String) {
        if (_viewState.value != ViewState.Inactive) return
        loadData(repoId)
    }

    private fun loadData(repoId: String) {
        viewModelScope.launch {
            _viewState.value = try {
                val updatedData = dataConnect.loadRepositoryDetails(repoId)
                ViewState.Response(
                    updatedData
                )
            } catch (e: Exception) {
                ViewState.Error(e.localizedMessage)
            }
        }
    }

    private fun reloadData(repoId: String) {
        _viewState.value = ViewState.Inactive
        loadData(repoId)
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is RepoDetailsIntent.FetchData -> fetchData(it.repoId)
                    is RepoDetailsIntent.ReloadData -> reloadData(it.repoId)
                }
            }
        }
    }

}