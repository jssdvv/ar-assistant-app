package com.jssdvv.ara.machinery.di

import android.app.Application
import androidx.room.Room
import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.core.data.repository.ActivityRepositoryImpl
import com.jssdvv.ara.core.data.repository.MachineRepositoryImpl
import com.jssdvv.ara.core.domain.repository.ActivityRepository
import com.jssdvv.ara.core.domain.repository.MachineRepository
import com.jssdvv.ara.machinery.domain.model.ActivityUseCases
import com.jssdvv.ara.machinery.domain.model.MachineUseCases
import com.jssdvv.ara.machinery.domain.usecase.DeleteActivity
import com.jssdvv.ara.machinery.domain.usecase.DeleteMachine
import com.jssdvv.ara.machinery.domain.usecase.GetActivities
import com.jssdvv.ara.machinery.domain.usecase.GetMachines
import com.jssdvv.ara.machinery.domain.usecase.InsertActivity
import com.jssdvv.ara.machinery.domain.usecase.InsertMachine
import com.jssdvv.ara.machinery.domain.usecase.UpdateActivity
import com.jssdvv.ara.machinery.domain.usecase.UpdateMachine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MachinesModule {
    @Provides
    @Singleton
    fun provideMachineDatabase(context: Application): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = AppDatabase.DATABASE_NAME
        ).createFromAsset("database/database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMachineRepository(database: AppDatabase): MachineRepository {
        return MachineRepositoryImpl(database.machineDao)
    }

    @Provides
    @Singleton
    fun provideActivityRepository(database: AppDatabase): ActivityRepository {
        return ActivityRepositoryImpl(database.activityDao)
    }

    @Provides
    @Singleton
    fun provideMachineUseCases(repository: MachineRepository): MachineUseCases {
        return MachineUseCases(
            getMachines = GetMachines(repository),
            insertMachine = InsertMachine(repository),
            updateMachine = UpdateMachine(repository),
            deleteMachine = DeleteMachine(repository)
        )
    }

    @Provides
    @Singleton
    fun provideActivityUseCases(repository: ActivityRepository): ActivityUseCases {
        return ActivityUseCases(
            getActivities = GetActivities(repository),
            insertActivity = InsertActivity(repository),
            updateActivity = UpdateActivity(repository),
            deleteActivity = DeleteActivity(repository)
        )
    }
}