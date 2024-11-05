package com.example.avance_proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.avance.MyApp
import com.example.avance.ui.theme.AvanceTheme
import com.example.avance.NoteTaskDatabase
import com.example.avance.NoteTaskRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa la base de datos y obtiene el DAO
        val database = NoteTaskDatabase.getDatabase(applicationContext)
        val noteTaskDao = database.noteTaskDao()

        // Crea una instancia del repositorio
        val repository = NoteTaskRepository(noteTaskDao)

        setContent {
            AvanceTheme {
                MyApp(noteTaskRepository = repository) // Pasa el repositorio a MyApp
            }
        }
    }
}
