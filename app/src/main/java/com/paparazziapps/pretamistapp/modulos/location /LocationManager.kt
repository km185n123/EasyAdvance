package com.paparazziapps.pretamistapp.modulos.location

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.work.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener


class LocationManager private constructor(val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var locationCallback: LocationCallback? = null

    companion object {
        @Volatile
        private var INSTANCE: LocationManager? = null

        fun getInstance(context: Context): LocationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationManager(context).also { INSTANCE = it }
            }
        }
    }

    fun getCurrentLocation(listener: OnSuccessListener<Location>, context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(listener)
    }

    fun startLocationService(context: Context) {
        val serviceIntent = Intent(context, LocationService::class.java)
        context.startService(serviceIntent)
    }

    fun stopLocationService(context: Context) {
        val serviceIntent = Intent(context, LocationService::class.java)
        context.stopService(serviceIntent)
    }

    fun stopWorkManager(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("LocationWork")
    }

    fun startLocationUpdates(locationListener: (Location) -> Unit) {
        Log.d("LocationManager", "Iniciando actualizaciones de ubicación.,")
        val locationRequest = LocationRequest.create().apply {
            interval = 10 * 1000 // Intervalo de actualización cada 30 segundos
            fastestInterval = 10 * 1000 // Intervalo más rápido en caso de disponibilidad
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    locationListener(location) // Llamada a la lambda con la ubicación
                    sendLocationToServer(location)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback as LocationCallback, Looper.getMainLooper())
        }
    }

    fun stopLocationUpdates(serviceContext: Context) {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    private fun sendLocationToServer(location: Location) {
        // Implementa aquí la lógica para enviar la ubicación al servidor
        // Puedes usar Retrofit, Volley, o cualquier otra librería para realizar la solicitud HTTP
        // Ejemplo ficticio:
        Log.d("LocationManager", "Ubicación enviada al servidor correctamente.....")

    }
}
