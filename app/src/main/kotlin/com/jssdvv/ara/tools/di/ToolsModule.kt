package com.jssdvv.ara.tools.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.tools.data.repository.ToolsRepositoryImpl
import com.jssdvv.ara.tools.domain.repository.ToolsRepository
import com.jssdvv.ara.tools.domain.usecase.DeleteTools
import com.jssdvv.ara.tools.domain.usecase.SelectTools
import com.jssdvv.ara.tools.domain.usecase.ToolsDataManager
import com.jssdvv.ara.tools.domain.usecase.UpsertTools
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ToolsModule {

    @Provides
    @Singleton
    fun provideToolsRepository(database: AppDatabase): ToolsRepository =
        ToolsRepositoryImpl(database.toolDao)

    @Provides
    @Singleton
    fun provideSelectAllToolsUseCase(repository: ToolsRepository): SelectTools =
        SelectTools(repository)

    @Provides
    @Singleton
    fun provideDeleteToolsUseCase(repository: ToolsRepository): DeleteTools =
        DeleteTools(repository)

    @Provides
    @Singleton
    fun provideUpsertUseCase(repository: ToolsRepository): UpsertTools =
        UpsertTools(repository)

    @Provides
    @Singleton
    fun provideToolsDataManager(
        select: SelectTools,
        upsert: UpsertTools,
        delete: DeleteTools
    ) = ToolsDataManager(select, upsert, delete)
}