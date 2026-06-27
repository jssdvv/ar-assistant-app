package com.jssdvv.ara.schedule.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.schedule.data.repository.EventRepositoryImpl
import com.jssdvv.ara.schedule.domain.repository.EventRepository
import com.jssdvv.ara.schedule.domain.usecase.DeleteEvents
import com.jssdvv.ara.schedule.domain.usecase.EventsDataManager
import com.jssdvv.ara.schedule.domain.usecase.SelectEvents
import com.jssdvv.ara.schedule.domain.usecase.UpsertEvents
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventsModule {

    @Provides
    @Singleton
    fun provideEventRepository(database: AppDatabase): EventRepository =
        EventRepositoryImpl(database.eventDao)

    @Provides
    @Singleton
    fun provideSelectEventsUseCase(repository: EventRepository): SelectEvents =
        SelectEvents(repository)

    @Provides
    @Singleton
    fun provideDeleteEventsUseCase(repository: EventRepository): DeleteEvents =
        DeleteEvents(repository)

    @Provides
    @Singleton
    fun provideUpsertEventsUseCase(repository: EventRepository): UpsertEvents =
        UpsertEvents(repository)

    @Provides
    @Singleton
    fun provideEventsDataManager(
        select: SelectEvents,
        upsert: UpsertEvents,
        delete: DeleteEvents,
    ): EventsDataManager = EventsDataManager(select, upsert, delete)
}
