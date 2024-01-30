package com.example.tr_test_app.model

data class RepositoryDetails (
    val title: String,
    val owner: Owner,
    val starsCount: Int,
    val forksCount: Int,
    val issuesCount: Int,
    val pullRequestCount: Int,
    val watchersCount: Int,
    val description: String,
    val primaryLanguage: String,
    val licenseName: String,
)