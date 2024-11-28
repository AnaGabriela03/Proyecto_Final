package com.example.avance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notes_Tasks")
data class NoteTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: String? = null,
    val type: String,
    val imageUri: String?, // Imágenes
    val fileUri: String?,  // Videos
    val audioUri: String?, // Audios
    val reminders: String? // Nuevos recordatorios seleccionados
)
