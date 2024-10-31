package com.example.avance

import android.app.TimePickerDialog
import android.net.Uri
import android.widget.CalendarView
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@Composable
fun segunda_pantalla(
    navController: NavController,
    noteId: Int,
    initialType: String,  // Tipo inicial que viene de la navegación
    viewModel: NoteTaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.Gray else Color.LightGray

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(initialType) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isCalendarVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Seleccione una fecha") }
    var selectedTime by remember { mutableStateOf("Seleccione una hora") }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { /* Lógica para manejar el archivo seleccionado */ } }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> bitmap?.let { /* Lógica para manejar la imagen capturada */ } }

    LaunchedEffect(noteId) {
        if (noteId != -1) {
            val note = viewModel.notesTasks.value.find { it.id == noteId }
            note?.let {
                title = it.title
                description = it.description
                selectedType = it.type
                val parts = it.date?.split(" ") ?: listOf("Seleccione una fecha", "Seleccione una hora")
                selectedDate = parts.getOrElse(0) { "Seleccione una fecha" }
                selectedTime = parts.getOrElse(1) { "Seleccione una hora" }
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.cancelar), color = textColor)
            }
            Text(
                text = if (noteId == -1) stringResource(R.string.nueva_nota) else stringResource(R.string.editar_nota),
                style = MaterialTheme.typography.titleLarge.copy(color = textColor)
            )
            TextButton(onClick = {
                if (title.isEmpty()) {
                    Toast.makeText(context,
                        context.getString(R.string.el_t_tulo_es_obligatorio), Toast.LENGTH_SHORT).show()
                } else if (selectedType == "Tarea" && (selectedDate == "Seleccione una fecha" || selectedTime == "Seleccione una hora")) {
                    Toast.makeText(context,
                        context.getString(R.string.la_fecha_y_la_hora_son_obligatorias_para_tareas), Toast.LENGTH_SHORT).show()
                } else {
                    val dateTime = if (selectedType == "Tarea") {
                        "$selectedDate $selectedTime"
                    } else {
                        null
                    }
                    val newNoteTask = NoteTask(
                        id = if (noteId == -1) 0 else noteId,
                        title = title,
                        description = description,
                        date = dateTime,
                        type = selectedType
                    )
                    viewModel.addOrUpdateNoteTask(newNoteTask)
                    navController.popBackStack()
                }
            }) {
                Text(if (noteId == -1) stringResource(R.string.agregar) else stringResource(R.string.guardar), color = textColor)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.t_tulo), fontSize = 14.sp, color = textColor)
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
                Text(text = stringResource(R.string.descripci_n), fontSize = 14.sp, color = textColor)
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

        // Ocultar el selector de tipo cuando ya se ha definido
        // Dentro del bloque de la UI de selección de tipo
        if (noteId == -1 && initialType.isEmpty()) {
            Box {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDropdownExpanded = true }
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E3E9))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Tipo", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = selectedType.ifEmpty { stringResource(R.string.seleccionar_tipo) }, color = Color.Black) // Mostrar "Seleccionar tipo" si está vacío
                        }
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Abrir menú", tint = Color.Black)
                    }
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.tarea), color = if (isDarkTheme) Color.White else Color.Black) },
                        onClick = {
                            selectedType = "Tarea"  // Actualiza el texto a "Tarea"
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.nota), color = if (isDarkTheme) Color.White else Color.Black) },
                        onClick = {
                            selectedType = "Nota"  // Actualiza el texto a "Nota"
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedType == "Tarea") {
                    IconButton(onClick = { isCalendarVisible = !isCalendarVisible }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = textColor)
                    }
                }
                IconButton(onClick = { fileLauncher.launch("*/*") }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Adjuntar archivo", tint = textColor)
                }
                IconButton(onClick = { cameraLauncher.launch(null) }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Cámara", tint = textColor)
                }
            }
        }

        if (isCalendarVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.fecha_y_hora),
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AndroidView(
                        factory = { context ->
                            CalendarView(context).apply {
                                setOnDateChangeListener { _, year, month, dayOfMonth ->
                                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(Calendar.MINUTE)

                                TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                                }, hour, minute, true).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                        ) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = "Hora", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.hora), color = Color.Black)
                        }

                        Button(
                            onClick = { isCalendarVisible = false },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                        ) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Recordar", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.recordar), color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}