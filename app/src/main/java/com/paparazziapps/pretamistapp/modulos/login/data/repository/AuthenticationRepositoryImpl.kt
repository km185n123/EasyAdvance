package com.paparazziapps.pretamistapp.modulos.login.data.repository

import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.login.data.repository.AuthenticationRepository
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import javax.inject.Inject


class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: Auth
) : AuthenticationRepository {
    override suspend fun signIn(email: String, password: String): ResultData<UserSession?> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            ResultData.Success( auth.currentSessionOrNull())
        } catch (e: Exception) {
            ResultData.Failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Boolean {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signInWithGoogle(): ResultData<UserSession?> {
        return try {
            auth.signInWith(Google)
            ResultData.Success( auth.currentSessionOrNull())
        } catch (e: Exception) {
            ResultData.Failure(e)
        }
    }
}
