package com.paparazziapps.pretamistapp.modulos.location

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

fun scheduleLocationUpdates(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, LocationAlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val triggerAtMillis = SystemClock.elapsedRealtime() + 30 * 1000L
    val intervalMillis = 30 * 1000L

    alarmManager.setRepeating(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        triggerAtMillis,
        intervalMillis,
        pendingIntent
    )

    Log.d("ScheduleLocationUpdates", "Alarma programada para cada 30 segundos")
}
