import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.firsov.homeassistant.ui.screens.HomeScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen(navController = navController, hasPresence = true) // или false
        }
        composable("devices") { DevicesScreen() }
        composable("sensor") { SensorScreen() }
    }
}

