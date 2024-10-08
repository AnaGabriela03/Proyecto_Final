package com.example.avance

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val Typography = androidx.compose.material3.Typography()
val Shapes = androidx.compose.material3.Shapes()

// Esquemas de colores claros y oscuros
private val LightColors = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5),  // Fondo claro
    surface = Color(0xFFFFFFFF),
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    background = Color(0xFF121212),  // Fondo oscuro
    surface = Color(0xFF121212),
    onSurface = Color.White
)

// Configura la navegación y el tema
@Composable
fun MyApp() {
    val darkTheme = isSystemInDarkTheme() // Detecta si el sistema está en modo oscuro
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors, // Cambia el esquema de colores
        typography = Typography,
        shapes = Shapes
    ) {
        val navController = rememberNavController()

        // Aplicar el fondo dinámico según el tema
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Cambia el fondo dinámico
        ) {
            composable("home") { Principal(navController) }
            composable("secondScreen") { segunda_pantalla(navController) }
            composable("notesTasksScreen/{item}") { backStackEntry ->
                val item = backStackEntry.arguments?.getString("item") ?: "Notas/Tareas"
                tercera_pantalla(item, listOf(item), navController)
            }
        }
    }
}
