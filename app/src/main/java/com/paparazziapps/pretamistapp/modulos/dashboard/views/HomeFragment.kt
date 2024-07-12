package com.paparazziapps.pretamistapp.modulos.dashboard.views

import CustomInfoWindowAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.DialogSalirSinGuardarBinding
import com.paparazziapps.pretamistapp.databinding.FragmentHomeBinding
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.MapLocationAnimator
import com.paparazziapps.pretamistapp.helper.fromHtml
import com.paparazziapps.pretamistapp.helper.fromJson
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalCalendar
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase
import com.paparazziapps.pretamistapp.helper.showMessageAboveBottomMenuGlobal
import com.paparazziapps.pretamistapp.modulos.clientes.views.RegistrarClienteActivity
import com.paparazziapps.pretamistapp.modulos.dashboard.adapters.PrestamoAdapter
import com.paparazziapps.pretamistapp.modulos.dashboard.interfaces.setOnClickedPrestamo
import com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.modulos.geofence.GeofenceManager
import com.paparazziapps.pretamistapp.modulos.geofence.GoogleGeofenceProvider
import com.paparazziapps.pretamistapp.modulos.location.LocationManager
import com.paparazziapps.pretamistapp.modulos.location.geofence.GeofenceCallback
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.pojo.User
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Credit
import com.paparazziapps.pretamistapp.modulos.registro.pojo.TypePrestamo
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences


class HomeFragment : Fragment(), setOnClickedPrestamo, OnMapReadyCallback {

    var _viewModel = ViewModelDashboard.getInstance()
    var _viewModelPrincipal = ViewModelPrincipal.getInstance()
    private lateinit var mMap: GoogleMap
    var locationManager: LocationManager? = null
    private var locationMarker: Marker? = null
    private var testLocationMarker: Marker? = null
    private var geofenceCenter: LatLng? = null
    var _binding: FragmentHomeBinding? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private val binding get() = _binding!!

    var preferences = MyPreferences()

    private lateinit var googleMap: GoogleMap

    //constructores
    val prestamoAdapter = PrestamoAdapter(this)

    val coordenadas = arrayListOf<Credit>()

    private lateinit var recyclerPrestamos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        locationManager = LocationManager.getInstance(requireContext())

        setOnClickedPrestamoHome = this

        //Link items with layout
        recyclerPrestamos = binding.recyclerPrestamos

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        //Configuration
        setupRecyclerPrestamos()
        //Observers
        observers()
        getInforUser()
        actionListening()

        _binding?.let {
            bottomSheetBehavior = BottomSheetBehavior.from(it.playerBottomSheetFragment)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return view
    }


    private fun actionListening() {
        binding.actionLocation.setOnClickListener {

        }
        binding.actionAdd.setOnClickListener {
            startForResult.launch(Intent(context, RegistrarClienteActivity::class.java))
        }
        binding.actionGoToClient.setOnClickListener {
            val latitude = 40.7128  // Reemplaza con la latitud deseada
            val longitude = -74.0060  // Reemplaza con la longitud deseada
            val uri = "google.navigation:q=$latitude,$longitude"
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
                setPackage("com.google.android.apps.maps")
            }
            try {
                startActivity(mapIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Google Maps no está instalado.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.actionWhatsappClient.setOnClickListener {
            val phoneNumber = "1234567890"  // Reemplaza con el número de teléfono deseado
            val message = "Hola, ¿cómo estás?"  // Reemplaza con el mensaje deseado
            val uri = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
            val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(uri)
            }
            try {
                startActivity(sendIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "WhatsApp no está instalado.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.actionPhoneClient.setOnClickListener {
            val phoneNumber = "tel:1234567890"  // Reemplaza con el número de teléfono deseado
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse(phoneNumber)
            }
            startActivity(callIntent)
        }

    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val msj = it.data?.getStringExtra("mensaje")
                // Handle the Intent
                println("Resultado de actividad: $msj")
                showMessage(msj ?: "")
            } else {
                println("Resultado de actividad--> null")
            }

        }


    private fun setupRecyclerPrestamos() {
        recyclerPrestamos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = prestamoAdapter
        }
    }

    private fun getInforUser() {
        _viewModelPrincipal.searchUserByEmail()
    }


    private fun observers() {
        _viewModelPrincipal.getUser().observe(viewLifecycleOwner, Observer(::updateUser))
        _viewModel.receivePrestamos().observe(viewLifecycleOwner, Observer(::updatePrestamos))
    }

    fun updatePrestamos(prestamosAll: MutableList<Credit>) {
        if (prestamosAll.isEmpty()) {
            binding.emptyPrestamo.isVisible = true
            recyclerPrestamos.isVisible = false
            //showMessage("No hay prestamos")
        } else {

            updateListLocation(prestamosAll)
            //Recibes todos los prestamos
            binding.emptyPrestamo.isVisible = false

            if (MyPreferences().isSuperAdmin) {
                val sucurs = MyPreferences().sucusales
                if (sucurs.isEmpty()) {
                    prestamoAdapter.setData(prestamosAll)
                } else {
                    try {
                        val newPrestamos = mutableListOf<Credit>()
                        val localSucursales = fromJson<List<Sucursales>>(sucurs)
                        localSucursales.forEach { sucurlocal ->
                            val item = Credit(
                                type = TypePrestamo.TITLE.value,
                                title = sucurlocal.name
                            )
                            newPrestamos.add(item)

                            val items = prestamosAll.filter {
                                it.sucursalId == sucurlocal.id
                            }
                            newPrestamos.addAll(items)
                        }
                        prestamoAdapter.setData(newPrestamos)

                    } catch (t: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(t)
                    }
                }
            } else {
                prestamoAdapter.setData(prestamosAll)
            }


            recyclerPrestamos.isVisible = true
            //showMessage(" Lista de prestamos ${it.count()}")
        }
    }

    private fun updateListLocation(prestamosAll: MutableList<Credit>) {
        coordenadas.clear()
        prestamosAll.forEach {
            coordenadas.add(it)
        }
        updateMapWithCoordinates(coordenadas)
    }

    fun updateUser(it: User) {
        println("Info usuario: ${it.superAdmin}")
        preferences.isAdmin = it.admin
        preferences.isSuperAdmin = it.superAdmin
        preferences.sucursalId = it.sucursalId ?: INT_DEFAULT
        preferences.sucursalName = it.sucursal ?: ""
        preferences.email_login = it.email ?: ""
        preferences.isActiveUser = it.activeUser

        if (it.activeUser) {
            _viewModel.getPrestamos()
        }
    }

    private fun showMessage(message: String) {
        showMessageAboveBottomMenuGlobal(message, binding.coordinator)
    }


    companion object {

        var setOnClickedPrestamoHome: setOnClickedPrestamo? = null


    }

    override fun onDestroy() {

        ViewModelRegister.destroyInstance()
        ViewModelDashboard.destroyInstance()
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    //->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Metodos override
    override fun actualizarPagoPrestamo(
        prestamo: Credit,
        needUpdate: Boolean,
        montoTotalAPagar: Double,
        adapterPosition: Int,
        diasRestrasado: String
    ) {

        //println("Hizo click en Actualizar Pago Prestamos")
        context.apply {
            (this as PrincipalActivity).showBottomSheetDetallePrestamoPrincipal(
                prestamo,
                montoTotalAPagar,
                diasRestrasado,
                adapterPosition,
                needUpdate
            )
        }


    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun openDialogoActualizarPrestamo(
        credit: Credit,
        montoTotalAPagar: Double,
        adapterPosition: Int,
        diasRestantesPorPagar: Int,
        diasPagados: Int,
        isClosed: Boolean
    ) {

        binding.cntCortina.isVisible = true

        val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialogBackground)
        val view: View = layoutInflater.inflate(R.layout.dialog_salir_sin_guardar, null)
        val bindingDialogSalir = DialogSalirSinGuardarBinding.bind(view)

        val title = bindingDialogSalir.textView
        val desc = bindingDialogSalir.lblDescSalirNoticias
        val btnPositive = bindingDialogSalir.btnAceptarSalir
        val btnNegative = bindingDialogSalir.btnCancelarSalir

        if (isClosed) {
            title.text = "¿Estas seguro de cerrar el préstamo?"
            desc.text =
                ("Se cerrára el préstamo de: <b>${replaceFirstCharInSequenceToUppercase("julian")}, ${
                    replaceFirstCharInSequenceToUppercase("lopezx castellanos")
                }").fromHtml()

        } else {
            title.text = "¿Estas seguro de actualizar la deuda?"
            desc.text =
                ("Se actualizará la deuda de: <b>${replaceFirstCharInSequenceToUppercase("julian")}, ${
                    replaceFirstCharInSequenceToUppercase("lopezcastellanos")
                } </b>" +
                        ",con un monto total a pagar de: <br><b>${getString(R.string.tipo_moneda)}${montoTotalAPagar}<b>").fromHtml()
        }

        dialogBuilder.apply {
            setView(view)
        }

        val dialog = dialogBuilder.create()
        dialog.apply {
            setCanceledOnTouchOutside(false)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setGravity(Gravity.CENTER)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setOnDismissListener {
                binding.cntCortina.visibility = View.GONE
            }
            show()
        }


        btnPositive.apply {
            visibility = View.VISIBLE
            setOnClickListener {


                //(context as PrincipalActivity).showCortinaPrincipal(true)

                dialog.dismiss()

                if (isClosed) {
                    _viewModel.cerrarPrestamo(credit.id) { isCorrect, msj, result, isRefresh ->
                        if (isCorrect) {
                            prestamoAdapter.removeItem(adapterPosition)//remover item de  local recycler View
                            showMessage(msj)

                        } else {
                            //(context as PrincipalActivity).showCortinaPrincipal(false)
                            showMessage(msj)
                        }
                    }
                } else {
                    _viewModel.updateUltimoPago(
                        credit.id,
                        getFechaActualNormalCalendar(),
                        montoTotalAPagar,
                        diasRestantesPorPagar,
                        diasPagados
                    ) { isCorrect, msj, result, isRefresh ->

                        if (isCorrect) {
                            credit.lastPaymentDate = getFechaActualNormalCalendar()
                            credit.daysRemainingToPay = diasRestantesPorPagar.toString()
                            credit.daysPaid = diasPagados.toString()
                            //(context as PrincipalActivity).showCortinaPrincipal(false)
                            prestamoAdapter.updateItem(
                                adapterPosition,
                                credit
                            )//Actualizar local recycler View
                            showMessage(msj)

                        } else {
                            //(context as PrincipalActivity).showCortinaPrincipal(false)
                            showMessage(msj)
                        }

                    }
                }


            }
        }

        btnNegative.apply {
            visibility = View.VISIBLE
            isAllCaps = false
            setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    // Método para agregar marcadores en el mapa


    fun updateMapWithCoordinates(coordinates: ArrayList<Credit>) {
        if (::mMap.isInitialized) {
            mMap.let {


                it.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater))

                val client = coordinates[0]

              /*  val lat = client.coordenada?.split(",")?.get(0)?.toDouble() ?: 0.0
                val long = client.coordenada?.split(",")?.get(1)?.toDouble() ?: 0.0
                val latLng = LatLng(lat, long)

                MapLocationAnimator(googleMap, requireContext()).startPulseAnimation(latLng)
                //   mapPulseAnimator.startPulseAnimation(latLng)

               // createGeofence(latLng)
                //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                it.addMarker(

                    MarkerOptions().position(latLng)
                        .title(Gson().toJson(client))
                )

                if (coordinates.isNotEmpty()) {
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }*/


            }
        }
    }

    private fun createGeofence(geofenceCenter: LatLng) {
        val geofenceProvider = GoogleGeofenceProvider(requireContext())
        val geofenceManager = GeofenceManager(geofenceProvider)

        // Crear una geocerca y dibujarla en el mapa
        this.geofenceCenter = geofenceCenter
        var geofenceRadius = 5000f

        geofenceManager.createGeofence(
            geofenceCenter,
            geofenceRadius,
            null,
            object : GeofenceCallback {
                override fun onGeofenceCreated(success: Boolean) {
                    // Manejar la creación de la geocerca
                    Toast.makeText(
                        requireContext(),
                        " // Manejar la creación de la geocerca",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onLocationChecked(isInside: Boolean) {
                    // Este método se puede ignorar en este contexto, ya que usaremos el callback de ubicación
                }
            })
    }


    override fun onMapReady(map: GoogleMap) {
        mMap = map
        // Asigna el adaptador personalizado al mapa
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater))

        mMap.let {
            googleMap = it
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL


            // Iniciar actualizaciones de ubicación
            locationManager?.startLocationUpdates { location ->
                updateLocationOnMap(location)
               // checkLocationInGeofence(location)
            }

        }
    }

    private fun checkLocationInGeofence(location: Location) {
        geofenceCenter?.let { center ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                center.latitude,
                center.longitude,
                location.latitude,
                location.longitude,
                distance
            )
            val isInside = distance[0] <= 70F

            // Llamar a onLocationChecked
            onLocationChecked(isInside)
        }
    }

    private fun onLocationChecked(isInside: Boolean) {
        if (isInside) {
            geofenceCenter?.let { center ->
                if (testLocationMarker == null) {
                    testLocationMarker = mMap.addMarker(
                        MarkerOptions().position(center)
                    )
                } else {
                    testLocationMarker?.position = center
                }
            }
            Toast.makeText(
                requireContext(),
                "La ubicación está dentro de la geocerca",
                Toast.LENGTH_LONG
            ).show()
        } else {
            // La ubicación está fuera de la geocerca
            Toast.makeText(
                requireContext(),
                "La ubicación está fuera de la geocerca",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateLocationOnMap(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)

        // Si el marcador ya existe, actualiza su posición
        if (locationMarker == null) {
            locationMarker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Ubicación Actual")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            ) // Cambia el color del marcador aquí

        } else {
            locationMarker?.position = latLng
        }

        // Mueve la cámara a la nueva ubicación

    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


}