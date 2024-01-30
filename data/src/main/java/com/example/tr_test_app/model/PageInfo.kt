package com.example.tr_test_app.model

data class PageInfo(
    val startCursor: String?,
    val endCursor: String?,
    val hasNewPage: Boolean?,
    val hasPreviousPage: Boolean?,
)
