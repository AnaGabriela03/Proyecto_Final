package com.example.avance

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

import androidx.compose.ui.platform.LocalDensity

@Composable
fun VerPantalla(
    navController: NavController,
    noteId: Int,
    viewModel: NoteTaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.Gray else Color.LightGray

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var videoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var audioPaths by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedReminders by remember { mutableStateOf<List<String>>(emptyList()) }
    var expandedImageUri by remember { mutableStateOf<Uri?>(null) } // Estado para la imagen ampliada

    // Cargar los datos de la nota o tarea según el ID
    LaunchedEffect(noteId) {
        val note = viewModel.notesTasks.value.find { it.id == noteId }
        note?.let {
            title = it.title
            description = it.description
            selectedTime = it.date ?: "Fecha no especificada"
            type = it.type
            imageUris = it.imageUri?.let { uris -> uris.split(",").map { Uri.parse(it) } } ?: emptyList()
            videoUris = it.fileUri?.let { uris -> uris.split(",").map { Uri.parse(it) } } ?: emptyList()
            audioPaths = it.audioUri?.split(",") ?: emptyList()
            selectedReminders = it.reminders?.split(",") ?: emptyList() // Cargar recordatorios
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con título y botón de "Atrás"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Atrás", color = textColor)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(color = textColor)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Sección de detalles
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Título", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                BasicTextField(
                    value = title,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                    textStyle = TextStyle(fontSize = 18.sp, color = textColor)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (type == "Tarea") {
                    Text(text = "Hora", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                    BasicTextField(
                        value = selectedTime,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                        textStyle = TextStyle(fontSize = 16.sp, color = textColor)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(text = "Descripción", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                BasicTextField(
                    value = description,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                    textStyle = TextStyle(fontSize = 16.sp, color = textColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

// Mostrar recordatorios seleccionados solo para tareas
        if (type == "Tarea" && selectedReminders.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // Margen general para toda la sección
                    .border(1.dp, borderColor, RoundedCornerShape(8.dp)) // Borde alrededor de la sección
                    .padding(16.dp) // Margen interno dentro del borde
            ) {
                Column {
                    Text(
                        text = "Recordatorios seleccionados:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp) // Espaciado debajo del título
                    )
                    selectedReminders.forEach { reminder ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp) // Espaciado entre los recordatorios
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime, // Icono de reloj
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp) // Tamaño del ícono
                                    .padding(end = 8.dp), // Espaciado entre el ícono y el texto
                                tint = textColor // Color acorde al tema
                            )
                            Text(
                                text = reminder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                        }
                    }
                }
            }
        } else if (type == "Tarea") {
            // Si es una tarea pero no tiene recordatorios
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "No hay recordatorios seleccionados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }





        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar imágenes
        imageUris.forEach { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Imagen asociada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expandedImageUri = uri } // Expandir imagen al hacer clic
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar videos
        videoUris.forEach { uri ->
            VideoPlayer(videoUri = uri)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Mostrar audios asociados con botones de reproducción
        audioPaths.forEach { audioPath ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Audio:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                IconButton(onClick = {
                    val mediaPlayer = MediaPlayer().apply {
                        setDataSource(audioPath)
                        prepare()
                        start()
                    }
                }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = textColor)
                }
            }
        }
    }

    // Ampliar imagen seleccionada
    if (expandedImageUri != null) {
        Dialog(onDismissRequest = { expandedImageUri = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { expandedImageUri = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = expandedImageUri,
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun VideoPlayer(videoUri: Uri) {
    var isPlaying by remember { mutableStateOf(false) } // Estado de reproducción

    AndroidView(
        factory = { context ->
            VideoView(context).apply {
                setVideoURI(videoUri)
                setOnPreparedListener {
                    it.isLooping = false // No se repite automáticamente
                    pause() // Inicia en pausa
                }
                setOnClickListener {
                    // Alterna entre reproducir y pausar
                    if (isPlaying) {
                        pause()
                    } else {
                        start()
                    }
                    isPlaying = !isPlaying
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp) // Tamaño uniforme con las imágenes
            .clip(RoundedCornerShape(8.dp)) // Borde redondeado similar a las imágenes
    )
}
