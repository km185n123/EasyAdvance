package com.paparazziapps.pretamistapp.helper


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class LocationHelper(
    private val activity: Activity,
    private val locationCallback: LocationCallback? = null
) {


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    fun checkPermissionsAndGetLocation() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                getLastLocation2()
            } else {
                promptEnableLocation()
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

   /* private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("LocationHelper", "Lat: $latitude, Lon: $longitude")
                    Toast.makeText(activity, "Lat: $latitude, Lon: $longitude", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Log.d(
                        "LocationHelper",
                        "No se pudo obtener la ubicación. Solicitando actualizaciones..."
                    )
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null
                    )
                    // requestNewLocationData()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationHelper", "Error al obtener la ubicación.", exception)
            }
    }*/

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 segundos
            fastestInterval = 5000 // 5 segundos
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

       // fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

     fun getLastLocation2(): Location? {
        var myLocation: Location? = null
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            Log.d("MainActivity", "Lat: $latitude, Lon: $longitude")

                            Toast.makeText(
                                activity,
                                "Lat: $latitude, Lon: $longitude",
                                Toast.LENGTH_LONG
                            ).show()
                            myLocation =  location
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
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        return myLocation
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun promptEnableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (isLocationEnabled()) {
                    getLastLocation2()
                } else {
                    promptEnableLocation()
                }
            } else {
                Toast.makeText(activity, "Permiso de ubicación denegado", Toast.LENGTH_LONG).show()
            }
        }
    }
}
