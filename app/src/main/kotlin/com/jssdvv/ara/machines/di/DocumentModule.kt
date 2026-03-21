package com.jssdvv.ara.machines.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.machines.data.repository.DocumentRepositoryImpl
import com.jssdvv.ara.machines.domain.repository.DocumentRepository
import com.jssdvv.ara.machines.domain.usecase.CountDocuments
import com.jssdvv.ara.machines.domain.usecase.DeleteDocuments
import com.jssdvv.ara.machines.domain.usecase.DocumentsDataManager
import com.jssdvv.ara.machines.domain.usecase.SelectDocuments
import com.jssdvv.ara.machines.domain.usecase.UpsertDocuments
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DocumentModule {

    @Provides
    @Singleton
    fun provideDocumentRepository(appDatabase: AppDatabase): DocumentRepository =
        DocumentRepositoryImpl(appDatabase.documentDao)

    @Provides
    @Singleton
    fun provideCountDocumentsUseCase(repository: DocumentRepository) = CountDocuments(repository)

    @Provides
    @Singleton
    fun provideSelectDocumentsUseCase(repository: DocumentRepository) = SelectDocuments(repository)

    @Provides
    @Singleton
    fun provideUpdateDocumentsUseCase(repository: DocumentRepository) = UpsertDocuments(repository)

    @Provides
    @Singleton
    fun provideDeleteDocumentsUseCase(repository: DocumentRepository) = DeleteDocuments(repository)

    @Provides
    @Singleton
    fun provideDocumentsDataManager(
        select: SelectDocuments,
        upsert: UpsertDocuments,
        delete: DeleteDocuments,
    ) = DocumentsDataManager(select, upsert, delete)
}