package com.paparazziapps.pretamistapp.modulos.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class LocationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("LocationAlarmReceiver", "Alarma activada, iniciando servicio de ubicación")
        context?.let {
            val serviceIntent = Intent(it, LocationForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.startForegroundService(serviceIntent)
            } else {
                it.startService(serviceIntent)
            }
        }
    }
}
