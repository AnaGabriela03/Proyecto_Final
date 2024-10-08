package com.example.avance

import android.widget.CalendarView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontWeight


// Segunda pantalla
@Composable
fun segunda_pantalla(navController: NavController) {
    val isDarkTheme = isSystemInDarkTheme() // Detecta si el sistema está en modo oscuro
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.Gray else Color.LightGray

    var selectedListOption by remember { mutableStateOf("Selecciona una lista") }
    var expanded by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Seleccione una fecha") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Cancelar, Nueva nota, Agregar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Cancelar", color = textColor)  // Cambia el color según el tema
            }
            Text(text = "Nueva nota", style = MaterialTheme.typography.titleLarge.copy(color = textColor)) // Color dinámico
            TextButton(onClick = { }) {
                Text("Agregar", color = textColor)  // Cambia el color según el tema
            }
        }

        // Título y Descripción con borde
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))  // Borde dinámico
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "TITULO", fontSize = 14.sp, color = textColor) // Color dinámico
                BasicTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)), // Fondo dinámico
                    textStyle = TextStyle(fontSize = 18.sp, color = textColor) // Color dinámico
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Descripción", fontSize = 14.sp, color = textColor) // Color dinámico
                BasicTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)), // Fondo dinámico
                    textStyle = TextStyle(fontSize = 16.sp, color = textColor) // Color dinámico
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selecciona una lista
        ListCardWithIcon(
            title = "   lista",
            icon = Icons.Default.Menu,  // Aquí se usa un ícono para la lista
            items = listOf("Tarea", "Nota"),
            textColor = textColor  // Pasar color dinámico
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de íconos con borde alrededor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))  // Borde dinámico
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = textColor) // Icono dinámico
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Clip", tint = textColor) // Icono dinámico
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Cámara", tint = textColor) // Icono dinámico
                }
            }
        }

        // Borde alrededor del calendario y los botones
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp)) // Borde dinámico
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Fecha y hora",
                    fontWeight = FontWeight.Bold,
                    color = textColor,  // Texto dinámico
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Calendario
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(16.dp)
                ) {
                    AndroidView(
                        factory = { context ->
                            CalendarView(context).apply {
                                setOnDateChangeListener { _, year, month, dayOfMonth ->
                                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones Hora y Recordar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .clickable {},
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = "Hora", tint = textColor) // Icono dinámico
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hora", color = textColor) // Texto dinámico
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .clickable {},
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = "Recordar", tint = textColor) // Icono dinámico
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Recordar", color = textColor) // Texto dinámico
                        }
                    }
                }
            }
        }
    }
}

// Componente para la lista
@Composable
fun ListCardWithIcon(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>,
    textColor: Color // Pasar el color dinámico
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = title, tint = textColor)  // Icono dinámico
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, color = textColor)  // Texto dinámico
                }
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Desplegar",
                    tint = textColor  // Cambia color del icono
                )
            }
            if (expanded && items.isNotEmpty()) {
                Column {
                    items.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { /* acción de clic */ },
                            color = textColor // Texto dinámico para ítems
                        )
                    }
                }
            }
        }
    }
}
