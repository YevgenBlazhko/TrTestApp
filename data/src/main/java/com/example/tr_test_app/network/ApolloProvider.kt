package com.example.tr_test_app.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class ApolloProvider {
    private val SERVER_URL = "https://api.github.com/graphql"

    private val HEADER_AUTHORIZATION = "Authorization"
    private val HEADER_AUTHORIZATION_BEARER = "Bearer"

    fun client(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(SERVER_URL)
            .addHttpHeader(
                HEADER_AUTHORIZATION,
                "$HEADER_AUTHORIZATION_BEARER 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"
            )
            .okHttpClient(getOkHttpClient())
            .build()
    }

    private fun getOkHttpClient() : OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
}