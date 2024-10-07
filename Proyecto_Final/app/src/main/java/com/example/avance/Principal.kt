package com.example.avance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Pantalla principal
@Composable
fun Principal(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // icono de búsqueda
        var searchQuery by remember { mutableStateOf("") }
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Seleccionar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatusCard("Todos", 0, Modifier.weight(1f).padding(end = 8.dp), Icons.Default.Home)
            StatusCard("Pendientes", 0, Modifier.weight(1f).padding(start = 8.dp), Icons.Default.Notifications)
        }

        Spacer(modifier = Modifier.height(16.dp))
        StatusCard("Terminados", 0, Modifier.fillMaxWidth(), Icons.Default.Check)
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de notas y tareas
        Text("MIS LISTAS", style = MaterialTheme.typography.titleLarge)

        // Cambiar para que navegue a la tercera pantalla
        SimpleListCard(
            title = "Notas",
            icon = Icons.Default.Menu,
            onClick = { navController.navigate("notesTasksScreen/Notas") } // Navega a la tercera pantalla con "Notas"
        )

        SimpleListCard(
            title = "Tareas",
            icon = Icons.Default.Menu,
            onClick = { navController.navigate("notesTasksScreen/Tareas") } // Navega a la tercera pantalla con "Tareas"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón Agregar nueva
        SimpleListCard(
            title = "Agregar nueva",
            icon = Icons.Default.Add, // Ícono de + para el botón
            onClick = { navController.navigate("secondScreen") }  // Navegar a la segunda pantalla
        )
    }
}


// Función para las tarjetas simples que se comportan como botones
@Composable
fun SimpleListCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit // Callback cuando se hace clic
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }, // Navega cuando se hace clic
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = title)  // Icono de la lista
                Spacer(modifier = Modifier.width(8.dp))
                Text(title)
            }
        }
    }
}

// Función original para las listas con ítems
@Composable
fun ListCardWithIcon(
    title: String,
    icon: ImageVector,
    items: List<String>,
    onItemClick: ((String) -> Unit)? = null // Callback para los ítems
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
                    Icon(imageVector = icon, contentDescription = title)  // Icono de la lista
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title)
                }
                if (items.isNotEmpty()) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar")  // Icono de despliegue
                }
            }
            if (expanded && items.isNotEmpty()) {
                Column {
                    items.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { onItemClick?.invoke(item) }  // Hacer el ítem clicleable
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun StatusCard(title: String, count: Int, modifier: Modifier = Modifier, icon: ImageVector) {
    Card(
        modifier = modifier.height(120.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono a la izquierda
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(32.dp)
                )
                // Número a la derecha
                Text(text = "$count", style = MaterialTheme.typography.headlineMedium)
            }
            // Título centrado en la parte inferior
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}