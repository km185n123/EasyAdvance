package com.paparazziapps.pretamistapp.modulos.login.views

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityLoginBinding
import com.paparazziapps.pretamistapp.factory.LoginViewModelFactory
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.modulos.login.providers.LoginProvider
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelLogin

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    lateinit var txtRegistroNuevo:MaterialTextView
    var btnLoginEmail: MaterialButton? = null
    var isValidEmail = false
    var isValidPass:Boolean = false
    private lateinit var _viewModelLogin: ViewModelLogin



    var TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
        _viewModelLogin = ViewModelProvider(this, LoginViewModelFactory(LoginProvider()))
            .get(ViewModelLogin::class.java)


        setColorToStatusBar(this)
        isAlreadyLogin()


        binding.apply {
            txtRegistroNuevo = btnRegister
            btnLoginEmail    = binding.ingresarLoginButton
        }

        //Validate Data
        validateFields()
        openNewRegistro()

        //Observables with MVVM
        showObservables()

        //Login Firebase
        loginFirebase()

        binding.versioncode.text = "Versión ${getVersionName()}"
    }

    private fun showObservables() {

        _viewModelLogin.message.observe(this) { message ->
            if (message != null) {
                _showMessageMainThread(message)
            }
        }

        _viewModelLogin.isLoading.observe(this) { isLoading ->
            Log.e("ISLOADING", "ISLOADING:$isLoading")
            if (isLoading) {
                binding.layoutLoading.visibility = View.VISIBLE
            } else {
                binding.layoutLoading.visibility = View.GONE
            }
        }

        _viewModelLogin.isLoginEmail.observe(this) { isLoginEmail ->
            println("isLoginEmail: $isLoginEmail")
            if (isLoginEmail) {
                Log.e(TAG, "EMAIL ENVIADO: " + binding.email.text.toString().lowercase())
                startActivity(
                    Intent(this, PrincipalActivity::class.java)
                        .putExtra("email", binding.email.text.toString().lowercase())
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }

    }

    private fun loginFirebase() {
        btnLoginEmail!!.setOnClickListener {
            hideKeyboardActivity(this@LoginActivity)
            if (isConnected(applicationContext)) {
                _viewModelLogin?.loginWithEmail(
                    binding.email.text.toString().trim(),
                    binding.pass.text.toString().trim()
                )
            } else _showMessageMainThread("Sin conexion a internet")
        }
        /*
        btnAnonimo.setOnClickListener(View.OnClickListener {
            if (TextUtilsText.isConnected(applicationContext)) {
                viewmodel.loginAnonymous()
            } else _showMessageMainThread("Sin conexion a internet")
        })*/
    }

    private fun validateFields() {
        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 0) validEmail(s.toString().trim { it <= ' ' })
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.pass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 5) {
                    binding.passLayout.error = null
                    isValidPass = true
                } else {
                    binding.passLayout.error = "La contraseña debe tener minimo 6 caracteres"
                    isValidPass = false
                }
                validEmailPass()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun validEmail(s: CharSequence) {
        if (isValidEmail(s)) {
            binding.emailLayout.error = null
            isValidEmail = true
        } else {
            binding.emailLayout.error = "Correo electrónico invalido"
            isValidEmail = false
        }
        validEmailPass()
    }

    private fun validEmailPass() {
        if (isValidEmail && isValidPass) {
            binding.ingresarLoginButton.apply {
                isEnabled = true
                backgroundTintMode = PorterDuff.Mode.SCREEN
                backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.colorPrimary)
                setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }

        } else {
            binding.ingresarLoginButton.apply {
                isEnabled = false
                backgroundTintMode = PorterDuff.Mode.MULTIPLY
                backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.color_input_text)
                setTextColor(ContextCompat.getColor(applicationContext, R.color.color_input_text))
            }
        }
    }


    private fun isAlreadyLogin() {
        if(MyPreferences().isLogin)
        {
          startActivity(Intent(this@LoginActivity, PrincipalActivity::class.java))
        }
    }

    private fun openNewRegistro() {
        txtRegistroNuevo.apply {
            setOnClickListener {
                startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            }
        }
    }

    private fun _showMessageMainThread(message: String) {
        Snackbar.make(findViewById(android.R.id.content), "" + message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}