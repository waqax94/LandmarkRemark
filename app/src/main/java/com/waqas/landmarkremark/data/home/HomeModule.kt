package com.waqas.landmarkremark.data.home

import com.waqas.landmarkremark.data.home.repository.HomeRepositoryImp
import com.waqas.landmarkremark.domain.home.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HomeModule {

    @Singleton
    @Provides
    fun provideHomeRepository(): HomeRepository {
        return HomeRepositoryImp()
    }

}