package com.paparazziapps.pretamistapp.modulos.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/*import io.github.jan.supabase.common.App
import io.github.jan.supabase.common.ChatViewModel
import io.github.jan.supabase.gotrue.handleDeeplinks*/

// Clase singleton para manejar la configuración del cliente




object SupabaseClientManager {

    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "BuildConfig.SUPABASE_URL",
            supabaseKey = "BuildConfig.SUPABASE_ANON_KEY"
        ) {
            install(Postgrest)
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "app"
                host = "supabase.com"
            }
            install(Storage)
        }
    }
}
