package com.paparazziapps.pretamistapp.modulos.login.data.repository

import com.paparazziapps.pretamistapp.helper.ResultData
import io.github.jan.supabase.gotrue.user.UserSession

interface AuthenticationRepository {
    suspend fun signIn(email: String, password: String): ResultData<UserSession?>
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signInWithGoogle(): ResultData<UserSession?>
}