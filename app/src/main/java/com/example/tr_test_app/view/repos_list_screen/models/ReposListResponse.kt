package com.example.tr_test_app.view.repos_list_screen.models

import com.example.tr_test_app.model.Repository

data class ReposListResponse(
    val repositories: List<Repository>?,
    val loading: Boolean,
    val hasNextPage: Boolean
)
