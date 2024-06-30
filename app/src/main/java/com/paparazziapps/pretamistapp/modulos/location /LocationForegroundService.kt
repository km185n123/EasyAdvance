package com.paparazziapps.pretamistapp.modulos.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity

private const val TAG = "LocationForegroundService"
private const val UPDATE_INTERVAL = 30 * 1000L // 30 segundos

class LocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var viewModel: ViewModelPrincipal
    private lateinit var viewModelStoreOwner: MyViewModelStoreOwner

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG", "onCreate ejecutado")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        createLocationCallback()

        viewModelStoreOwner = MyViewModelStoreOwner()
        viewModel = ViewModelProvider(viewModelStoreOwner).get(ViewModelPrincipal::class.java)

        startForegroundService()
        requestLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAG", "onStartCommand ejecutado")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let { location ->
                    Log.e("TAG", "Nueva ubicación: ${location.latitude}, ${location.longitude}")
                    // Aquí puedes enviar la ubicación al servidor
                    sendLocationToServer(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("LongLogTag")
    private fun sendLocationToServer(latitude: Double, longitude: Double) {
        // Implementa el envío de la ubicación a tu servidor aquí
        Log.e(TAG, "Ubicación enviada al servidor: $latitude, $longitude")
        // Aquí deberías implementar la lógica para enviar la ubicación al servidor
        // Puedes usar Retrofit, Volley, etc. para hacer la solicitud HTTP
        val userEmail = viewModel.getUserEmail()
        if (!userEmail.isNullOrBlank()) {
            viewModel.updateUserLocation(userEmail, latitude, longitude)
        } else {
            Log.e(TAG, "No se pudo obtener el email del usuario")
        }
    }

    private fun startForegroundService() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("foreground_service", "Foreground Service")
            } else {
                "" // Versiones anteriores a Android O no requieren un canal de notificación
            }

        val notificationIntent = Intent(this, PrincipalActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Servicio de Ubicación")
            .setContentText("Obteniendo ubicación cada 30 segundos")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
            return channelId
        }
        return ""
    }
}
