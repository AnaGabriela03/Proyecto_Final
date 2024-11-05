package com.example.avance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoteTaskViewModelFactory(private val repository: NoteTaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteTaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

