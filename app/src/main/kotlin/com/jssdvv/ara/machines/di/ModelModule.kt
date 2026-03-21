package com.jssdvv.ara.machines.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.machines.data.repository.ModelRepositoryImpl
import com.jssdvv.ara.machines.domain.repository.ModelRepository
import com.jssdvv.ara.machines.domain.usecase.CountModels
import com.jssdvv.ara.machines.domain.usecase.DeleteModels
import com.jssdvv.ara.machines.domain.usecase.ModelsDataManager
import com.jssdvv.ara.machines.domain.usecase.SelectModels
import com.jssdvv.ara.machines.domain.usecase.UpsertModels
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {

    @Provides
    @Singleton
    fun provideModelRepository(database: AppDatabase): ModelRepository =
        ModelRepositoryImpl(database.modelDao)

    @Provides
    @Singleton
    fun provideCountModelsUseCase(repository: ModelRepository) = CountModels(repository)

    @Provides
    @Singleton
    fun provideSelectModelsUseCase(repository: ModelRepository) = SelectModels(repository)

    @Provides
    @Singleton
    fun provideUpdateModelsUseCase(repository: ModelRepository) = UpsertModels(repository)

    @Provides
    @Singleton
    fun provideDeleteModelsUseCase(repository: ModelRepository) = DeleteModels(repository)

    @Provides
    @Singleton
    fun provideModelsDataManager(
        select: SelectModels,
        update: UpsertModels,
        delete: DeleteModels,
    ) = ModelsDataManager(select, update, delete)
}