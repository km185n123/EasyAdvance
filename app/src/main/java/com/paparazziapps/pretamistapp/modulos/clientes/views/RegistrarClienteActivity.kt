package com.paparazziapps.pretamistapp.modulos.clientes.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegistrarClienteBinding
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.hideKeyboardActivity
import com.paparazziapps.pretamistapp.helper.setMaxLength
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Client
import com.paparazziapps.pretamistapp.modulos.clientes.viewmodels.ClientsViewModel
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelSucursales
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences


class RegistrarClienteActivity : AppCompatActivity() {

    val _viewModel = ClientsViewModel.getInstance()
    var _viewModelSucursales = ViewModelSucursales.getInstance()

    lateinit var binding: ActivityRegistrarClienteBinding

    lateinit var nombres: TextInputEditText
    lateinit var layoutNombres: TextInputLayout
    lateinit var apellidos: TextInputEditText
    lateinit var layoutApellidos: TextInputLayout
    lateinit var dni: TextInputEditText
    lateinit var layoutDNI: TextInputLayout
    lateinit var celular: TextInputEditText
    lateinit var layoutCelular: TextInputLayout
    lateinit var layoutEdad: TextInputLayout
    lateinit var celularReferencia: TextInputLayout
    lateinit var direccion: TextInputLayout
    lateinit var direccionReferencia: TextInputLayout
    lateinit var ubicacion: TextInputLayout

    lateinit var registerButton: MaterialButton
    lateinit var toolbar: Toolbar

    var preferences = MyPreferences()
    var client: Client? = Client()

    //Sucursales Supér Admin
    var listaSucursales = mutableListOf<Sucursales>()
    lateinit var sucursalTxt: AutoCompleteTextView
    lateinit var sucursalTxtLayout: TextInputLayout
    lateinit var viewProgressSucursal: View
    lateinit var viewCurtainSucursal: View
    lateinit var viewDotsSucursal: View

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLastLocation()

        //  fechaSelectedUnixtime = Date().time.toString()
        nombres = binding.nombres
        apellidos = binding.apellidos
        dni = binding.dni
        celular = binding.celular
        registerButton = binding.registrarClientButton
        toolbar = binding.tool.toolbar

        layoutNombres = binding.nombresLayout
        layoutApellidos = binding.apellidosLayout
        layoutDNI = binding.dniLayout
        layoutCelular = binding.celularLayout
        layoutEdad = binding.edadLayout
        celularReferencia = binding.celularReferenciaLayout
        direccion = binding.direccionLayout
        direccionReferencia = binding.direccionReferenciaLayout
        ubicacion = binding.ubicacionLayout

        //SuperAdmin
        sucursalTxt = binding.edtSucursal
        sucursalTxtLayout = binding.sucursalTxtInputLyt

        viewProgressSucursal = binding.progressSucursal
        viewDotsSucursal = binding.dotsSucursal
        viewCurtainSucursal = binding.curtainSucursal

        fieldsSuperAdmin()

        //Set max lengh Document
        dni.setMaxLength(resources.getInteger(R.integer.cantidad_documento_max))
        layoutDNI.counterMaxLength = resources.getInteger(R.integer.cantidad_documento_max)

        //get intent
        //getExtras()
        validateFields()
        setUpToolbarInitialize()
        registerClient()
        //Observers
        startObservers()
    }


    private fun fieldsSuperAdmin() {
        if (preferences.isSuperAdmin) {
            sucursalTxtLayout.beVisible()
            viewProgressSucursal.beVisible()
            _viewModelSucursales.getSucursales()
        }
    }

    private fun startObservers() {
        _viewModel.getMessage().observe(this) { message -> showMessage(message) }

        _viewModelSucursales.sucursales.observe(this) {
            if (it.isNotEmpty()) {
                listaSucursales = it.toMutableList()
                var scrsales = mutableListOf<String>()
                it.forEach {
                    scrsales.add(it.name ?: "")
                }

                val adapterSucursales = ArrayAdapter(this, R.layout.select_items, scrsales)
                sucursalTxt.setAdapter(adapterSucursales)
                sucursalTxt.setOnClickListener { sucursalTxt.showDropDown() }
                sucursalTxtLayout.setEndIconOnClickListener { sucursalTxt.showDropDown() }

                viewProgressSucursal.isVisible = false
                viewDotsSucursal.isVisible = false
                viewCurtainSucursal.isVisible = false
            }
        }
    }

    private fun showMessage(message: String?) {
        Snackbar.make(binding.root, "${message}", Snackbar.LENGTH_SHORT).show()
    }

    private fun setUpToolbarInitialize() {
        toolbar.title = "Registrar"
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun validateFields() {

        nombres.doAfterTextChanged {

            var nombresChanged = it.toString()

            layoutNombres.error = when {
                nombresChanged.isNullOrEmpty() -> "El nombre esta vacío"
                nombresChanged.count() < 4 -> "El nombre esta incompleto"
                else -> null
            }
            showbutton()

        }

        apellidos.doAfterTextChanged {

            var apellidosChanged = it.toString()

            layoutApellidos.error = when {
                apellidosChanged.isNullOrEmpty() -> "Los apellidos estan vacíos"
                apellidosChanged.count() < 4 -> "Los apellidos estan incompletos"
                else -> null
            }
            showbutton()

        }

        dni.doAfterTextChanged {

            var doucmentoChanged = it.toString()
            var documentoMax = resources.getInteger(R.integer.cantidad_documento_max)

            layoutDNI.error = when {
                doucmentoChanged.isNullOrEmpty() -> "${getString(R.string.documento_vacío)}"
                doucmentoChanged.count() in 1 until documentoMax -> "${getString(R.string.documento_incompleto)}"
                else -> null
            }

            showbutton()

        }

        celular.doAfterTextChanged {

            var celularChanged = it.toString()

            layoutCelular.error = when {
                celularChanged.isNullOrEmpty() -> "Celular vacío"
                celularChanged.count() in 1..8 -> "Celular incompleto"
                else -> null
            }

            showbutton()

        }


    }

    private fun showbutton() {

        if (!nombres.text.toString().trim().isNullOrEmpty() &&
            nombres.text.toString().trim().count() >= 4 &&
            !apellidos.text.toString().trim().isNullOrEmpty() &&
            apellidos.text.toString().trim().count() >= 4 &&
            !celular.text.toString().trim().isNullOrEmpty() &&
            celular.text.toString().trim().count() == 9 &&
            !dni.text.toString().trim().isNullOrEmpty() &&
            dni.text.toString().trim()
                .count() == resources.getInteger(R.integer.cantidad_documento_max)
        // && !fecha.text.toString().trim().isNullOrEmpty()
        ) {
            //Registrar prestamo
            registerButton.apply {
                isEnabled = true
                backgroundTintMode = PorterDuff.Mode.SCREEN
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        } else {
            registerButton.apply {
                isEnabled = false
                backgroundTintMode = PorterDuff.Mode.MULTIPLY
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.color_input_text)
                setTextColor(ContextCompat.getColor(context, R.color.color_input_text))
            }

        }
    }

    private fun registerClient() = binding.apply {

        registerButton.apply {

            setOnClickListener {

                hideKeyboardActivity(this@RegistrarClienteActivity)
                isEnabled = false
                binding.cortina.isVisible = true

                with(client) {
                    this?.nombres = nombres.text.toString().trim()
                    this?.apellidos = apellidos.text.toString().trim()
                    this?.dni = dni.text.toString().trim()
                    this?.celular1 = celular.text.toString().trim()
                    this?.celular2 = celular2.text.toString().trim()
                    this?.edad = edad.text.toString().trim()
                    this?.direccion = direccion1.text.toString().trim()
                    this?.direccion2 = direccionReferencia.text.toString().trim()
                }



                var idSucursalSelected: Int = INT_DEFAULT

                listaSucursales.forEach {
                    if (it.name.equals(
                            sucursalTxt.text.toString().trim()
                        )
                    ) idSucursalSelected = it.id ?: INT_DEFAULT
                }

                //Register ViewModel
                //Actualizar el idSucursal para crear un prestamo como superAdmin
                client?.let {
                    _viewModel.createClient(
                        it,
                        idSucursal = idSucursalSelected
                    ) { isCorrect, msj, result, isRefresh ->

                        if (isCorrect) {
                            //showMessage(msj)
                            intent.putExtra("mensaje", msj)
                            setResult(RESULT_OK, intent)
                            finish()

                        } else {
                            binding.cortina.isVisible = false
                            isEnabled = true
                        }
                    }
                }

            }
        }
    }

    private fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            Log.d("MainActivity", "Lat: $latitude, Lon: $longitude")
                            client?.coordenada = "$latitude,$longitude"
                            Toast.makeText(
                                this,
                                "Lat: $latitude, Lon: $longitude",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Log.d("MainActivity", "No se pudo obtener la ubicación.")
                        }
                    }
                    .addOnFailureListener {
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
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (isLocationEnabled()) {
                    getLastLocation()
                } else {
                    promptEnableLocation()
                }
            } else {
                // Permiso denegado, manejar la negación de permisos
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        client = null
        ClientsViewModel.destroyInstance()
        super.onDestroy()
    }

}

