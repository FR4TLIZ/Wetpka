package com.example.wetpka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wetpka.ui.AtlasScreen
import com.example.wetpka.ui.LogbookScreen
import com.example.wetpka.ui.MapScreen
import com.example.wetpka.ui.ProfileScreen
import com.example.wetpka.ui.theme.WetpkaTheme // Jeśli podkreśla na czerwono, sprawdź czy nazwa motywu się zgadza

// Klasa pomocnicza definiująca nasze ekrany w menu dolnym
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Atlas : BottomNavItem("atlas", "Atlas", Icons.Default.List)
    object Map : BottomNavItem("map", "Mapa łowisk", Icons.Default.Place)
    object Logbook : BottomNavItem("logbook", "Rejestr", Icons.Default.Edit)
    object Profile : BottomNavItem("profile", "Legitymacja", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WetpkaTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    // Kontroler nawigacji
    val navController = rememberNavController()

    // Lista naszych zakładek
    val items = listOf(
        BottomNavItem.Atlas,
        BottomNavItem.Map,
        BottomNavItem.Logbook,
        BottomNavItem.Profile
    )

    // Główny szkielet z dolnym paskiem (Scaffold)
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(text = item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                // Gwarantuje, że nie otworzymy 100 takich samych ekranów po szybkim klikaniu
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // To jest nasz "pojemnik" na ekrany, który reaguje na kliknięcia w menu dolnym
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Atlas.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Atlas.route) { AtlasScreen() }
            composable(BottomNavItem.Map.route) { MapScreen() }
            composable(BottomNavItem.Logbook.route) { LogbookScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}