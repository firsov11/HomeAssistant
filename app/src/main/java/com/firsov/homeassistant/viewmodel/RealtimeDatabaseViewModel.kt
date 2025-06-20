import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsov.homeassistant.data.DeviceControl
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.firsov.homeassistant.data.weather.WeatherService
import com.google.gson.Gson

class RealtimeDatabaseViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference

    private val _devices = MutableStateFlow<List<DeviceData>>(emptyList())
    val devices: StateFlow<List<DeviceData>> = _devices

    private val _deviceControl = MutableStateFlow(DeviceControl())
    val deviceControl: StateFlow<DeviceControl> = _deviceControl

    private val _pressureHistory = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val pressureHistory: StateFlow<List<Pair<String, Float>>> = _pressureHistory

    private val _forecastPressure = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val forecastPressure: StateFlow<List<Pair<String, Float>>> = _forecastPressure
    
    init {
        observeDevices()
        observeDeviceControl()
        loadDailyPressureData()
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

    private fun loadDailyPressureData() {
        database.child("presence_logs").orderByChild("type").equalTo("PRESSURE")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<Pair<Long, Float>>()

                    for (entry in snapshot.children) {
                        val timestamp = entry.child("timestamp").getValue(Long::class.java) ?: continue
                        val pressure = entry.child("pressure").getValue(Float::class.java) ?: continue
                        entries.add(timestamp to pressure)
                    }

                    val sorted = entries.sortedByDescending { it.first }.take(3).reversed()

                    val labeled = sorted.map { (ts, pressure) ->
                        val label = formatShortDate(ts)
                        label to pressure
                    }.toMutableList()

                    _pressureHistory.value = labeled
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Ошибка при получении давления: ${error.message}")
                }
            })
    }

    fun loadForecast(
        lat: Double = 49.46,
        lon: Double = 36.19,
        apiKey: String
    ) {
        viewModelScope.launch {
            try {
                val response = WeatherService.api.getForecast(lat, lon, apiKey)

                // Конвертируем hPa → мм рт. ст.
                val grouped = response.list
                    .groupBy { formatForecastDay(it.dt) }
                    .mapValues {
                        val grndLevel = it.value.first().main.grnd_level
                        val hpa = grndLevel ?: it.value.first().main.pressure
                        hpa * 0.750062f
                    }

                _forecastPressure.value = grouped.entries.map { it.key to it.value }
                Log.d("Forecast", "Converted forecast (mmHg): ${_forecastPressure.value}")
            } catch (e: Exception) {
                Log.e("Forecast", "Ошибка загрузки прогноза", e)
            }
        }
    }

    private fun formatForecastDay(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("d MMMM (прогноз)", Locale("ru"))
        return format.format(date)
    }

    private fun formatShortDate(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("d MMMM", Locale("ru"))
        return formatter.format(date)
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
