package com.jssdvv.ara.machines.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.machines.data.repository.OperationRepositoryImpl
import com.jssdvv.ara.machines.domain.repository.OperationRepository
import com.jssdvv.ara.machines.domain.usecase.DeleteOperation
import com.jssdvv.ara.machines.domain.usecase.OperationDataManager
import com.jssdvv.ara.machines.domain.usecase.SelectOperation
import com.jssdvv.ara.machines.domain.usecase.UpsertOperation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OperationModule {

    @Provides
    @Singleton
    fun provideOperationRepository(database: AppDatabase): OperationRepository =
        OperationRepositoryImpl(database.operationDao)

    @Provides
    @Singleton
    fun provideSelectOperationUseCase(repository: OperationRepository) = SelectOperation(repository)

    @Provides
    @Singleton
    fun provideUpsertOperationUseCase(repository: OperationRepository) = UpsertOperation(repository)

    @Provides
    @Singleton
    fun provideDeleteOperationUseCase(repository: OperationRepository) = DeleteOperation(repository)

    @Provides
    @Singleton
    fun provideOperationDataManager(
        select: SelectOperation,
        upsert: UpsertOperation,
        delete: DeleteOperation,
    ) = OperationDataManager(select, upsert, delete)
}