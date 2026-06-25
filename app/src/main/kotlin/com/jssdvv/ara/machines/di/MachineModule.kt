package com.jssdvv.ara.machines.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.machines.data.repository.MachineRepositoryImpl
import com.jssdvv.ara.machines.domain.repository.MachineRepository
import com.jssdvv.ara.machines.domain.usecase.DeleteMachines
import com.jssdvv.ara.machines.domain.usecase.MachinesDataManager
import com.jssdvv.ara.machines.domain.usecase.SearchMachines
import com.jssdvv.ara.machines.domain.usecase.SelectMachines
import com.jssdvv.ara.machines.domain.usecase.UpsertMachines
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MachineModule {

    @Provides
    @Singleton
    fun provideMachineRepository(database: AppDatabase): MachineRepository =
        MachineRepositoryImpl(database.machineDao)

    @Provides
    @Singleton
    fun provideGetMachinesUseCase(repository: MachineRepository): SelectMachines =
        SelectMachines(repository)

    @Provides
    @Singleton
    fun provideGetMachineUseCase(repository: MachineRepository): SearchMachines =
        SearchMachines(repository)

    @Provides
    @Singleton
    fun provideDeleteMachineUseCase(repository: MachineRepository): DeleteMachines =
        DeleteMachines(repository)

    @Provides
    @Singleton
    fun provideUpdateMachineUseCase(repository: MachineRepository): UpsertMachines =
        UpsertMachines(repository)

    @Provides
    @Singleton
    fun provideMachinesDataManager(
        select: SelectMachines,
        upsert: UpsertMachines,
        delete: DeleteMachines
    ) = MachinesDataManager(select, upsert, delete)
}