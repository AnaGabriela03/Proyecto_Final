package com.example.avance

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Obtén los datos de la alarma
        val title = intent.getStringExtra("TITLE") ?: "Recordatorio"
        val message = intent.getStringExtra("MESSAGE") ?: "No olvides revisar tu tarea."

        // Crear un canal de notificación (necesario para Android 8.0 o superior)
        val channelId = "alarm_channel"
        val channelName = "Alarm Notifications"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Crear la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
