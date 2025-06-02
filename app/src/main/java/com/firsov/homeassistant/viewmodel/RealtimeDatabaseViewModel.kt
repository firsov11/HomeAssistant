import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class RealtimeDatabaseViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference
    private val _devices = MutableStateFlow<List<DeviceData>>(emptyList())
    val devices: StateFlow<List<DeviceData>> = _devices

    init {
        observeDevices()
    }

    private fun observeDevices() {
        database.child("presence_logs").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val deviceList = mutableListOf<DeviceData>()

                for (deviceSnapshot in snapshot.children) {
                    val deviceId = deviceSnapshot.child("device_id").getValue(String::class.java) ?: continue

                    val typeString = deviceSnapshot.child("type").getValue(String::class.java) ?: continue

                    val type = try {
                        DeviceType.valueOf(typeString.uppercase())
                    } catch (e: IllegalArgumentException) {
                        continue // некорректный тип — пропускаем
                    }


                    val presence = deviceSnapshot.child("presence").getValue(Boolean::class.java) ?: false
                    val temperature = deviceSnapshot.child("temperature").getValue(Float::class.java)
                    val humidity = deviceSnapshot.child("humidity").getValue(Float::class.java)
                    val pressure = deviceSnapshot.child("pressure").getValue(Float::class.java)

                    // Получаем и форматируем timestamp
                    val timestamp = deviceSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                    val humanTime = if (timestamp > 0) {
                        try {
                            val date = Date(timestamp)
                            val calendar = Calendar.getInstance()
                            val now = Calendar.getInstance()
                            now.timeInMillis = System.currentTimeMillis()

                            calendar.time = date

                            val formatter = SimpleDateFormat("HH:mm", Locale("ru"))

                            when {
                                isSameDay(calendar, now) -> "Сегодня в ${formatter.format(date)}"
                                isYesterday(calendar, now) -> "Вчера в ${formatter.format(date)}"
                                else -> {
                                    val fullFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru"))
                                    fullFormat.format(date)
                                }
                            }
                        } catch (e: Exception) {
                            "неизвестно"
                        }
                    } else {
                        "неизвестно"
                    }


                    val device = DeviceData(
                        device_id = deviceId,
                        presence = presence,
                        temperature = temperature,
                        humidity = humidity,
                        pressure = pressure,
                        type = type,
                        human_time = humanTime
                    )

                    deviceList.add(device)
                }

                _devices.value = deviceList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "Ошибка при получении данных: ${error.message}")
            }
        })
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isYesterday(cal1: Calendar, cal2: Calendar): Boolean {
    val yesterday = Calendar.getInstance()
    yesterday.timeInMillis = cal2.timeInMillis
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    return isSameDay(cal1, yesterday)
}

