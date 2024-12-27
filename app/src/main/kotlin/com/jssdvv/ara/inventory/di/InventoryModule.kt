package com.jssdvv.ara.inventory.di

import android.app.Application
import androidx.room.Room
import com.jssdvv.ara.inventory.data.local.InventoryDatabase
import com.jssdvv.ara.inventory.data.repository.InventoryItemRepositoryImpl
import com.jssdvv.ara.inventory.domain.repository.InventoryItemRepository
import com.jssdvv.ara.inventory.domain.usecase.DeleteInventoryItem
import com.jssdvv.ara.inventory.domain.usecase.GetInventoryItems
import com.jssdvv.ara.inventory.domain.usecase.InsertInventoryItem
import com.jssdvv.ara.inventory.domain.usecase.InventoryItemUseCases
import com.jssdvv.ara.inventory.domain.usecase.UpdateInventoryItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryModule {
    @Provides
    @Singleton
    fun provideInventoryDatabase(context: Application): InventoryDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = InventoryDatabase::class.java,
            name = InventoryDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(database: InventoryDatabase): InventoryItemRepository {
        return InventoryItemRepositoryImpl(database.inventoryItemDao)
    }

    @Provides
    @Singleton
    fun provideInventoryUseCases(repository: InventoryItemRepository): InventoryItemUseCases {
        return InventoryItemUseCases(
            getInventoryItems = GetInventoryItems(repository),
            insertInventoryItem = InsertInventoryItem(repository),
            updateInventoryItem = UpdateInventoryItem(repository),
            deleteInventoryItem = DeleteInventoryItem(repository)
        )
    }
}