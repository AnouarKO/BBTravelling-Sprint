package com.example.bbtraveling.di

import com.example.bbtraveling.data.repository.RoomTripRepository
import com.example.bbtraveling.data.repository.RoomUserProfileRepository
import com.example.bbtraveling.data.settings.SharedPreferencesSettingsRepository
import com.example.bbtraveling.domain.repository.TripRepository
import com.example.bbtraveling.domain.repository.UserProfileRepository
import com.example.bbtraveling.domain.repository.UserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(
        repository: RoomTripRepository
    ): TripRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        repository: RoomUserProfileRepository
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(
        repository: SharedPreferencesSettingsRepository
    ): UserSettingsRepository
}
