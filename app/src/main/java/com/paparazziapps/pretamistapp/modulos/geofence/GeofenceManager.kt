package com.paparazziapps.pretamistapp.modulos.geofence

import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.paparazziapps.pretamistapp.modulos.location.geofence.GeofenceCallback
import com.paparazziapps.pretamistapp.modulos.location.geofence.GeofenceProvider


class GeofenceManager(geofenceProvider: GeofenceProvider) {
    private val geofenceProvider: GeofenceProvider

    init {
        this.geofenceProvider = geofenceProvider
    }

    fun createGeofence(
        center: LatLng?,
        radius: Float,
        testLocation: LatLng?,
        callback: GeofenceCallback?
    ) {
        geofenceProvider.createGeofence(center, radius, testLocation, callback)

       /* geofenceProvider.createGeofence(center, radius, null, object : GeofenceCallback {
            override fun onGeofenceCreated(success: Boolean) {
                // Manejar la creación de la geocerca
                Toast.makeText(requireContext(), " // Manejar la creación de la geocerca", Toast.LENGTH_LONG).show()
            }

            override fun onLocationChecked(isInside: Boolean) {
                // Este método se puede ignorar en este contexto, ya que usaremos el callback de ubicación
            }
        })*/






    }
}

