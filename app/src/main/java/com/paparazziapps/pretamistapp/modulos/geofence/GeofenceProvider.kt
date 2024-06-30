package com.paparazziapps.pretamistapp.modulos.location.geofence

import com.google.android.gms.maps.model.LatLng


interface GeofenceProvider {
    fun createGeofence(
        center: LatLng?,
        radius: Float,
        testLocation: LatLng?,
        callback: GeofenceCallback?
    )
}