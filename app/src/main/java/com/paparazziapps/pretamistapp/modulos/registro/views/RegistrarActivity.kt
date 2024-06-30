package com.paparazziapps.pretamistapp.modulos.registro.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegistrarBinding
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.helper.views.beGone
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Client
import com.paparazziapps.pretamistapp.modulos.clientes.viewmodels.ClientsViewModel
import com.paparazziapps.pretamistapp.modulos.clientes.views.adapter.ClientAdapter
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelSucursales
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences
import generatePagaresPdfWithSignature
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class RegistrarActivity : AppCompatActivity() {

    val _viewModel = ViewModelRegister.getInstance()
    val _clientViewModel = ClientsViewModel.getInstance()
    var _viewModelSucursales = ViewModelSucursales.getInstance()

    lateinit var binding: ActivityRegistrarBinding
    var prestamoReceived = Prestamo()
    lateinit var fecha: TextInputEditText
    lateinit var layoutFecha: TextInputLayout
    lateinit var registerButton: MaterialButton
    lateinit var pagareButton: MaterialButton
    lateinit var toolbar: Toolbar
    var fechaSelectedUnixtime: Long? = null

    var preferences = MyPreferences()

    //Sucursales Supér Admin
    var listaSucursales = mutableListOf<Sucursales>()
    lateinit var sucursalTxt: AutoCompleteTextView
    lateinit var sucursalTxtLayout: TextInputLayout
    lateinit var viewProgressSucursal: View
    lateinit var viewCurtainSucursal: View
    lateinit var viewDotsSucursal: View
    private var prestamo: Prestamo? = Prestamo()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val listClients = arrayListOf<Client>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLastLocation()

        fecha = binding.fecha
        registerButton = binding.registrarButton
        pagareButton = binding.pagare
        toolbar = binding.tool.toolbar

        layoutFecha = binding.fechaLayout

        //SuperAdmin
        sucursalTxt = binding.edtSucursal
        sucursalTxtLayout = binding.sucursalTxtInputLyt

        viewProgressSucursal = binding.progressSucursal
        viewDotsSucursal = binding.dotsSucursal
        viewCurtainSucursal = binding.curtainSucursal

        fieldsSuperAdmin()

        //Set max lengh Document
        //  dni.setMaxLength(resources.getInteger(R.integer.cantidad_documento_max))
        // layoutDNI.counterMaxLength = resources.getInteger(R.integer.cantidad_documento_max)

        //get intent
        getExtras()
        showCalendar()
        setupSpinners()
        registerPrestamo()
        sendPagare()
        setUpToolbarInitialize()

        //Observers
        startObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendPagare() {
        pagareButton.setOnClickListener {
            prestamo?.let {
                val deudor = "Jhon jeferson quevedo"
                val acreedor = "${it.nombres} ${it.apellidos}"
                val importe = it.montoTotalAPagar.toString()
                val fechaEmision = getCurrentDate()
                val fechaVencimiento = "${it.dias_restantes_por_pagar} despues de la fecha ${fechaEmision}"
                val phoneNumber = it.celular  // Reemplaza con el número de teléfono deseado

                /* generatePagaresPdf(
                    this,
                    "pagare.pdf",
                    deudor,
                    acreedor,
                    importe,
                    fechaEmision,
                    fechaVencimiento
                )*/

                val signatureDialog = SignatureDialogFragment()
                signatureDialog.show(this.supportFragmentManager, "signatureDialog")
                signatureDialog.onDismissListener = object : SignatureDialogFragment.OnDismissListener {
                    override fun onDismiss(signatureBitmap: Bitmap?) {
                        if (signatureBitmap != null) {
                            val pdfFile = generatePagaresPdfWithSignature(
                                this@RegistrarActivity,
                                "pagare.pdf",
                                deudor,
                                acreedor,
                                importe,
                                fechaEmision,
                                fechaVencimiento,
                                "Santiago de Queretaro",
                                "calle 5 carrera 6",
                                signatureBitmap
                            )
                            sendPdfViaWhatsApp(this@RegistrarActivity, pdfFile, phoneNumber.toString())
                        }
                    }
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    fun sendPdfViaWhatsApp(context: Context, file: File, phoneNumber: String) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(
                "jid",
                "$phoneNumber@s.whatsapp.net"
            ) // Número de teléfono con el formato internacional
            setPackage("com.whatsapp")
        }

        try {
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "WhatsApp no está instalado.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupSpinners() {
        _clientViewModel.getClients { isCorrect, msj, result, isRefresh ->

            if (isCorrect) {
                binding.apply {

                }
            }
        }
        _clientViewModel.clients.observe(this) {

            println("Sucursales Finanzas: $it")

            if (it.isNotEmpty()) {
                it.forEach {
                    listClients.add(it)
                }
            }

        }
        val adapterModos = ClientAdapter(this, listClients)
        binding.dniClient.setAdapter(adapterModos)
        binding.dniClient.setOnClickListener { binding.dniClient.showDropDown() }
        // Manejar la selección de elementos
        binding.dniClient.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as Client
                // Haz algo con el objeto seleccionado
                Toast.makeText(this, "Seleccionado: ${selectedItem.nombres}", Toast.LENGTH_SHORT)
                    .show()
                prestamo?.nombres = selectedItem.nombres
                prestamo?.apellidos = selectedItem.apellidos
                prestamo?.dni = selectedItem.dni
                prestamo?.celular = selectedItem.celular1
                prestamo?.fecha = fecha.text.toString().trim()
                prestamo?.unixtime = fechaSelectedUnixtime
                prestamo?.unixtimeRegistered = getFechaActualNormalInUnixtime()
                prestamo?.capital = prestamoReceived.capital
                prestamo?.interes = prestamoReceived.interes
                prestamo?.plazo_vto = prestamoReceived.plazo_vto
                prestamo?.dias_restantes_por_pagar = prestamoReceived.plazo_vto
                prestamo?.diasPagados = 0
                prestamo?.montoDiarioAPagar = prestamoReceived.montoDiarioAPagar
                prestamo?.montoTotalAPagar = prestamoReceived.montoTotalAPagar
                prestamo?.state = "ABIERTO"

                validateFields()
            }

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

    private fun registerPrestamo() {

        registerButton.apply {

            setOnClickListener {
                hideKeyboardActivity(this@RegistrarActivity)
                isEnabled = false
                binding.cortina.isVisible = true


                prestamo?.fecha = fecha.text.toString().trim()
                var idSucursalSelected: Int = INT_DEFAULT

                listaSucursales.forEach {
                    if (it.name?.equals(
                            sucursalTxt.text.toString().trim()
                        ) == true
                    ) idSucursalSelected = it.id ?: INT_DEFAULT
                }

                //Register ViewModel
                //Actualizar el idSucursal para crear un prestamo como superAdmin
                prestamo?.let {

                    _viewModel.createPrestamo(
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

                //Fin click listener
            }


        }
    }


    private fun validateFields() {

        fecha.doAfterTextChanged {
            showbutton()
        }

        binding.dniClient.doAfterTextChanged {
            val nombresChanged = it.toString()
            binding.dniClient.error = when {
                nombresChanged.isEmpty() -> "El nombre esta vacío"
                nombresChanged.count() < 4 -> "El nombre esta incompleto"
                else -> null
            }
            showbutton()
        }


    }

    private fun showbutton() {

        if (binding.dniClient.text.toString().equals(prestamo?.nombres) &&
            fecha.text.toString().trim().isNotEmpty()
        ) {
            //Registrar prestamo
            registerButton.apply {
                isEnabled = true
                backgroundTintMode = PorterDuff.Mode.SCREEN
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            pagareButton.apply {
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
            pagareButton.apply {
                isEnabled = false
                backgroundTintMode = PorterDuff.Mode.MULTIPLY
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.color_input_text)
                setTextColor(ContextCompat.getColor(context, R.color.color_input_text))
            }

        }
    }


    private fun showCalendar() {
        binding.fechaLayout.setEndIconOnClickListener {
            getCalendar()
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun getCalendar() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleciona una fecha")
                .build()

        datePicker.show(supportFragmentManager, "Datepickerdialog");

        datePicker.addOnPositiveButtonClickListener {
            println("UnixTime: ${it}")
            fechaSelectedUnixtime = it
            SimpleDateFormat("dd/MM/yyyy").apply {
                timeZone = TimeZone.getTimeZone("GMT")
                format(it).toString().also {
                    binding.fecha.setText(it)
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
                            prestamo?.let {
                                it.coordenada = "$latitude,$longitude"
                            }
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


    private fun getExtras() {

        if (intent.extras != null) {
            var extras = intent.getStringExtra("prestamoJson")
            var gson = Gson()

            if (!extras.isNullOrEmpty()) {
                prestamoReceived = gson.fromJson(extras, Prestamo::class.java)
                binding.interes.setText("${prestamoReceived.interes!!.toInt()}%")
                binding.capital.setText("${getString(R.string.tipo_moneda)} ${prestamoReceived.capital!!.toInt()}")
                binding.plazosEnDias.setText("${prestamoReceived.plazo_vto.toString()} dias")
            }
        }

    }

    override fun onDestroy() {
        prestamo = null
        ViewModelRegister.destroyInstance()
        super.onDestroy()
    }

}