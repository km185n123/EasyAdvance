package com.paparazziapps.pretamistapp.modulos.login.di

import com.paparazziapps.pretamistapp.modulos.login.data.repository.AuthenticationRepository
import com.paparazziapps.pretamistapp.modulos.login.data.repository.AuthenticationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.gotrue.Auth
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LoginModule {
 @Provides
    @Singleton
    fun provideAuthenticationRepository(auth: Auth): AuthenticationRepository {
        return AuthenticationRepositoryImpl(auth)
    }

}