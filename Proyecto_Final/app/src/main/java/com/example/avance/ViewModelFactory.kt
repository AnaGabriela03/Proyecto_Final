package com.example.avance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoteTaskViewModelFactory(private val noteTaskDao: NoteTaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteTaskViewModel(noteTaskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
