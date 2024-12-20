package com.example.avance

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tercera_pantalla(
    title: String,
    navController: NavController,
    viewModel: NoteTaskViewModel,
    isTaskSelected: Boolean
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)
    val borderColor = if (isDarkTheme) Color.LightGray else Color.Black
    val notes by viewModel.notesTasks.collectAsState()

    LaunchedEffect(isTaskSelected) {
        viewModel.loadNotesTasks(isTaskSelected)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(title)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            notes.forEach { note ->
                TaskItem(
                    title = note.title,
                    description = note.description,
                    date = note.date,
                    imageUris = note.imageUri?.split(",")?.map { Uri.parse(it) } ?: emptyList(),
                    fileUris = note.fileUri?.split(",")?.map { Uri.parse(it) } ?: emptyList(),
                    textColor = textColor,
                    backgroundColor = backgroundColor,
                    borderColor = borderColor,
                    isTask = note.type == "Tarea",
                    onEdit = {
                        navController.navigate("secondScreen?noteId=${note.id}&type=${note.type}")
                    },
                    onClick = {
                        navController.navigate("verPantalla?noteId=${note.id}")
                    },
                    onDelete = {
                        viewModel.deleteNoteTask(note)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))


            Spacer(modifier = Modifier.height(12.dp))

            StyledAddButton(
                onClick = {
                    navController.navigate("secondScreen?type=${if (isTaskSelected) "Tarea" else "Nota"}")
                },
                backgroundColor = backgroundColor,
                textColor = textColor
            )

        }
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
    imageUris: List<Uri>,
    fileUris: List<Uri>,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    isTask: Boolean,
    onEdit: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }  // Estado del checkbox
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isTask) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                    isChecked = checked  // Actualiza el estado del checkbox
                    if (checked) {
                        // Iniciar un retraso de 3 segundos para eliminar la tarea
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(3000)
                            if (isChecked) {  // Verificar si aún está marcado
                                onDelete()
                                isChecked = false  // Restablece el estado del checkbox
                            }
                        }
                    }
                }
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(color = textColor))
            Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
            date?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
            }

            // Mostrar las imágenes y videos en miniatura si existen
            if (imageUris.isNotEmpty() || fileUris.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    imageUris.take(3).forEach { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .padding(end = 4.dp)
                        )
                    }

                    fileUris.take(3).forEach { uri ->
                        // Mostrar la miniatura del video
                        VideoThumbnail(videoUri = uri)
                    }
                }
            }
        }

        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = textColor)
        }
        IconButton(onClick = { showDeleteConfirmation = true }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = textColor)
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
                    isChecked = false  // Restablece el estado del checkbox tras la eliminación
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

@Composable
fun VideoThumbnail(videoUri: Uri) {
    // Utilizamos MediaMetadataRetriever para obtener la miniatura del video
    val context = LocalContext.current
    val retriever = remember { MediaMetadataRetriever() }
    val thumbnail = remember {
        try {
            retriever.setDataSource(context, videoUri)
            retriever.getFrameAtTime(1000000)  // Primer fotograma del video
        } catch (e: Exception) {
            null
        }
    }

    // Si la miniatura es válida, la mostramos
    thumbnail?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Video Thumbnail",
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp))
                .padding(end = 4.dp)
        )
    }
}
