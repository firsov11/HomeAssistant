import android.util.Log
import androidx.lifecycle.ViewModel
import com.firsov.homeassistant.data.DeviceControl
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RealtimeDatabaseViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference

    private val _devices = MutableStateFlow<List<DeviceData>>(emptyList())
    val devices: StateFlow<List<DeviceData>> = _devices

    private val _deviceControl = MutableStateFlow(DeviceControl())
    val deviceControl: StateFlow<DeviceControl> = _deviceControl

    init {
        observeDevices()
        observeDeviceControl()
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
                    val vent = deviceSnapshot.child("vent").getValue(Boolean::class.java) ?: false
                    val temperature = deviceSnapshot.child("temperature").getValue(Float::class.java)
                    val humidity = deviceSnapshot.child("humidity").getValue(Float::class.java)
                    val pressure = deviceSnapshot.child("pressure").getValue(Float::class.java)
                    val tempout = deviceSnapshot.child("tempout").getValue(Float::class.java)
                    val humout = deviceSnapshot.child("humout").getValue(Float::class.java)
                    val co = deviceSnapshot.child("co").getValue(Float::class.java)

                    val timestamp = deviceSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                    val humanTime = formatTimestamp(timestamp)

                    val device = DeviceData(
                        device_id = deviceId,
                        presence = presence,
                        vent = vent,
                        temperature = temperature,
                        humidity = humidity,
                        tempout = tempout,
                        humout = humout,
                        pressure = pressure,
                        co = co,
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

    private fun observeDeviceControl() {
        database.child("device_control").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val control = snapshot.getValue(DeviceControl::class.java)
                Log.d("FirebaseDebug", "device_control snapshot: ${snapshot.value}")
                if (control != null) {
                    _deviceControl.value = control
                    Log.d("FirebaseDebug", "DeviceControl value updated: $control")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "Ошибка при получении device_control: ${error.message}")
            }
        })
    }

    // Форматирование времени для отображения
    private fun formatTimestamp(timestamp: Long): String {
        if (timestamp <= 0) return "неизвестно"
        return try {
            val date = Date(timestamp)
            val calendar = Calendar.getInstance().apply { time = date }
            val now = Calendar.getInstance()

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
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(cal1: Calendar, cal2: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = cal2.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(cal1, yesterday)
    }
}
