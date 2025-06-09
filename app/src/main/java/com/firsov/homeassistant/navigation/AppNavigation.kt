import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.firsov.homeassistant.ui.screens.HomeScreen
import com.firsov.homeassistant.ui.screens.SensorCoScreen
import com.firsov.homeassistant.ui.screens.SensorOutScreen
import com.firsov.homeassistant.ui.screens.SensorPressureScreen
import com.firsov.homeassistant.ui.screens.VentilationScreen


@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen(navController = navController, hasPresence = true) // или false
        }
        composable("radar") { RadarScreen() }
        composable("sensor") { SensorScreen() }
        composable("sensor_pressure") { SensorPressureScreen() }
        composable("sensorout") { SensorOutScreen() }
        composable("ventilation") { VentilationScreen() }
        composable("co") { SensorCoScreen() }
        composable("send_test") {
            SendDeviceDataScreen(navController)
        }


    }
}

