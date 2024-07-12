package com.paparazziapps.pretamistapp.modulos.collection

import LoanViewModel
import android.app.AlertDialog
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentInProgressCollectionBinding
import com.paparazziapps.pretamistapp.factory.LoanViewModelFactory
import com.paparazziapps.pretamistapp.modulos.geofence.GeofenceManager
import com.paparazziapps.pretamistapp.modulos.geofence.GoogleGeofenceProvider
import com.paparazziapps.pretamistapp.modulos.location.LocationManager
import com.paparazziapps.pretamistapp.modulos.location.geofence.GeofenceCallback
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider

class InProgressCollectionFragment : Fragment() {

    private var _binding: FragmentInProgressCollectionBinding? = null
    private val binding get() = _binding!!
    private var locationManager: LocationManager? = null
    private var geofenceCenter: LatLng? = null
    private var viewModel: LoanViewModel? = null
    private var currenLocationClient: LatLng? = null
    var geofenceProvider: GoogleGeofenceProvider? = null
    var geofenceManager: GeofenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInProgressCollectionBinding.inflate(inflater, container, false)


        viewModel = ViewModelProvider(
            this,
            LoanViewModelFactory(PrestamoProvider())
        ).get(LoanViewModel::class.java)
        viewModel?.loadCurrentLoan()

        locationManager = LocationManager.getInstance(requireContext())
        geofenceProvider = GoogleGeofenceProvider(requireContext())
        geofenceProvider?.let {
            geofenceManager = GeofenceManager(it)
        }

        observers()
        _binding?.btnInPlace?.setOnClickListener {
            findNavController().navigate(R.id.action_inProgressCollectionFragment_to_incidentManagementScreenFragment)
            /*locationManager?.getCurrentLocation(requireContext()) { location ->
                location?.let {
                    checkLocationInGeofence(LatLng(location.latitude,location.longitude))
                }
            }*/
        }



        return binding.root
    }

    private fun observers() {
       /* viewModel?.currentLoan?.observe(viewLifecycleOwner) { client ->
            if (client != null) {
                val lat = client.coordenada?.split(",")?.get(0)?.toDouble() ?: 0.0
                val long = client.coordenada?.split(",")?.get(1)?.toDouble() ?: 0.0
                currenLocationClient = LatLng(lat, long)
                currenLocationClient?.let {
                    createGeofence(it)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    " //  Problemas con  el resultado del servicio",
                    Toast.LENGTH_LONG
                ).show()
            }
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        geofenceManager?.destroy()
        _binding = null
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Alerta")
            .setMessage("Aún no se encuentra en el sitio")
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun createGeofence(geofenceCenter: LatLng) {
        // Crear una geocerca y dibujarla en el mapa
        this.geofenceCenter = geofenceCenter
        var geofenceRadius = 5000f

        geofenceManager?.createGeofence(
            geofenceCenter,
            geofenceRadius,
            null,
            object : GeofenceCallback {
                override fun onGeofenceCreated(success: Boolean) {
                    // Manejar la creación de la geocerca
                  /*  Toast.makeText(
                        requireContext(),
                        " // Manejar la creación de la geocerca",
                        Toast.LENGTH_LONG
                    ).show()*/
                }

                override fun onLocationChecked(isInside: Boolean) {
                    // Este método se puede ignorar en este contexto, ya que usaremos el callback de ubicación
                    Toast.makeText(
                        requireContext(),
                        " //  Este método se puede ignorar en este contexto, ya que usaremos el callback de ubicación",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun checkLocationInGeofence(location: LatLng) {
        geofenceCenter?.let { center ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                center.latitude,
                center.longitude,
                location.latitude,
                location.longitude,
                distance
            )
            val isInside = distance[0] <= 45F

            onLocationChecked(isInside)
        }
    }

    private fun onLocationChecked(isInside: Boolean) {
        if (isInside) {
            findNavController().navigate(R.id.action_inProgressCollectionFragment_to_incidentManagementScreenFragment)

            //   findNavController().navigate(R.id.action_inProgressCollectionFragment_to_incidentManagementScreenFragment)
        } else {
            showAlertDialog()
        }
    }


}
