

package com.firsov.homeassistant.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.firsov.homeassistant.R
import com.google.firebase.database.*

class AlertService : Service() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    companion object {
        private const val CHANNEL_ID = "alert_channel"
        private const val CHANNEL_NAME = "Оповещения"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createInitialNotification())
        startFirebaseListener()
    }

    private fun startFirebaseListener() {
        database.child("device_triggered").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val coAlert = snapshot.child("co_alert").getValue(Boolean::class.java) ?: false
                val radarAlert = snapshot.child("radar_alert").getValue(Boolean::class.java) ?: false
                val ventAlert = snapshot.child("vent_alert").getValue(Boolean::class.java) ?: false

                if (radarAlert) {
                    sendNotification("Обнаружено движение!", "Один из радаров зафиксировал присутствие.")
                }

                if (coAlert) {
                    sendNotification("Опасность CO!", "Один из датчиков зафиксировал угарный газ.")
                }

                if (ventAlert) {
                    sendNotification("Проветривание включено", "Вентиляция была активирована.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Ошибка при прослушивании device_triggered: ${error.message}")
            }
        })
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createInitialNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Home Assistant")
            .setContentText("Оповещения активны")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ← замени на свой ресурс
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun sendNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ← замени на свой ресурс
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
