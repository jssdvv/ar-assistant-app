package com.jssdvv.ara.machines.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.machines.data.repository.ActivityRepositoryImpl
import com.jssdvv.ara.machines.domain.repository.ActivityRepository
import com.jssdvv.ara.machines.domain.usecase.CountActivities
import com.jssdvv.ara.machines.domain.usecase.DeleteActivities
import com.jssdvv.ara.machines.domain.usecase.SelectActivities
import com.jssdvv.ara.machines.domain.usecase.UpsertActivities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ActivityModule {

    @Provides
    @Singleton
    fun provideActivityRepository(database: AppDatabase): ActivityRepository =
        ActivityRepositoryImpl(database.activityDao)

    @Provides
    @Singleton
    fun provideCountActivitiesUseCase(repository: ActivityRepository): CountActivities =
        CountActivities(repository)

    @Provides
    @Singleton
    fun provideGetActivitiesUseCase(repository: ActivityRepository): SelectActivities =
        SelectActivities(repository)

    @Provides
    @Singleton
    fun provideUpdateActivityUseCase(repository: ActivityRepository): UpsertActivities =
        UpsertActivities(repository)

    @Provides
    @Singleton
    fun provideDeleteActivityUseCase(repository: ActivityRepository): DeleteActivities =
        DeleteActivities(repository)
}