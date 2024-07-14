package com.paparazziapps.pretamistapp.modulos.dashboard.di

import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.BranchAgentProvider
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.BranchAgentSupaProvider
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.ClientProvider
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.ClientSupaProvider
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.ProfileProvider
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.ProfileSupaProvider
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.BranchAgentRepository
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.BranchAgentRepositoryImpl
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.ClientRepository
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.ClientRepositoryImpl
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.ProfileRepository
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.ProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DashboardModule {
    @Provides
    @Singleton
    fun provideProfileRepository(profileProvider: ProfileProvider): ProfileRepository {
        return ProfileRepositoryImpl(profileProvider)
    }

    @Provides
    @Singleton
    fun provideBranchAgentRepository(branchAgentProvider: BranchAgentProvider): BranchAgentRepository {
        return BranchAgentRepositoryImpl(branchAgentProvider)
    }

    @Provides
    @Singleton
    fun provideClientRepository(clientProvider: ClientProvider): ClientRepository {
        return ClientRepositoryImpl(clientProvider)
    }

    @Provides
    @Singleton
    fun provideProfileProvider(postgrest: Postgrest, storage: Storage): ProfileProvider {
        return ProfileSupaProvider(postgrest, storage)
    }

    @Provides
    @Singleton
    fun provideBranchAgentProvider(postgrest: Postgrest, storage: Storage): BranchAgentProvider {
        return BranchAgentSupaProvider(postgrest)
    }

    @Provides
    @Singleton
    fun provideClientProvider(postgrest: Postgrest, storage: Storage): ClientProvider {
        return ClientSupaProvider(postgrest)
    }
}