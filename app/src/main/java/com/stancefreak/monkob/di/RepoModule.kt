package com.stancefreak.monkob.di

import com.stancefreak.monkob.remote.network.ApiHelperImpl
import com.stancefreak.monkob.remote.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepoModule {

    @Provides
    @ViewModelScoped
    fun provideAppRepo(helperImpl: ApiHelperImpl) = AppRepository(helperImpl)

}