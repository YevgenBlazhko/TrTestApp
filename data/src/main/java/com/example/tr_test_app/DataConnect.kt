package com.example.tr_test_app

import com.example.tr_test_app.di.DaggerReposRepositoryComponent
import com.example.tr_test_app.model.RepositoriesPage
import com.example.tr_test_app.model.RepositoryDetails
import com.example.tr_test_app.repository.ReposRepository

class DataConnect {

    private val reposRepository: ReposRepository =
        DaggerReposRepositoryComponent.create().getReposRepository()

    suspend fun loadRepositoriesList(): RepositoriesPage? {
        return reposRepository.getPopularRepositoryList()
    }

    suspend fun loadRepositoriesList(query: String): RepositoriesPage? {
        return reposRepository.getPopularRepositoryList(query)
    }

    suspend fun loadRepositoriesListByPage(endCursor: String?): RepositoriesPage? {
        return reposRepository.getPopularRepositoryList(endCursor = endCursor)
    }

    suspend fun loadRepositoryDetails(id: String) : RepositoryDetails? {
        return reposRepository.getRepositoryDetails(id)
    }

}