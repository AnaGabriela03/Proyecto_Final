package com.example.avance

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.widget.CalendarView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
// Variables globales
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.provider.MediaStore
import java.util.*
import android.util.Log
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Videocam

private var mediaPlayer: MediaPlayer? = null


// Variables globales para la grabación de audio
private var mediaRecorder: MediaRecorder? = null
private var audioFilePath: String? = null



// Guardar imagen capturada
fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): Uri? {
    val file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")
    return try {
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        Uri.fromFile(file)
    } catch (e: Exception) {
        null
    }
}


// Guardar video en almacenamiento interno
fun saveVideoToInternalStorage(context: Context, uri: Uri): Uri? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.filesDir, "video_${System.currentTimeMillis()}.mp4")
    return try {
        inputStream?.use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
// Función para reproducir audio con validación
fun playAudio(context: Context, path: String) {
    try {
        mediaPlayer?.release() // Libera el reproductor si ya estaba en uso
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path) // Establece la fuente del audio desde la ruta
            prepare() // Prepara el reproductor
            start() // Inicia la reproducción
        }
        Toast.makeText(context, "Reproduciendo audio...", Toast.LENGTH_SHORT).show()

        mediaPlayer?.setOnCompletionListener {
            it.release() // Libera los recursos del reproductor cuando el audio termina
            mediaPlayer = null
            Toast.makeText(context, "Audio finalizado", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error al reproducir el audio: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

// Iniciar grabación sin permisos explícitos
fun startRecordingWithoutPermissions(context: Context) {
    try {
        val storageDir = context.filesDir // Almacena en el directorio interno de la app
        val audioFile = File(storageDir, "audio_${System.currentTimeMillis()}.mp3")
        audioFilePath = audioFile.absolutePath

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(audioFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
            start()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error al iniciar la grabación", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

// Detener grabación
fun stopRecording(): String? {
    return try {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        audioFilePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
// Manejo de permisos
fun checkAndRequestPermissions(context: Context, onPermissionsGranted: () -> Unit) {
    val requiredPermissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val missingPermissions = requiredPermissions.filter {
        ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }

    if (missingPermissions.isEmpty()) {
        onPermissionsGranted()
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            missingPermissions.toTypedArray(),
            100
        )
    }
}
@Composable
fun ListCardWithIcon(
    title: String,
    icon: ImageVector,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Controlar el estado desplegado
    var selectedItem by remember { mutableStateOf(title) } // Ítem seleccionado

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }, // Alterna el estado de expandir
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Encabezado de la tarjeta con el icono y título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedItem,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }



            // Lista desplegable de opciones (solo se muestra si está expandido)
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedItem = item
                                    onItemSelected(item)
                                    expanded = false // Cierra el menú
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(24.dp)) // Espaciado antes del texto
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}


// Verificar y solicitar permisos
fun requestPermissionsIfNeeded(
    context: Context,
    permissions: Array<String>,
    onGranted: () -> Unit
) {
    val missingPermissions = permissions.filter {
        ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }

    if (missingPermissions.isEmpty()) {
        onGranted()
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            missingPermissions.toTypedArray(),
            200 // Código único para permisos
        )
    }
}


@Composable
fun segunda_pantalla(
    navController: NavController,
    noteId: Int,
    initialType: String,
    viewModel: NoteTaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.Gray else Color.LightGray

    // Variables de estado
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(initialType) }
    var isCalendarVisible by remember { mutableStateOf(false) } // Siempre cerrado al inicio
    var selectedDate by remember { mutableStateOf("Seleccione una fecha") }
    var selectedTime by remember { mutableStateOf("Seleccione una hora") }
    var audioFilePaths by remember { mutableStateOf<List<String>>(emptyList()) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var fileUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isRecording by remember { mutableStateOf(false) }
    val selectedOptions = remember { mutableStateMapOf<String, Boolean>() }
    val imageUrisStrings = imageUris.map { it.toString() } // Convierte cada URI a String
    val fileUrisStrings = fileUris.map { it.toString() }
    var selectedReminders by remember { mutableStateOf(listOf<String>()) }



    LaunchedEffect(noteId) {
        if (noteId != -1) {
            val note = viewModel.notesTasks.value.find { it.id == noteId }
            note?.let {
                title = it.title
                description = it.description
                selectedType = it.type
                isCalendarVisible = it.type == "Tarea"
                audioFilePaths = it.audioUri?.split(",") ?: emptyList()
                imageUris = it.imageUri?.split(",")?.map { uri -> Uri.parse(uri) } ?: emptyList()
                fileUris = it.fileUri?.split(",")?.map { uri -> Uri.parse(uri) } ?: emptyList()

                // Configurar recordatorios
                val savedReminders = it.reminders?.split(",") ?: emptyList()
                selectedReminders = savedReminders

                // Actualizar selectedOptions para reflejar los recordatorios guardados
                selectedOptions.clear() // Limpia el estado previo
                savedReminders.forEach { reminder ->
                    selectedOptions[reminder] = true
                }

                // Configurar fecha y hora
                it.date?.let { fullDate ->
                    val parts = fullDate.split(" ")
                    if (parts.size == 2) {
                        selectedDate = parts[0]
                        selectedTime = parts[1]
                    }
                }
            }
        }
    }




    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToInternalStorage(context, it)
            if (uri != null) {
                imageUris = imageUris + uri
            }
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val savedUri = saveVideoToInternalStorage(context, it)
            if (savedUri != null) {
                fileUris = fileUris + savedUri
            }
        }
    }

    // Funciones para grabar audio
    val startRecording: () -> Unit = {
        try {
            startRecordingWithoutPermissions(context)
            Toast.makeText(context, "Grabando audio...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al iniciar la grabación", Toast.LENGTH_SHORT).show()
        }
    }

    val stopRecording: () -> Unit = {
        val path = stopRecording()
        if (path != null) {
            audioFilePaths = audioFilePaths + path
            Toast.makeText(context, "Audio guardado en: $path", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error al detener la grabación", Toast.LENGTH_SHORT).show()
        }
    }


    // Declara una lista para almacenar las URIs de los videos


// Crear el videoRecorderLauncher para manejar la grabación de video
    val videoRecorderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Obtener la URI del video grabado
            val videoUri = result.data?.data
            videoUri?.let {
                // Guardar el video en el almacenamiento interno
                val savedUri = saveVideoToInternalStorage(context, it)
                if (savedUri != null) {
                    // Agregar el video guardado a la lista
                    fileUris = fileUris + savedUri
                }
            }
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
        // Header con "Cancelar", Título centrado y "Guardar"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = textColor
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Título según si es una tarea o una nota
                Text(
                    text = when {
                        noteId == -1 && selectedType == "Tarea" -> "Nueva Tarea"  // Si es una nueva tarea
                        noteId != -1 && selectedType == "Tarea" -> "Editar Tarea" // Si es una tarea existente
                        noteId == -1 -> "Nueva Nota" // Si es una nueva nota
                        else -> "Editar Nota" // Si es una nota existente
                    },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
            }


            TextButton(onClick = {
                if (title.isEmpty()) {
                    Toast.makeText(context, "El título es obligatorio", Toast.LENGTH_SHORT).show()
                } else {
                    // Convertir los recordatorios seleccionados en una cadena
                    val reminders = selectedOptions.filter { it.value }.keys.joinToString(",")

                    // Crear o actualizar la nota
                    val newNoteTask = NoteTask(
                        id = if (noteId == -1) 0 else noteId,
                        title = title,
                        description = description,
                        type = selectedType,
                        imageUri = if (imageUris.isNotEmpty()) imageUrisStrings.joinToString(",") else null,
                        fileUri = if (fileUris.isNotEmpty()) fileUrisStrings.joinToString(",") else null,
                        audioUri = if (audioFilePaths.isNotEmpty()) audioFilePaths.joinToString(",") else null,
                        date = if (selectedType == "Tarea") "$selectedDate $selectedTime" else null,
                        reminders = selectedReminders.joinToString(",") // Guardar recordatorios seleccionados
                    )

                    viewModel.addOrUpdateNoteTask(newNoteTask)
                    navController.popBackStack()
                }
            })
            {
                Text(
                    text = if (noteId == -1) "Agregar" else "Guardar",
                    fontWeight = FontWeight.Bold
                )
            }

        }

        // Contenido Principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Título", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                    textStyle = TextStyle(fontSize = 18.sp, color = textColor)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Descripción", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                    textStyle = TextStyle(fontSize = 16.sp, color = textColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

// Solo muestra el selector de tipo si no hay un tipo predefinido
        if (noteId == -1 && selectedType.isEmpty()) {
            ListCardWithIcon(
                title = "Seleccionar tipo",
                icon = Icons.Default.Menu,  // Solo se pasa el icono de menú
                items = listOf("Tarea", "Nota"),
                onItemSelected = { selected ->
                    selectedType = selected
                }
            )
            Spacer(modifier = Modifier.height(16.dp)) // Añadir un espacio después
        }


        // Contenedor con margen para los botones funcionales
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Botón para adjuntar archivo
                IconButton(onClick = { fileLauncher.launch("video/*") }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Adjuntar archivo", tint = textColor)
                }

                // Botón para abrir la cámara normal
                IconButton(onClick = {
                    requestPermissionsIfNeeded(
                        context,
                        arrayOf(android.Manifest.permission.CAMERA)
                    ) {
                        cameraLauncher.launch(null)
                    }
                }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Abrir cámara", tint = textColor)
                }


                IconButton(onClick = {
                    // Solicitar permisos de cámara
                    requestPermissionsIfNeeded(
                        context,
                        arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                    ) {
                        // Lanza la grabación de video solo si los permisos son otorgados
                        val videoCaptureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                            // Puedes configurar opciones adicionales aquí si es necesario
                        }
                        videoRecorderLauncher.launch(videoCaptureIntent)
                    }
                }) {
                    Icon(Icons.Default.Videocam, contentDescription = "Grabar video", tint = textColor)
                }




                // Icono del calendario en la barra de botones
                if (selectedType == "Tarea") {
                    IconButton(onClick = {
                        isCalendarVisible = !isCalendarVisible // Alternar visibilidad del calendario
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Abrir calendario", tint = textColor)
                    }
                }

                // DETER AUDIOS
                fun stopRecording(): String? {
                    return try {
                        mediaRecorder?.apply {
                            stop()
                            release()
                        }
                        mediaRecorder = null
                        audioFilePath
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                // AUDIOS
                IconButton(onClick = {
                    requestPermissionsIfNeeded(
                        context,
                        arrayOf(android.Manifest.permission.RECORD_AUDIO)
                    ) {
                        if (isRecording) {
                            stopRecording()?.let { path ->
                                Log.d("AudioDebug", "Audio Path: $path")
                                audioFilePaths = audioFilePaths + path
                                Toast.makeText(context, "Audio guardado en: $path", Toast.LENGTH_SHORT).show()
                            } ?: Log.e("AudioDebug", "Error al detener la grabación")
                        } else {
                            Log.d("AudioDebug", "Iniciando grabación")
                            startRecordingWithoutPermissions(context)
                            Toast.makeText(context, "Grabando audio...", Toast.LENGTH_SHORT).show()
                        }
                        isRecording = !isRecording
                    }
                }) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Grabar audio",
                        tint = textColor
                    )
                }

            }
        }

        // Mostrar calendario solo cuando esté visible
        if (isCalendarVisible) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AndroidView(
                    factory = { context ->
                        CalendarView(context).apply {
                            setOnDateChangeListener { _, year, month, dayOfMonth ->
                                selectedDate = "$dayOfMonth/${month + 1}/$year"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)
                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                            },
                            hour,
                            minute,
                            true
                        ).show()
                    }) {
                        Text("Hora")
                    }
                    val context = LocalContext.current








                    // Variable de estado para mostrar el diálogo
                    var showDialog by remember { mutableStateOf(false) }

// Estado global para las opciones seleccionadas
                    val selectedOptions = remember { mutableStateMapOf<String, Boolean>() }

// Lista de opciones
                    val options = listOf(
                        "30 segundos antes",
                        "1 minuto antes",
                        "5 minutos antes",
                        "15 minutos antes",
                        "30 minutos antes",
                        "1 hora antes",
                        "2 horas antes",
                        "1 día antes",
                        "2 días antes"
                    )

// Inicializar opciones seleccionadas previamente
                    LaunchedEffect(selectedReminders) {
                        options.forEach { option ->
                            selectedOptions[option] = selectedReminders.contains(option)
                        }
                    }

// Botón para abrir el diálogo
                    Button(onClick = { showDialog = true }) {
                        Text("Recordar")
                    }

// Mostrar el diálogo
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Selecciona el tiempo de recordatorio") },
                            text = {
                                Column {
                                    options.forEach { option ->
                                        val isOptionEnabled = remember {
                                            // Lógica para validar si la opción sigue siendo válida
                                            val calendar = Calendar.getInstance().apply {
                                                val (day, month, year) = selectedDate.split("/").map { it.toInt() }
                                                val (hour, minute) = selectedTime.split(":").map { it.toInt() }
                                                set(Calendar.YEAR, year)
                                                set(Calendar.MONTH, month - 1)
                                                set(Calendar.DAY_OF_MONTH, day)
                                                set(Calendar.HOUR_OF_DAY, hour)
                                                set(Calendar.MINUTE, minute)
                                                set(Calendar.SECOND, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }

                                            when (option) {
                                                "30 segundos antes" -> calendar.add(Calendar.SECOND, -30)
                                                "1 minuto antes" -> calendar.add(Calendar.MINUTE, -1)
                                                "5 minutos antes" -> calendar.add(Calendar.MINUTE, -5)
                                                "15 minutos antes" -> calendar.add(Calendar.MINUTE, -15)
                                                "30 minutos antes" -> calendar.add(Calendar.MINUTE, -30)
                                                "1 hora antes" -> calendar.add(Calendar.HOUR_OF_DAY, -1)
                                                "2 horas antes" -> calendar.add(Calendar.HOUR_OF_DAY, -2)
                                                "1 día antes" -> calendar.add(Calendar.DAY_OF_MONTH, -1)
                                                "2 días antes" -> calendar.add(Calendar.DAY_OF_MONTH, -2)
                                            }

                                            val now = Calendar.getInstance().apply {
                                                set(Calendar.SECOND, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            calendar.timeInMillis > now.timeInMillis
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = isOptionEnabled) {
                                                    if (isOptionEnabled) {
                                                        selectedOptions[option] = !(selectedOptions[option] ?: false)
                                                    }
                                                }
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = selectedOptions[option] ?: false,
                                                onCheckedChange = {
                                                    if (isOptionEnabled) {
                                                        selectedOptions[option] = it
                                                    }
                                                },
                                                enabled = isOptionEnabled
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = option,
                                                color = if (isOptionEnabled) Color.Unspecified else Color.Gray
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDialog = false

                                    // Configuración de alarmas
                                    val calendar = Calendar.getInstance().apply {
                                        val (day, month, year) = selectedDate.split("/").map { it.toInt() }
                                        val (hour, minute) = selectedTime.split(":").map { it.toInt() }
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month - 1)
                                        set(Calendar.DAY_OF_MONTH, day)
                                        set(Calendar.HOUR_OF_DAY, hour)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }

                                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    selectedOptions.filter { it.value }.keys.forEach { option ->
                                        val reminderCalendar = calendar.clone() as Calendar

                                        when (option) {
                                            "30 segundos antes" -> reminderCalendar.add(Calendar.SECOND, -30)
                                            "1 minuto antes" -> reminderCalendar.add(Calendar.MINUTE, -1)
                                            "5 minutos antes" -> reminderCalendar.add(Calendar.MINUTE, -5)
                                            "15 minutos antes" -> reminderCalendar.add(Calendar.MINUTE, -15)
                                            "30 minutos antes" -> reminderCalendar.add(Calendar.MINUTE, -30)
                                            "1 hora antes" -> reminderCalendar.add(Calendar.HOUR_OF_DAY, -1)
                                            "2 horas antes" -> reminderCalendar.add(Calendar.HOUR_OF_DAY, -2)
                                            "1 día antes" -> reminderCalendar.add(Calendar.DAY_OF_MONTH, -1)
                                            "2 días antes" -> reminderCalendar.add(Calendar.DAY_OF_MONTH, -2)
                                        }

                                        if (reminderCalendar.timeInMillis > System.currentTimeMillis()) {
                                            val intent = Intent(context, AlarmReceiver::class.java).apply {
                                                putExtra("TITLE", title)
                                                putExtra("MESSAGE", description)
                                            }
                                            val pendingIntent = PendingIntent.getBroadcast(
                                                context,
                                                System.currentTimeMillis().toInt(),
                                                intent,
                                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                            )

                                            alarmManager.setExactAndAllowWhileIdle(
                                                AlarmManager.RTC_WAKEUP,
                                                reminderCalendar.timeInMillis,
                                                pendingIntent
                                            )

                                            Toast.makeText(context, "Recordatorio configurado para $option.", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    // Filtrar recordatorios seleccionados y actualizar el estado
                                    selectedReminders = selectedOptions.filter { it.value }.keys.toList()

                                    // Mostrar mensaje de confirmación
                                    if (selectedReminders.isEmpty()) {
                                        Toast.makeText(context, "No se seleccionó ningún recordatorio.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Recordatorios seleccionados: $selectedReminders", Toast.LENGTH_SHORT).show()
                                    }
                                }) {
                                    Text("Aceptar")
                                }
                            }
                        )
                    }
                }
            }
        }









                    @Composable
        fun VideoPlayer(videoUri: Uri) {
            var isPlaying by remember { mutableStateOf(false) } // Controlar el estado de reproducción

            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        setVideoURI(videoUri)
                        setOnPreparedListener {
                            it.isLooping = false // El video no se repite automáticamente
                            pause() // Pausa automáticamente al cargar
                        }
                        setOnClickListener {
                            if (isPlaying) {
                                pause() // Pausa el video si ya está reproduciéndose
                            } else {
                                start() // Reproduce el video si está pausado
                            }
                            isPlaying = !isPlaying // Alterna el estado de reproducción
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Tamaño fijo para los videos
                    .clip(RoundedCornerShape(8.dp)) // Borde redondeado similar a las imágenes
            )
        }


        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Mostrar todas las imágenes
            imageUris.forEach { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Imagen",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp) // Tamaño de la imagen
                            .clip(RoundedCornerShape(8.dp)) // Bordes redondeados
                    )
                    IconButton(
                        onClick = {
                            imageUris = imageUris - uri // Eliminar la imagen de la lista
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar imagen")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar todos los videos
            fileUris.forEach { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    VideoPlayer(videoUri = uri) // Mostrar el reproductor de video
                    IconButton(
                        onClick = {
                            fileUris = fileUris - uri // Eliminar el video de la lista
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar video")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar los audios
            audioFilePaths.forEachIndexed { index, path ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Audio ${index + 1}", modifier = Modifier.weight(1f))
                    Row {
                        IconButton(onClick = { playAudio(context, path) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Reproducir audio")
                        }
                        IconButton(
                            onClick = {
                                audioFilePaths = audioFilePaths - path // Eliminar el audio de la lista
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar audio")
                        }
                    }
                }
            }
        }


        if (selectedType == "Tarea") {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recordatorios seleccionados:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // Mostrar recordatorios seleccionados
            if (selectedReminders.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    selectedReminders.forEach { reminder ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Recordatorio",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = reminder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No se han seleccionado recordatorios.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }



            Spacer(modifier = Modifier.height(8.dp))


            var showDialog by remember { mutableStateOf(false) }
            val options = listOf(
                "30 segundos antes",
                "1 minuto antes",
                "5 minutos antes",
                "15 minutos antes",
                "30 minutos antes",
                "1 hora antes",
                "2 horas antes",
                "1 día antes",
                "2 días antes"
            )



            // Botón para editar recordatorios
            Button(
                onClick = { showDialog = true }, // Reutiliza el estado `showDialog` para abrir el diálogo
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Editar recordatorios")
            }

            // Mostrar diálogo si `showDialog` está activo
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Selecciona el tiempo de recordatorio") },
                    text = {
                        Column {
                            options.forEach { option ->
                                val isOptionEnabled = remember {
                                    // Lógica para validar si la opción sigue siendo válida
                                    val calendar = Calendar.getInstance().apply {
                                        val (day, month, year) = selectedDate.split("/").map { it.toInt() }
                                        val (hour, minute) = selectedTime.split(":").map { it.toInt() }
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month - 1)
                                        set(Calendar.DAY_OF_MONTH, day)
                                        set(Calendar.HOUR_OF_DAY, hour)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }

                                    when (option) {
                                        "30 segundos antes" -> calendar.add(Calendar.SECOND, -30)
                                        "1 minuto antes" -> calendar.add(Calendar.MINUTE, -1)
                                        "5 minutos antes" -> calendar.add(Calendar.MINUTE, -5)
                                        "15 minutos antes" -> calendar.add(Calendar.MINUTE, -15)
                                        "30 minutos antes" -> calendar.add(Calendar.MINUTE, -30)
                                        "1 hora antes" -> calendar.add(Calendar.HOUR_OF_DAY, -1)
                                        "2 horas antes" -> calendar.add(Calendar.HOUR_OF_DAY, -2)
                                        "1 día antes" -> calendar.add(Calendar.DAY_OF_MONTH, -1)
                                        "2 días antes" -> calendar.add(Calendar.DAY_OF_MONTH, -2)
                                    }

                                    val now = Calendar.getInstance().apply {
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    calendar.timeInMillis > now.timeInMillis
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = isOptionEnabled) {
                                            if (isOptionEnabled) {
                                                selectedOptions[option] = !(selectedOptions[option] ?: false)
                                            }
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedOptions[option] ?: false,
                                        onCheckedChange = {
                                            if (isOptionEnabled) {
                                                selectedOptions[option] = it
                                            }
                                        },
                                        enabled = isOptionEnabled
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option,
                                        color = if (isOptionEnabled) Color.Unspecified else Color.Gray
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false

                            // Actualizar recordatorios seleccionados
                            selectedReminders = selectedOptions.filter { it.value }.keys.toList()

                            Toast.makeText(
                                context,
                                "Recordatorios actualizados: $selectedReminders",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }

    }
}


        @Composable
    fun CalendarViewComponent(selectedDate: String, onDateSelected: (String) -> Unit) {
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        onDateSelected("$dayOfMonth/${month + 1}/$year")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }