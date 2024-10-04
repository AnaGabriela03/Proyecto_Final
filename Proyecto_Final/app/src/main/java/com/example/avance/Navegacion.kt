package com.example.avance

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Configura la navegación
@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Principal(navController) }
        composable("secondScreen") {  segunda_pantalla(navController) }
        composable("notesTasksScreen/{item}") { backStackEntry ->
            val item = backStackEntry.arguments?.getString("item") ?: "Notas/Tareas"
            tercera_pantalla(item, listOf(item), navController)
        }


    }
}