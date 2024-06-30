package com.paparazziapps.pretamistapp.modulos.location.geofence


interface GeofenceCallback {
    fun onGeofenceCreated(isSuccess: Boolean)
    fun onLocationChecked(isInside: Boolean)
}