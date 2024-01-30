package com.example.tr_test_app.view.repos_list_screen

import RepoItem
import SearchInputField
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.tr_test_app.intent.ReposListIntent
import com.example.tr_test_app.view.MainActivity
import com.example.tr_test_app.view.repo_details_screen.RepoDetailsFragment
import com.example.tr_test_app.view.components.ErrorView
import com.example.tr_test_app.view.components.Loader
import com.example.tr_test_app.view.repos_list_screen.models.ReposListResponse
import com.example.tr_test_app.viewstate.ViewState
import com.example.tribuna_test_app.R
import kotlinx.coroutines.launch


class ReposListFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this)[ReposListViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.viewState.observeAsState(ViewState.Inactive)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Cyan),
                    contentAlignment = Alignment.Center
                ) {
                    when (state) {
                        is ViewState.Inactive -> {
                            Loader(withBackground = false)
                        }

                        is ViewState.Error -> {
                            ErrorView(onPressedTry = {
                                reloadData()
                            })
                        }

                        is ViewState.Response<*> -> {
                            val data =
                                (state as ViewState.Response<*>).response as ReposListResponse
                            val repositories = data.repositories ?: arrayListOf()

                            Column {
                                SearchInputField(onPressedSearch = {
                                    searchData(it)
                                })

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center

                                ) {
                                    if ((repositories.isNotEmpty()))
                                        LazyColumn(
                                            contentPadding = PaddingValues(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {
                                            items(repositories.size) { index ->
                                                val item = repositories[index]
                                                RepoItem(repository = item, onTap = {
                                                    goToRepoDetails(it)
                                                })
                                            }
                                            if (data.hasNextPage && !data.loading)
                                                item {
                                                    IconButton(
                                                        modifier = Modifier.align(Alignment.Center),
                                                        onClick = { loadNextPage() }) {
                                                        Icon(
                                                            Icons.Filled.Refresh,
                                                            contentDescription = "Refresh",
                                                            modifier = Modifier
                                                                .size(64.dp)

                                                        )
                                                    }
                                                }
                                        }
                                    else Text(text = "No results")
                                }
                            }

                            if (data.loading) {
                                Loader(withBackground = true)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
    }

    private fun searchData(query: String) {
        lifecycleScope.launch {
            viewModel.intent.send(ReposListIntent.SearchItem(query))
        }
    }

    private fun loadNextPage() {
        lifecycleScope.launch {
            viewModel.intent.send(ReposListIntent.LoadNextPage)
        }
    }

    private fun reloadData() {
        lifecycleScope.launch {
            viewModel.intent.send(ReposListIntent.ReloadData)
        }
    }

    private fun setupData() {
        lifecycleScope.launch {
            viewModel.intent.send(ReposListIntent.FetchData)
        }
    }

    private fun goToRepoDetails(repoId: String) {
        val fragment = RepoDetailsFragment()
        val args = Bundle()
        args.putString(getString(R.string.repo_id), repoId)
        fragment.arguments = args
        (requireActivity() as MainActivity).goTo(fragment)
    }
}