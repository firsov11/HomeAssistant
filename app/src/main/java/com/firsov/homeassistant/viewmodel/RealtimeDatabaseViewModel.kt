import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RealtimeDatabaseViewModel : ViewModel() {
    private val dbRef = FirebaseDatabase.getInstance().getReference("presence_logs")
    private val _devices = MutableStateFlow<List<DeviceData>>(emptyList())
    val devices: StateFlow<List<DeviceData>> get() = _devices

    init {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<DeviceData>()
                for (deviceSnapshot in snapshot.children) {
                    val device_id = deviceSnapshot.child("device_id").getValue(String::class.java) ?: continue
                    val human_time = deviceSnapshot.child("human_time").getValue(String::class.java) ?: ""
                    val presence = deviceSnapshot.child("presence").getValue(Boolean::class.java) ?: false
                    val timestamp = deviceSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L

                    list.add(
                        DeviceData(
                            device_id = device_id,
                            human_time = human_time,
                            presence = presence,
                            number = timestamp
                        )
                    )
                }
                _devices.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Логирование или обработка ошибки при необходимости
            }
        })
    }
}
