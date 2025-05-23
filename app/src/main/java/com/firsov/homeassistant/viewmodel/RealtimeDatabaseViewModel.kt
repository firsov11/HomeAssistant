import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RealtimeDatabaseViewModel : ViewModel() {
    private val dbRef = FirebaseDatabase.getInstance().getReference("devices")
    private val _devices = MutableStateFlow<List<DeviceData>>(emptyList())
    val devices: StateFlow<List<DeviceData>> get() = _devices

    init {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<DeviceData>()
                for (deviceSnapshot in snapshot.children) {
                    val id = deviceSnapshot.key ?: continue
                    val status = deviceSnapshot.child("status").getValue(String::class.java) ?: "unknown"
                    val temp = deviceSnapshot.child("temperature").getValue(Double::class.java) ?: 0.0
                    list.add(DeviceData(id, status, temp))
                }
                _devices.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error if needed
            }
        })
    }
}
