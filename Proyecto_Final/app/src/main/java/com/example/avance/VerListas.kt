package com.example.avance

import android.net.Uri
import android.widget.CalendarView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun tercera_pantalla(
    title: String,
    navController: NavController,
    viewModel: NoteTaskViewModel,  // Asegúrate de incluir el parámetro viewModel aquí
    isTaskSelected: Boolean  // Asegúrate de incluir el parámetro isTaskSelected aquí
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)
    val borderColor = if (isDarkTheme) Color.LightGray else Color.Black
    val scrollState = rememberScrollState()

    // Observar el flujo de notas/tareas desde el ViewModel
    val notes by viewModel.notesTasks.collectAsState()

    // Cargar las notas o tareas al iniciar la pantalla
    LaunchedEffect(isTaskSelected) {
        viewModel.loadNotesTasks(isTaskSelected)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(color = textColor),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar lista de tareas/notas desde la base de datos
        notes.forEach { note ->
            TaskItem(
                title = note.title,
                description = note.description,
                date = note.date,
                textColor = textColor,
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                onEdit = {
                    navController.navigate("secondScreen?noteId=${note.id}")
                },
                onDelete = {
                    viewModel.deleteNoteTask(note) // Llama a la función de eliminación en el ViewModel
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sección de botones con borde en la parte inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón del calendario
                if (isTaskSelected) {
                    IconButton(onClick = { /* Muestra el calendario */ }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = textColor)
                    }
                }
                // Ícono para abrir archivos
                IconButton(onClick = { /* Lanza el selector de archivos */ }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Adjuntar archivo", tint = textColor)
                }
                // Ícono para abrir cámara
                IconButton(onClick = { /* Lanza la cámara */ }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Cámara", tint = textColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        StyledAddButton(
            onClick = { navController.navigate("secondScreen") },
            backgroundColor = backgroundColor,
            textColor = textColor
        )
    }
}


@Composable
fun StyledAddButton(onClick: () -> Unit, backgroundColor: Color, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar nueva", tint = textColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.agregar_nueva), color = textColor)
        }
    }
}

@Composable
fun TaskItem(
    title: String,
    description: String,
    date: String?,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(color = textColor))
                Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
                if (date != null) {
                    Text(text = date, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = textColor)
                }
                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = textColor)
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(text = stringResource(R.string.confirmaci_n_de_eliminaci_n)) },
            text = { Text(text = stringResource(R.string.est_s_seguro_de_que_deseas_eliminar)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    onDelete()
                }) {
                    Text(stringResource(R.string.eliminar), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}