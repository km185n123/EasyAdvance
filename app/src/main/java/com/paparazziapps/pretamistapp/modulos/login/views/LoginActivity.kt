package com.paparazziapps.pretamistapp.modulos.login.views

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityLoginBinding
import com.paparazziapps.pretamistapp.helper.getVersionName
import com.paparazziapps.pretamistapp.helper.hideKeyboardActivity
import com.paparazziapps.pretamistapp.helper.isConnected
import com.paparazziapps.pretamistapp.helper.isValidEmail
import com.paparazziapps.pretamistapp.helper.setColorToStatusBar
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.SignInViewModel
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    private var isValidEmail = false
    private var isValidPass = false
    private val viewModelLogin: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        setColorToStatusBar(this)
        setupViews()
        observeViewModel()
        checkPreviousLogin()
    }

    private fun setupViews() = binding.apply {
        versioncode.text = "Versión ${getVersionName()}"
        email.addTextChangedListener(emailTextWatcher)
        pass.addTextChangedListener(passwordTextWatcher)

        ingresarLoginButton.setOnClickListener {
            hideKeyboardActivity(this@LoginActivity)
            if (isConnected(applicationContext)) {
                viewModelLogin.onSignIn(
                    email.text.toString().trim(),
                    pass.text.toString().trim()
                )
            } else {
                showMessage("Sin conexión a internet")
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModelLogin.message.observe(this) { message ->
            message?.let { showMessage(it) }
        }

        viewModelLogin.isLoading.observe(this) { isLoading ->
            isLoading?.let {
                binding.layoutLoading.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        viewModelLogin.userSession.observe(this) { userSession ->
            userSession?.let {
                Log.d(TAG, "EMAIL ENVIADO: ${binding.email.text.toString().lowercase()}")
                startActivity(Intent(this, PrincipalActivity::class.java).apply {
                    putExtra("email", binding.email.text.toString().lowercase())
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }

    private fun checkPreviousLogin() {
        if (MyPreferences().isLogin) {
            startActivity(Intent(this@LoginActivity, PrincipalActivity::class.java))
            finish()
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { validEmail(it.toString().trim()) }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                if (it.length > 5) {
                    binding.passLayout.error = null
                    isValidPass = true
                } else {
                    binding.passLayout.error = "La contraseña debe tener mínimo 6 caracteres"
                    isValidPass = false
                }
                validateEmailPassword()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun validEmail(email: String) {
        isValidEmail = isValidEmail(email)
        binding.emailLayout.error =
            if (isValidEmail) null else "Correo electrónico inválido"
        validateEmailPassword()
    }

    private fun validateEmailPassword() {
        binding.ingresarLoginButton.isEnabled = isValidEmail && isValidPass
        binding.ingresarLoginButton.apply {
            val tintList = if (isEnabled) R.color.colorPrimary else R.color.color_input_text
            backgroundTintList = ContextCompat.getColorStateList(applicationContext, tintList)
            setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    if (isEnabled) R.color.white else R.color.color_input_text
                )
            )
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
