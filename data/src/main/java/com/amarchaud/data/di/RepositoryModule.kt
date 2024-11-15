package com.amarchaud.data.di

import com.amarchaud.domain.repository.AvivTestRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.amarchaud.data.repository.AvivTestRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindDataRepo(repository: AvivTestRepositoryImpl): AvivTestRepository
}