package com.example.tr_test_app.view.repo_details_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.tr_test_app.intent.RepoDetailsIntent
import com.example.tr_test_app.model.RepositoryDetails
import com.example.tr_test_app.view.components.ErrorView
import com.example.tr_test_app.view.components.Loader
import com.example.tr_test_app.viewstate.ViewState
import com.example.tribuna_test_app.R
import kotlinx.coroutines.launch

class RepoDetailsFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this)[RepoDetailsViewModel::class.java] }

    private var repoId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        repoId = arguments?.getString(getString(R.string.repo_id))
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
                        is ViewState.Error -> {
                            ErrorView(onPressedTry = {
                                reloadData()
                            })
                        }

                        is ViewState.Inactive -> {
                            Loader(withBackground = false)
                        }

                        is ViewState.Response<*> -> {
                            val data =
                                (state as ViewState.Response<*>).response as RepositoryDetails
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AsyncImage(
                                    model = data.owner.avatar,
                                    contentDescription = null,
                                    modifier = Modifier.size(124.dp)
                                )
                                Text(
                                    text = data.title,
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.W700,
                                    )
                                )
                                Text(
                                    text = data.description,
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.W500,
                                        textAlign = TextAlign.Center,
                                    )
                                )
                                val textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center)
                                Text(text = "Author: ${data.owner.login}", style = textStyle)
                                Text(text = "Stars: ${data.starsCount}", style = textStyle)
                                Text(text = "Forks: ${data.forksCount}", style = textStyle)
                                Text(text = "Issues: ${data.issuesCount}", style = textStyle)
                                Text(text = "Pull requests: ${data.pullRequestCount}", style = textStyle)
                                Text(text = "Watchers: ${data.watchersCount}", style = textStyle)
                                Text(text = "Primary language: ${data.primaryLanguage}", style = textStyle)
                                Text(text = "License: ${data.licenseName}", style = textStyle)
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

    private fun reloadData() {
        lifecycleScope.launch {
            viewModel.intent.send(RepoDetailsIntent.ReloadData(repoId ?: ""))
        }
    }

    private fun setupData() {
        lifecycleScope.launch {
            viewModel.intent.send(RepoDetailsIntent.FetchData(repoId ?: ""))
        }
    }
}