package com.example.tr_test_app.di

import com.example.tr_test_app.repository.ReposRepository
import dagger.Component

@Component(modules = [ReposRepositoryModule::class])
interface ReposRepositoryComponent {
    fun getReposRepository(): ReposRepository
}