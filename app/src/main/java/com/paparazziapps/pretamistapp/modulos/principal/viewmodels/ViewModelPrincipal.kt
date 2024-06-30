package com.paparazziapps.pretamistapp.modulos.principal.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.paparazziapps.pretamistapp.modulos.login.pojo.User
import com.paparazziapps.pretamistapp.modulos.login.providers.LoginProvider
import com.paparazziapps.pretamistapp.modulos.login.providers.UserProvider
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelRegistroUsuario

class ViewModelPrincipal : ViewModel() {


    var mUserProvider = UserProvider()
    var user = User()
    var mAuth = LoginProvider()

    private val _user = MutableLiveData<User>()

    private val _message = MutableLiveData<String>()
    private val _locationUpdateSuccess = MutableLiveData<Boolean>()

    fun showMessage(): LiveData<String> {
        return _message
    }

    fun getUser(): LiveData<User> {
        return _user
    }

    fun locationUpdateSuccess(): LiveData<Boolean> {
        return _locationUpdateSuccess
    }

    fun getUserEmail(): String? {
        return mAuth.getEmail()
    }

    fun searchUserByEmail() {
        //_isLoading.value = true
        try {
            mUserProvider.searchUserByEmail(mAuth.getEmail()).addOnSuccessListener { task ->

                if(task.exists())
                {
                    user = task.toObject(User::class.java)!!
                    _user.value = user
                } else {
                    // If sign in fails, display a message to the user.
                    _message.setValue("Ah ocurrido un error al traer los datos del usurio")
                    //_isLoading.setValue(false)
                }

            }.addOnFailureListener { e ->
                _message.setValue(e.message)
               // _isLoading.setValue(false)
            }
        } catch (e: Exception) {
            _message.setValue(e.message)
        }
    }

    fun updateUserLocation(email: String, latitude: Double, longitude: Double) {
        mUserProvider.updateLocation(email, latitude, longitude).addOnSuccessListener {
            _locationUpdateSuccess.value = true
        }.addOnFailureListener { e ->
            _message.value = e.message
            _locationUpdateSuccess.value = false
        }
    }


    companion object Singleton{
        private var instance: ViewModelPrincipal? = null

        fun getInstance(): ViewModelPrincipal =
            instance ?: ViewModelPrincipal(
                //local y remoto
            ).also {
                instance = it
            }

        fun destroyInstance(){
            instance = null
        }
    }
}