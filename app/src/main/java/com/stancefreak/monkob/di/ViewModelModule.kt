package com.stancefreak.monkob.di

import com.stancefreak.monkob.remote.network.ApiHelperImpl
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.views.home.HomeViewModel
import com.stancefreak.monkob.views.monitoring.physical.MonitoringPhysicalViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun homeViewModel(repository: AppRepository) = HomeViewModel(repository)

    @Provides
    @ViewModelScoped
    fun physicalViewModel(repository: AppRepository) = MonitoringPhysicalViewModel(repository)

}