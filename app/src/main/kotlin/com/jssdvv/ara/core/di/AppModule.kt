package com.jssdvv.ara.core.di

import android.content.Context
import androidx.room.Room
import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.core.data.local.SeedDatabaseCallback
import com.jssdvv.ara.core.data.repository.BarcodeWriterImpl
import com.jssdvv.ara.core.data.repository.DirectoriesManagerImpl
import com.jssdvv.ara.core.data.repository.FilesManagerImpl
import com.jssdvv.ara.core.data.repository.NotificationService
import com.jssdvv.ara.core.data.repository.PDFHelperImpl
import com.jssdvv.ara.core.data.repository.PermissionHandlerImpl
import com.jssdvv.ara.core.data.repository.RationaleProviderImpl
import com.jssdvv.ara.core.data.repository.VibratorHelperImpl
import com.jssdvv.ara.core.domain.repository.BarcodeWriter
import com.jssdvv.ara.core.domain.repository.DirectoriesManager
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PDFHelper
import com.jssdvv.ara.core.domain.repository.PermissionHandler
import com.jssdvv.ara.core.domain.repository.RationaleProvider
import com.jssdvv.ara.core.domain.repository.VibratorHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = AppDatabase.DATABASE_NAME
        ).createFromAsset(AppDatabase.DATABASE_ASSET_PATH)
            //.fallbackToDestructiveMigration()
            .addCallback(SeedDatabaseCallback())
            .build()

    @Provides
    @Singleton
    fun provideDirectoriesManager(context: Context): DirectoriesManager =
        DirectoriesManagerImpl(context)

    @Provides
    @Singleton
    fun provideBarcodeWriter(
        context: Context,
        directoriesManager: DirectoriesManager,
    ): BarcodeWriter =
        BarcodeWriterImpl(
            context = context,
            directoriesManager = directoriesManager
        )

    @Provides
    @Singleton
    fun provideVibratorHelper(context: Context): VibratorHelper = VibratorHelperImpl(context)

    @Provides
    @Singleton
    fun providesLoaderHelper(
        context: Context,
        directoriesManager: DirectoriesManager,
    ): FilesManager = FilesManagerImpl(context, directoriesManager)

    @Provides
    @Singleton
    fun provideRationaleProvider(): RationaleProvider = RationaleProviderImpl()

    @Provides
    @Singleton
    fun providePermissionHandler(
        context: Context,
        rationaleProvider: RationaleProvider,
    ): PermissionHandler = PermissionHandlerImpl(context, rationaleProvider)

    @Provides
    @Singleton
    fun providePDFGeneratorHelper(
        context: Context,
        directoriesManager: DirectoriesManager,
        filesManager: FilesManager
    ): PDFHelper = PDFHelperImpl(context, directoriesManager, filesManager)

    @Provides
    @Singleton
    fun provideNotificationService(
        context: Context
    ): NotificationService = NotificationService(context)
}