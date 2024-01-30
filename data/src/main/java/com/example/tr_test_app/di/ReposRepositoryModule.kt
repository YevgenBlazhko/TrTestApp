package com.example.tr_test_app.di

import com.apollographql.apollo3.ApolloClient
import com.example.tr_test_app.network.ApolloProvider
import dagger.Module
import dagger.Provides

@Module
class ReposRepositoryModule {

    @Provides
    fun provideApolloProvider(): ApolloProvider {
        return ApolloProvider()
    }

    @Provides
    fun provideApolloClient(apolloProvider: ApolloProvider): ApolloClient {
        return apolloProvider.client()
    }
}