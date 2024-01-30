package com.example.tr_test_app.repository

import androidx.core.net.toUri
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.tr_test_app.model.Owner
import com.example.tr_test_app.model.PageInfo
import com.example.tr_test_app.model.RepositoriesPage
import com.example.tr_test_app.model.Repository
import com.example.tr_test_app.model.RepositoryDetails
import com.example.tribuna_test_app.GetPopularRepositoryListQuery
import com.example.tribuna_test_app.GetRepositoryInfoQuery
import javax.inject.Inject


class ReposRepository @Inject constructor(private val apolloClient: ApolloClient) {

    suspend fun getPopularRepositoryList(
        queryText: String = "",
        endCursor: String? = null
    ): RepositoriesPage? {
        val query = "stars:>1000 sort:stars-desc $queryText in:name"
        return try {
            val publicRepositories: GetPopularRepositoryListQuery.Data = apolloClient
                .query(GetPopularRepositoryListQuery(query, Optional.present(endCursor)))
                .execute()
                .dataAssertNoErrors
            val repositories = publicRepositories.search.nodes?.map {
                Repository(
                    id = it?.onRepository?.id ?: "",
                    title = it?.onRepository?.name ?: "No title",
                    owner = Owner(
                        it?.onRepository?.owner?.login ?: "No user name",
                        it?.onRepository?.owner?.avatarUrl.toString().toUri()
                    ),
                    scores = it?.onRepository?.stargazers?.totalCount ?: 0,
                )
            }
            val pageInfo = PageInfo(
                startCursor = publicRepositories.search.pageInfo.startCursor,
                endCursor = publicRepositories.search.pageInfo.endCursor,
                hasPreviousPage = publicRepositories.search.pageInfo.hasPreviousPage,
                hasNewPage = publicRepositories.search.pageInfo.hasNextPage,
            )
            RepositoriesPage(repositories = repositories ?: listOf(), pageInfo = pageInfo)
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    suspend fun getRepositoryDetails(id: String): RepositoryDetails? {
        return try {
            val repositoryDetails: GetRepositoryInfoQuery.Data = apolloClient
                .query(GetRepositoryInfoQuery(id))
                .execute()
                .dataAssertNoErrors
            val repo = repositoryDetails.node?.onRepository
            RepositoryDetails(
                title = repo?.name ?: "",
                owner = Owner(
                    repo?.owner?.login ?: "No user name",
                    repo?.owner?.avatarUrl.toString().toUri(),
                ),
                description = repo?.description ?: "No descriptions",
                forksCount = repo?.forks?.totalCount ?: 0,
                issuesCount = repo?.issues?.totalCount ?: 0,
                starsCount = repo?.stargazers?.totalCount ?: 0,
                watchersCount = repo?.watchers?.totalCount ?: 0,
                pullRequestCount = repo?.pullRequests?.totalCount ?: 0,
                primaryLanguage = repo?.primaryLanguage?.name ?: "",
                licenseName = repo?.licenseInfo?.name ?: "",
            )
        } catch (e: Exception) {
            throw Exception(e)
        }
    }
}