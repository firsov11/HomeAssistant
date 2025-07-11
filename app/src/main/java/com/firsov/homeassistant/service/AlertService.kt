package com.firsov.homeassistant.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.firsov.homeassistant.R
import com.google.firebase.database.*

class AlertService : Service() {

    private val triggeredRef = FirebaseDatabase.getInstance().getReference("device_triggered")
    private val controlRef = FirebaseDatabase.getInstance().getReference("device_control")

    private var isRadarEnabled = true
    private var isCoEnabled = true
    private var isVentEnabled = true

    private var lastRadar = false
    private var lastCo = false
    private var lastVent = false

    private var initialized = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createInitialNotification())
        loadDeviceControl()
        startListening()
    }

    private fun loadDeviceControl() {
        controlRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isRadarEnabled = snapshot.child("radar").getValue(Boolean::class.java) ?: true
                isCoEnabled = snapshot.child("sensor_co").getValue(Boolean::class.java) ?: true
                isVentEnabled = snapshot.child("vent").getValue(Boolean::class.java) ?: true
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AlertService", "Ошибка device_control: ${error.message}")
            }
        })
    }

    private fun startListening() {
        triggeredRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val radar = snapshot.child("radar_alert").getValue(Boolean::class.java) ?: false
                val co = snapshot.child("co_alert").getValue(Boolean::class.java) ?: false
                val vent = snapshot.child("vent_alert").getValue(Boolean::class.java) ?: false

                if (initialized) {
                    if (radar && isRadarEnabled && !lastRadar) {
                        sendNotification("Обнаружено движение!", "Один из радаров зафиксировал присутствие.")
                    }
                    if (co && isCoEnabled && !lastCo) {
                        sendNotification("Опасность CO!", "Один из датчиков зафиксировал угарный газ.")
                    }
                    if (vent && isVentEnabled && !lastVent) {
                        sendNotification("Проветривание включено", "Вентиляция была активирована.")
                    }
                } else {
                    initialized = true // пропускаем первое срабатывание
                }

                lastRadar = radar
                lastCo = co
                lastVent = vent
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AlertService", "Ошибка device_triggered: ${error.message}")
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createInitialNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Home Assistant")
            .setContentText("Оповещения активны")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun sendNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "alert_channel"
        private const val CHANNEL_NAME = "Оповещения"
        private const val NOTIFICATION_ID = 1
    }
}
