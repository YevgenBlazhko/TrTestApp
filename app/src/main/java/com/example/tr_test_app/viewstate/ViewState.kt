package com.example.tr_test_app.viewstate

sealed class ViewState {
    data object Inactive : ViewState()
    data class Response<T>(val response: T) : ViewState()
    data class Error(val error: String?) : ViewState()
}
