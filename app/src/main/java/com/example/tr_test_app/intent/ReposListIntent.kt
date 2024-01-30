package com.example.tr_test_app.intent

sealed class ReposListIntent {
    data object FetchData : ReposListIntent()
    data class SearchItem (val query: String): ReposListIntent()

    data object LoadNextPage: ReposListIntent()

    data object ReloadData: ReposListIntent()

}