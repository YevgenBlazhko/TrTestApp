package com.example.tr_test_app.intent

sealed class RepoDetailsIntent {
    data class FetchData(val repoId: String) : RepoDetailsIntent()
    data class ReloadData(val repoId: String): RepoDetailsIntent()

}