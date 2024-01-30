package com.example.tr_test_app.model

data class Repository (
    val id: String,
    val title: String,
    val owner: Owner,
    val scores: Int,
)