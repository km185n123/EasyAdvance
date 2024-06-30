package com.paparazziapps.pretamistapp.modulos.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.paparazziapps.pretamistapp.modulos.location.geofence.GeofenceCallback
import com.paparazziapps.pretamistapp.modulos.location.geofence.GeofenceProvider

class GoogleGeofenceProvider(private val context: Context, private val googleMap: GoogleMap) : GeofenceProvider {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private var geofencePendingIntent: PendingIntent? = null
        private get() {
            if (field != null) {
                return field
            }
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            field = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return field
        }

    override fun createGeofence(
        center: LatLng?,
        radius: Float,
        testLocation: LatLng?,
        callback: GeofenceCallback?
    ) {
        val geofence = Geofence.Builder()
            .setRequestId("GeofenceId")
            .setCircularRegion(center!!.latitude, center.longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent!!)
            .addOnSuccessListener {
                callback!!.onGeofenceCreated(true)
                drawGeofenceOnMap(center, radius) // Dibuja la geocerca en el mapa
                checkLocation(center, radius, testLocation, callback)
            }
            .addOnFailureListener { e: Exception? ->
                callback!!.onGeofenceCreated(false)
            }
    }

    private fun drawGeofenceOnMap(center: LatLng, radius: Float) {
        googleMap.addCircle(
            CircleOptions()
                .center(center)
                .radius(radius.toDouble())
                .strokeColor(0x80FF0000.toInt()) // Color del borde
                .fillColor(0x44FF0000) // Color de relleno con transparencia
                .strokeWidth(4f)
        )
    }

    private fun checkLocation(
        center: LatLng?,
        radius: Float,
        testLocation: LatLng?,
        callback: GeofenceCallback?
    ) {
        val distance = FloatArray(1)
        Location.distanceBetween(
            center!!.latitude,
            center.longitude,
            testLocation!!.latitude,
            testLocation.longitude,
            distance
        )
        val isInside = distance[0] <= radius
        callback!!.onLocationChecked(isInside)
    }
}
