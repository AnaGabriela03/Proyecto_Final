package com.example.avance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.isSystemInDarkTheme


@Composable
fun tercera_pantalla(title: String, items: List<String>, navController: NavController) {
    val isDarkTheme = isSystemInDarkTheme() // Detecta si el sistema está en modo oscuro
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)
    val borderColor = if (isDarkTheme) Color.LightGray else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la lista en la parte superior
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(color = textColor),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar lista de tareas
        items.forEachIndexed { index, item ->
            TaskItem(
                title = "Título de la tarea $index",
                description = "Descripción $index",
                date = "Fecha $index",
                textColor = textColor,
                backgroundColor = backgroundColor,
                borderColor = borderColor
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sección de botones con borde en la parte inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))  // Borde dinámico
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = textColor)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Editar", tint = textColor)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Cámara", tint = textColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón Agregar nueva en la parte inferior
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
            Text("Agregar nueva", color = textColor)
        }
    }
}

@Composable
fun TaskItem(title: String, description: String, date: String, textColor: Color, backgroundColor: Color, borderColor: Color) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = false, onClick = {})
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(color = textColor))
                Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
                Text(text = date, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sección de imágenes
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Img", color = textColor)
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Img", color = textColor)
            }
        }

        Divider(
            color = borderColor,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}