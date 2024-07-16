package com.paparazziapps.pretamistapp.modulos.clientes.views

import ClientsViewModel
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegistrarClienteBinding
import com.paparazziapps.pretamistapp.helper.hideKeyboardActivity
import com.paparazziapps.pretamistapp.helper.setMaxLength
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.modulos.clientes.data.pojo.Client
import com.paparazziapps.pretamistapp.modulos.clientes.viewmodels.ClientViewModel
import com.paparazziapps.pretamistapp.modulos.login.data.model.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelSucursales
import com.paparazziapps.pretamistapp.modulos.login.views.LoginActivity
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class RegistrarClienteActivity : AppCompatActivity() {

  //  private val _viewModelSucursales = ViewModelSucursales.getInstance()
    private val viewModelClient: ClientViewModel by viewModels()
    private lateinit var binding: ActivityRegistrarClienteBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private var client: Client? = Client()
    private val preferences = MyPreferences()
    private val listaSucursales = mutableListOf<Sucursales>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        initView()
        validateFields()
        setupObservers()
        getLastLocation()
        setupRegisterButton()
    }

    private fun setupToolbar() {
        binding.tool.toolbar.apply {
            title = "Registrar"
            setNavigationOnClickListener { finish() }
        }
    }

    private fun initView() {
        binding.apply {
            nombres.setMaxLength(resources.getInteger(R.integer.cantidad_documento_max))
            dni.setMaxLength(resources.getInteger(R.integer.cantidad_documento_max))
        }

        if (preferences.isSuperAdmin) {
            binding.apply {
                sucursalTxtInputLyt.beVisible()
                progressSucursal.beVisible()
              //  _viewModelSucursales.getSucursales()
            }
        }
    }

    private fun validateFields() = binding.apply {
        nombres.doAfterTextChanged { updateFieldError(nombresLayout, it.toString(), 4) }
        apellidos.doAfterTextChanged { updateFieldError(apellidosLayout, it.toString(), 4) }
        dni.doAfterTextChanged {
            updateFieldError(
                dniLayout,
                it.toString(),
                resources.getInteger(R.integer.cantidad_documento_max)
            )
        }
        celular.doAfterTextChanged { updateFieldError(celularLayout, it.toString(), 10) }
    }


    private fun updateFieldError(layout: TextInputLayout, text: String, minLength: Int) {
        layout.error = when {
            text.isEmpty() -> "${layout.hint} está vacío"
            text.length < minLength -> "${layout.hint} está incompleto"
            else -> null
        }
        updateRegisterButtonState()
    }

    private fun updateRegisterButtonState() {
        binding.apply {
            val isFormValid = !nombres.text.isNullOrEmpty() && nombres.text!!.length >= 4 &&
                    !apellidos.text.isNullOrEmpty() && apellidos.text!!.length >= 4 &&
                    !dni.text.isNullOrEmpty() && dni.text!!.length == resources.getInteger(R.integer.cantidad_documento_max) &&
                    !celular.text.isNullOrEmpty() && celular.text!!.length == 10
            registrarClientButton.isEnabled = isFormValid
        }
    }

    private fun setupObservers() {
       /* _viewModelSucursales.sucursales.observe(this) { sucursales ->
            if (sucursales.isNotEmpty()) {
                listaSucursales.clear()
                listaSucursales.addAll(sucursales)
                val sucursalNames = sucursales.map { it.name ?: "" }
                val adapter = ArrayAdapter(this, R.layout.select_items, sucursalNames)
                binding.edtSucursal.apply {
                    setAdapter(adapter)
                    setOnClickListener { showDropDown() }
                }
                binding.sucursalTxtInputLyt.setEndIconOnClickListener { binding.edtSucursal.showDropDown() }
                binding.apply {
                    progressSucursal.isVisible = false
                    dotsSucursal.isVisible = false
                    curtainSucursal.isVisible = false
                }
            }
        }*/

        viewModelClient.message.observe(this) { message ->
            message?.let { showMessage(it) }
        }

        viewModelClient.isLoading.observe(this) { isLoading ->
            isLoading?.let {
                binding.cortina.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        viewModelClient.success.observe(this) {
           finish()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRegisterButton() = binding.apply {
        registrarClientButton.setOnClickListener {
            hideKeyboardActivity(this@RegistrarClienteActivity)
            curtainSucursal.isVisible = true

            val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

            client?.let {
                it.createdAt = currentDate
                it.name = binding.nombres.text.toString().trim()
                it.lastName = binding.apellidos.text.toString().trim()
                it.dni = binding.dni.text.toString().trim()
                it.phone = binding.celular.text.toString().trim()
                it.phone2 = binding.celular2.text.toString().trim()
                it.age = binding.edad.text.toString().trim().toIntOrNull()
                it.address = binding.direccion1.text.toString().trim()
                it.address2 = binding.direccionReferencia.text.toString().trim()
                it.occupation = "prueba"
                it.businessName = "prueba"
                it.branchId = 1L
                it.score = 100
                viewModelClient.onCreateClient(it)
            }
        }
    }

    private fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        Log.d("MainActivity", "Lat: $latitude, Lon: $longitude")
                        client?.coordinate = "$latitude,$longitude"
                        Toast.makeText(this, "Lat: $latitude, Lon: $longitude", Toast.LENGTH_LONG)
                            .show()
                    } ?: Log.d("MainActivity", "No se pudo obtener la ubicación.")
                }.addOnFailureListener {
                    Log.e("MainActivity", "Error al obtener la ubicación.")
                }
            } else {
                promptEnableLocation()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun promptEnableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    getLastLocation()
                } else {
                    promptEnableLocation()
                }
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showMessage(message: String?) {
        Snackbar.make(binding.root, message.orEmpty(), Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        client = null
        ClientsViewModel.destroyInstance()
        super.onDestroy()
    }
}
