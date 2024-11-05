package com.example.avance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NoteTaskViewModel(private val repository: NoteTaskRepository) : ViewModel() {

    private val _notesTasks = MutableStateFlow<List<NoteTask>>(emptyList())
    val notesTasks: StateFlow<List<NoteTask>> = _notesTasks

    fun loadNotesTasks(isTaskSelected: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _notesTasks.value = if (isTaskSelected) {
                repository.getAllTasks()
            } else {
                repository.getAllNotes()
            }
        }
    }

    fun addOrUpdateNoteTask(noteTask: NoteTask) {
        viewModelScope.launch(Dispatchers.IO) {
            if (noteTask.id == 0) {
                repository.insert(noteTask)
            } else {
                repository.update(noteTask)
            }
            loadNotesTasks(noteTask.type == "Tarea")
        }
    }

    fun deleteNoteTask(noteTask: NoteTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(noteTask)
            loadNotesTasks(noteTask.type == "Tarea")
        }
    }
}
