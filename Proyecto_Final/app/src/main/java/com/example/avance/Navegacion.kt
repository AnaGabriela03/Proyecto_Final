package com.example.avance

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun MyApp(noteTaskDao: NoteTaskDao) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    // Crear el ViewModel usando el NoteTaskViewModelFactory
    val viewModel: NoteTaskViewModel = viewModel(factory = NoteTaskViewModelFactory(noteTaskDao))

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable("home") {
                Principal(navController)
            }

            composable(
                "secondScreen?noteId={noteId}", // Define la segunda pantalla
                arguments = listOf(navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1 // Valor por defecto para nueva nota
                })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                segunda_pantalla(
                    navController = navController,
                    noteId = noteId,
                    viewModel = viewModel // Pasa el ViewModel en lugar del DAO
                )
            }

            composable(
                "notesTasksScreen/{item}",
                arguments = listOf(navArgument("item") { type = NavType.StringType })
            ) { backStackEntry ->
                val item = backStackEntry.arguments?.getString("item") ?: "Notas/Tareas"
                val isTaskSelected = item == "Tareas"

                tercera_pantalla(
                    title = if (isTaskSelected) stringResource(R.string.tareas) else stringResource(R.string.notas),
                    navController = navController,
                    viewModel = viewModel, // Pasa el ViewModel en lugar del DAO
                    isTaskSelected = isTaskSelected
                )
            }
        }
    }
}
