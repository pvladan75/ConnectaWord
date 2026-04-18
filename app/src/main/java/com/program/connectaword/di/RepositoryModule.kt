package com.program.connectaword.di

import com.program.connectaword.repository.AuthRepository
import com.program.connectaword.repository.AuthRepositoryImpl
import com.program.connectaword.repository.LobbyRepository
import com.program.connectaword.repository.LobbyRepositoryImpl
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLobbyRepository(
        lobbyRepositoryImpl: LobbyRepositoryImpl
    ): LobbyRepository
}