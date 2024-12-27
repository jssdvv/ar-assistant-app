package com.jssdvv.ara.machinery.di

import android.app.Application
import androidx.room.Room
import com.jssdvv.ara.machinery.data.local.MachineryDatabase
import com.jssdvv.ara.machinery.data.repository.ActivityRepositoryImpl
import com.jssdvv.ara.machinery.data.repository.MachineRepositoryImpl
import com.jssdvv.ara.machinery.domain.repository.ActivityRepository
import com.jssdvv.ara.machinery.domain.repository.MachineRepository
import com.jssdvv.ara.machinery.domain.usecase.ActivityUseCases
import com.jssdvv.ara.machinery.domain.usecase.DeleteActivity
import com.jssdvv.ara.machinery.domain.usecase.DeleteMachine
import com.jssdvv.ara.machinery.domain.usecase.GetActivities
import com.jssdvv.ara.machinery.domain.usecase.GetMachines
import com.jssdvv.ara.machinery.domain.usecase.InsertActivity
import com.jssdvv.ara.machinery.domain.usecase.InsertMachine
import com.jssdvv.ara.machinery.domain.usecase.MachineUseCases
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
    fun provideMachineDatabase(context: Application): MachineryDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = MachineryDatabase::class.java,
            name = MachineryDatabase.DATABASE_NAME
        ).createFromAsset("database/machinesDatabase.db").build()
    }

    @Provides
    @Singleton
    fun provideMachineRepository(database: MachineryDatabase): MachineRepository {
        return MachineRepositoryImpl(database.machineDao)
    }

    @Provides
    @Singleton
    fun provideActivityRepository(database: MachineryDatabase): ActivityRepository {
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