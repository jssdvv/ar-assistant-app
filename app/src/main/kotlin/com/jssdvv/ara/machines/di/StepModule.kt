package com.jssdvv.ara.machines.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.machines.data.repository.StepRepositoryImpl
import com.jssdvv.ara.machines.domain.repository.StepRepository
import com.jssdvv.ara.machines.domain.usecase.DeleteSteps
import com.jssdvv.ara.machines.domain.usecase.SelectSteps
import com.jssdvv.ara.machines.domain.usecase.StepsDataManager
import com.jssdvv.ara.machines.domain.usecase.UpsertSteps
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StepModule {

    @Provides
    @Singleton
    fun provideStepRepository(database: AppDatabase): StepRepository =
        StepRepositoryImpl(database.stepDao)

    @Provides
    @Singleton
    fun provideSelectStepsUseCase(repository: StepRepository) = SelectSteps(repository)

    @Provides
    @Singleton
    fun provideUpsertStepsUseCase(repository: StepRepository) = UpsertSteps(repository)

    @Provides
    @Singleton
    fun provideDeleteStepsUseCase(repository: StepRepository) = DeleteSteps(repository)

    @Provides
    @Singleton
    fun provideStepsDataManager(
        select: SelectSteps,
        upsert: UpsertSteps,
        delete: DeleteSteps,
    ) = StepsDataManager(select, upsert, delete)
}