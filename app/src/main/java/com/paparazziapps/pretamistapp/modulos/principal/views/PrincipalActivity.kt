package com.paparazziapps.pretamistapp.modulos.principal.views


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.common.base.Strings.isNullOrEmpty
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityPrincipalBinding
import com.paparazziapps.pretamistapp.databinding.BottomsheetDetallePrestamoBinding
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.getDiasRestantesFromStart
import com.paparazziapps.pretamistapp.helper.getDoubleWithTwoDecimals
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalInUnixtime
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase
import com.paparazziapps.pretamistapp.helper.standardSimpleButton
import com.paparazziapps.pretamistapp.helper.standardSimpleButtonOutline
import com.paparazziapps.pretamistapp.helper.standardSimpleButtonOutlineDisable
import com.paparazziapps.pretamistapp.helper.tintDrawable
import com.paparazziapps.pretamistapp.helper.toJson
import com.paparazziapps.pretamistapp.helper.views.beGone
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.modulos.dashboard.views.HomeFragment.Companion.setOnClickedPrestamoHome
import com.paparazziapps.pretamistapp.modulos.location.LocationManager
import com.paparazziapps.pretamistapp.modulos.location.scheduleLocationUpdates
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelSucursales
import com.paparazziapps.pretamistapp.modulos.login.views.LoginActivity
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences

class PrincipalActivity : AppCompatActivity(){
    private lateinit var binding:ActivityPrincipalBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var layout_detalle_prestamo: BottomsheetDetallePrestamoBinding
    private lateinit var bottomSheetDetallePrestamo: BottomSheetBehavior<ConstraintLayout>
    private var preferences = MyPreferences()

    private var isEnabledCheck = true

    var _viewModelPrincipal = ViewModelPrincipal.getInstance()
    var _viewModelSucursales = ViewModelSucursales.getInstance()

    private lateinit var locationManager: LocationManager

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.navView
        toolbar = binding.tool.toolbar


        MyPreferences().isLogin = true
        isFreeTrial()
        setUpInicialToolbar()
        _viewModelSucursales.getSucursales()
        observers()

        locationManager = LocationManager.getInstance(this)


        // Verificar y solicitar permisos de ubicación si no están concedidos
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)

        if (!isGPSEnabled) {
            showGPSDisabledDialog()
        } else {
            // Inicia el proceso de obtención de ubicación si el GPS está activado
            requestLocationPermissionsAndStartUpdates()
        }

        _viewModelPrincipal.locationUpdateSuccess().observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Ubicación actualizada correctamente", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Error al actualizar la ubicación", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun requestLocationPermissionsAndStartUpdates() {
        // Aquí implementa la lógica para solicitar permisos y comenzar las actualizaciones de ubicación
        // Ejemplo:
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Si los permisos están concedidos, programar actualizaciones de ubicación
            scheduleLocationUpdates(this)
        }
    }

    private fun showGPSDisabledDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("El GPS está desactivado. ¿Deseas activarlo?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()

                showGPSDisabledDialog()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, programar actualizaciones de ubicación
                scheduleLocationUpdates(this)
            } else {
                // Permiso denegado, manejar la situación según sea necesario
                showGPSDisabledDialog()
            }
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    private fun iniciarServiciosDeUbicacion() {
        // Obtener ubicación actual
        locationManager.getCurrentLocation(OnSuccessListener { location ->
            location?.let {
                Toast.makeText(this, "Lat: ${it.latitude}, Lon: ${it.longitude}", Toast.LENGTH_SHORT).show()
            }
        },this)

        // Iniciar el servicio de ubicación
        locationManager.startLocationService(this)

        // Iniciar actualizaciones de ubicación con AlarmManager cada 30 segundos
        locationManager.startLocationUpdates(this)
    }*/

    private fun observers() {

        _viewModelPrincipal.getUser().observe(this){
            println("Info usuario: ${it.superAdmin}")
            preferences.isAdmin = it.admin
            preferences.isSuperAdmin = it.superAdmin
            preferences.sucursalId = it.sucursalId?: INT_DEFAULT
            preferences.sucursalName = it.sucursal?:""
            preferences.email_login = it.email?:""
            preferences.isActiveUser = it.activeUser

            if(it.activeUser) {
                setUpBottomNav()
                setupBottomSheetDetallePrestamo()
                binding.navView.beVisible()

            }else {
                //Usuario desactivado
                binding.navView.beGone()
                isUserActivePrincipal()
            }
        }

        _viewModelSucursales.sucursales.observe(this){
            //save info sucursales
            if(it.isNotEmpty()){
                MyPreferences().sucusales = toJson(it)
            }
            //Get Info user
            _viewModelPrincipal.searchUserByEmail()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_guardar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val btnSave   = menu?.findItem(R.id.action_save_perfil)?.actionView?.findViewById<AppCompatButton>(R.id.btn_save_item)
        val colorState = if (isEnabledCheck) ContextCompat.getColor(this@PrincipalActivity, R.color.red)
        else ContextCompat.getColor(this@PrincipalActivity, R.color.color_text_web)
        val colorStateTxt = ContextCompat.getColor(this@PrincipalActivity, R.color.colorWhite)


        btnSave?.apply {
            isEnabled = isEnabledCheck

            //val resouse = ContextCompat.getDrawable(this@PrincipalActivity, R.drawable.corner_boton_outline) as Drawable
            val resouse = ContextCompat.getDrawable(this@PrincipalActivity, R.drawable.border_mask) as Drawable
            val customResource = tintDrawable(resouse, colorState)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resouse.colorFilter = BlendModeColorFilter(ContextCompat.getColor(this@PrincipalActivity, R.color.colorSecondary), BlendMode.COLOR)
            }else{
                resouse.setColorFilter(ContextCompat.getColor(this@PrincipalActivity, R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP)
            }
            background = customResource

            val resouseDrawable = ContextCompat.getDrawable(this@PrincipalActivity, R.drawable.ic_logout) as Drawable
            val customResourceDrawable = tintDrawable(resouseDrawable, colorStateTxt)

            setCompoundDrawablesWithIntrinsicBounds(customResourceDrawable, null, null, null)
            setTextColor(colorStateTxt)
            setText("Cerrar sessión")
            setOnClickListener {
                if (isEnabledCheck){
                    logout()
                }
            }

        }


        return true
    }

    fun logout(){
        preferences.isLogin = false
        preferences.removeLoginData()
        startActivity(Intent(this@PrincipalActivity, LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun isUserActivePrincipal() {
        binding.cortinaUserInactive.beVisible()
        binding.userInactiveLayout.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun isFreeTrial() {
        if(resources.getBoolean(R.bool.isFreeTrail))
        {
            var fecha7Dias:Long = 1652651285000  // fechaPasado -> 1647147600000 o fechaSuperior -->1649826000000

            if(getFechaActualNormalInUnixtime().minus(fecha7Dias) > 0)
            {
                println("Fecha actual normal: ${getFechaActualNormalInUnixtime().minus(fecha7Dias)}")

            }
        }

    }

    private fun testCrashlytics() {
        throw RuntimeException("Test Crash") // Force a crash
    }

    private fun setupBottomSheetDetallePrestamo() {

        layout_detalle_prestamo = binding.layoutBottomsheetDetallePrestamo


        bottomSheetDetallePrestamo = BottomSheetBehavior.from(layout_detalle_prestamo.root)

        bottomSheetDetallePrestamo.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback()
        {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //Ocultar cortina cuando se oculta bottomsheet
                binding.cortinaBottomSheet.isVisible = newState < 4

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })

        layout_detalle_prestamo.root.setOnClickListener {

            bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun setUpInicialToolbar() {
        toolbar.title = "Dashboard"
        setSupportActionBar(toolbar)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    private fun setUpBottomNav() {

        val navController =findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_registrar, R.id.navigation_finanzas, R.id.navigation_home
            )
        )
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemSelectedListener { item ->

            when(item.itemId)
            {
                R.id.navigation_finanzas -> {
                    println("Mostraste finanzas")
                    navController.navigate(R.id.navigation_finanzas)
                    toolbar.title = "Finanzas"
                    true
                }

                R.id.navigation_home -> {
                    println("Mostraste home")
                    navController.navigate(R.id.navigation_home)
                    toolbar.title = "Dashboard"
                    true
                }

                R.id.navigation_registrar -> {
                    println("Mostraste registrar")
                    navController.navigate(R.id.navigation_registrar)
                    toolbar.title = "Registrar"
                    true
                }

                else -> false
            }

        }
    }


    fun showBottomSheetDetallePrestamoPrincipal(prestamo: Prestamo, montoTotalAPagar: Double, diasRestrasado:String, adapterPosition: Int, needUpdate:Boolean) {
        println("FEcha Unixtime:${getFechaActualNormalInUnixtime()}")

        var diasRestantesPorPagarNuevo:Int?= null
        var diasEnQueTermina = getDiasRestantesFromStart(prestamo.fecha?:"",prestamo.plazo_vto?:0)
        var isClosed:Boolean = false


        //Set inicial bottomsheet
        layout_detalle_prestamo.edtDiasAPagar.apply {
            text?.clear()
            clearFocus()
        }
        layout_detalle_prestamo.apply {
            contentLayoutDiasAPagar.error = null
            btnPagar.apply {
                text = "Actualizar deuda"
                isVisible = false
                standardSimpleButton()
            }
        }
        layout_detalle_prestamo.contenCapitalPrestado.apply {
            if(preferences.isAdmin || preferences.isSuperAdmin) this.beVisible()
            else this.beGone()
        }


        layout_detalle_prestamo.tvMontoDiario.apply {
            text= "S./ ${prestamo.montoDiarioAPagar}"
        }

        layout_detalle_prestamo.tvPlazoPrestamo.text = "${prestamo.plazo_vto.toString()} días"

        //Ocultar vistas si no tiene deudas
        if(prestamo.dias_restantes_por_pagar!! == 0)
        {
            println("Dias restantes por pagar es == a 0 *---> ${prestamo.dias_restantes_por_pagar}")
            //If dias restantes es cero
            layout_detalle_prestamo.apply {
                btnPagar.apply {
                    text = "Cerrar préstamo"
                    isVisible = true
                    isEnabled = true
                    standardSimpleButtonOutline()
                }
                contentDiasAPagar.isVisible = false
                contentPagoTotal.isVisible = false
            }
        }else
        {
            layout_detalle_prestamo.btnPagar.apply {
                this.standardSimpleButtonOutlineDisable()
                isEnabled = false
            }

            if(isNullOrEmpty(montoTotalAPagar.toString()))
            {
                if(montoTotalAPagar.toString().contains("null"))
                {
                    layout_detalle_prestamo.apply {
                        this.contentPagoTotal.isVisible = true
                        this.btnPagar.isVisible = true
                        this.contentLineaExtra.isVisible = true
                        this.contentDiasAPagar.isVisible = true
                    }

                }else {
                    layout_detalle_prestamo.apply {
                        this.contentPagoTotal.isVisible = false
                        this.tvCapitalPrestado.text = "${getString(R.string.tipo_moneda_defecto_cero)}"
                        this.btnPagar.isVisible = false
                        this.contentLineaExtra.isVisible = false
                        this.contentDiasAPagar.isVisible = false
                    }
                }

            }else
            {
                layout_detalle_prestamo.apply {
                    this.contentPagoTotal.isVisible = true
                    this.btnPagar.isVisible = true
                    this.contentLineaExtra.isVisible = true
                    this.contentDiasAPagar.isVisible = true
                }

            }
        }

        layout_detalle_prestamo.tvDiasPagados.text = "${prestamo.diasPagados} días"
        layout_detalle_prestamo.lblNombreCompleto.text = "${replaceFirstCharInSequenceToUppercase(prestamo.nombres?:"")}, ${replaceFirstCharInSequenceToUppercase(prestamo.apellidos?:"")}"
        layout_detalle_prestamo.tvCapitalPrestado.text = "${getString(R.string.tipo_moneda)} ${prestamo.capital}"
        layout_detalle_prestamo.tvInteresPrestado.text = "${prestamo.interes}%"
        layout_detalle_prestamo.tvPlazoVto.text = "en $diasEnQueTermina días"
        layout_detalle_prestamo.tvDiasRetrasados.text = "$diasRestrasado días"
        layout_detalle_prestamo.tvDni.text = "${prestamo.dni}"
        layout_detalle_prestamo.tvFechaPrestamo.text = "${prestamo.fecha}"
        layout_detalle_prestamo.tvMontoTotal.text = "S/. 0.00"



        layout_detalle_prestamo.btnPagar.apply {
            setOnClickListener {
                    //Actualizar en fragment


                    isClosed = text.toString()=="Cerrar préstamo"
                    if(isClosed)
                    {
                        binding.cortinaBottomSheet.isVisible = false
                        bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
                        setOnClickedPrestamoHome?.openDialogoActualizarPrestamo(prestamo,0.0,adapterPosition, 0, 0, isClosed = isClosed)

                    }else{
                        var montoTotalAPagarNuevo = layout_detalle_prestamo.edtDiasAPagar.text.toString().trim().toInt() * prestamo.montoDiarioAPagar!!
                        diasRestantesPorPagarNuevo = prestamo.dias_restantes_por_pagar?.minus(layout_detalle_prestamo.edtDiasAPagar.text.toString().trim().toInt())
                        var diasPagadosNuevo = prestamo.diasPagados?.plus(layout_detalle_prestamo.edtDiasAPagar.text.toString().trim().toInt())
                        binding.cortinaBottomSheet.isVisible = false
                        bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
                        setOnClickedPrestamoHome?.openDialogoActualizarPrestamo(prestamo,montoTotalAPagarNuevo,adapterPosition, diasRestantesPorPagarNuevo?:-9999, diasPagados = diasPagadosNuevo!!, isClosed = isClosed)

                    }


                }
            }



        //Validar
        layout_detalle_prestamo.edtDiasAPagar.doAfterTextChanged {

            layout_detalle_prestamo.contentLayoutDiasAPagar.error = when {
                it.toString().isNullOrEmpty() -> "Los dias deben ser rellenados"
                it.toString().toInt() == 0 -> "Los dias deben ser mayores a 0"
                //it.toString().toInt() in 1..diasRestrasado.toInt() -> "Los dias no deben ser mayores a $diasRestrasado"
                prestamo.dias_restantes_por_pagar!! < it.toString().toInt() -> "Los dias no pueden superar a ${prestamo.dias_restantes_por_pagar}"
                else -> null
            }
            //println("Dias retrasado: ${it.toString().toInt()} ---- >=  ${diasRestrasado}")

            if(!it.toString().isNullOrEmpty() && it.toString().toInt() <= prestamo.dias_restantes_por_pagar?:0)
            {
                layout_detalle_prestamo.btnPagar.apply {
                    this.standardSimpleButtonOutline()
                    isEnabled = true
                }

                layout_detalle_prestamo.tvMontoTotal.text = "S/. ${getDoubleWithTwoDecimals(prestamo.montoDiarioAPagar!!.times(it.toString().toInt()))}"

            }else
            {
                layout_detalle_prestamo.btnPagar.apply {
                    this.standardSimpleButtonOutlineDisable()
                    isEnabled = false
                }
                layout_detalle_prestamo.tvMontoTotal.text = "S/. 0.00"

            }
        }

        //Mostrar bottom sheet
        binding.cortinaBottomSheet.isVisible = true
        bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener el servicio de ubicación
        locationManager.stopLocationService(this)

        // Detener actualizaciones de ubicación con AlarmManager
        locationManager.stopLocationUpdates(this)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

}