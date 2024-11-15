package com.amarchaud.data.di

import com.amarchaud.data.repository.SimpleListDemoRepositoryImpl
import com.amarchaud.domain.repository.SimpleListDemoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindDataRepo(repository: SimpleListDemoRepositoryImpl): SimpleListDemoRepository
}