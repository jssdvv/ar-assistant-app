package com.jssdvv.ara.machines.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.machines.data.repository.MarkerRepositoryImpl
import com.jssdvv.ara.machines.domain.repository.MarkerRepository
import com.jssdvv.ara.machines.domain.usecase.CountMarkers
import com.jssdvv.ara.machines.domain.usecase.DeleteMarkers
import com.jssdvv.ara.machines.domain.usecase.MarkersDataManager
import com.jssdvv.ara.machines.domain.usecase.SelectMarker
import com.jssdvv.ara.machines.domain.usecase.SelectMarkers
import com.jssdvv.ara.machines.domain.usecase.UpsertMarkers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MarkerModule {

    @Provides
    @Singleton
    fun provideMarkerRepository(database: AppDatabase): MarkerRepository =
        MarkerRepositoryImpl(database.markerDao)

    @Provides
    @Singleton
    fun provideCountMarkersUseCase(repository: MarkerRepository) = CountMarkers(repository)

    @Provides
    @Singleton
    fun provideSelectMarkerUseCase(repository: MarkerRepository) = SelectMarker(repository)

    @Provides
    @Singleton
    fun provideSelectMarkersUseCase(repository: MarkerRepository) = SelectMarkers(repository)

    @Provides
    @Singleton
    fun provideUpdateMarkerUseCase(repository: MarkerRepository) = UpsertMarkers(repository)

    @Provides
    @Singleton
    fun provideDeleteMarkerUseCase(repository: MarkerRepository) = DeleteMarkers(repository)

    @Provides
    @Singleton
    fun provideMarkersDataManager(
        select: SelectMarkers,
        update: UpsertMarkers,
        delete: DeleteMarkers,
    ) = MarkersDataManager(select, update, delete)
}