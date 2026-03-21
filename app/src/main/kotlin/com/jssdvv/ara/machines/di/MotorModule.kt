package com.jssdvv.ara.machines.di

import com.jssdvv.ara.core.data.local.AppDatabase
import com.jssdvv.ara.machines.data.repository.MotorRepositoryImpl
import com.jssdvv.ara.machines.domain.repository.MotorRepository
import com.jssdvv.ara.machines.domain.usecase.SelectMotorAndSpecs
import com.jssdvv.ara.machines.domain.usecase.SelectMotors
import com.jssdvv.ara.machines.domain.usecase.UpdateMotorSpecs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MotorModule {

    @Provides
    @Singleton
    fun provideMotorRepository(database: AppDatabase): MotorRepository =
        MotorRepositoryImpl(database.motorDao)

    @Provides
    @Singleton
    fun provideUpdateMotorSpecsUseCase(repository: MotorRepository): UpdateMotorSpecs =
        UpdateMotorSpecs(repository)

    @Provides
    @Singleton
    fun provideMotorAndMotorSpecsUseCase(repository: MotorRepository): SelectMotorAndSpecs =
        SelectMotorAndSpecs(repository)

    @Provides
    @Singleton
    fun provideUpdateMotorUseCase(repository: MotorRepository): SelectMotors =
        SelectMotors(repository)
}