package com.example.avance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NoteTaskViewModel(private val noteTaskDao: NoteTaskDao) : ViewModel() {

    private val _notesTasks = MutableStateFlow<List<NoteTask>>(emptyList())
    val notesTasks: StateFlow<List<NoteTask>> = _notesTasks

    fun loadNotesTasks(isTaskSelected: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _notesTasks.value = if (isTaskSelected) {
                noteTaskDao.getAllTasks()
            } else {
                noteTaskDao.getAllNotes()
            }
        }
    }

    fun addOrUpdateNoteTask(noteTask: NoteTask) {
        viewModelScope.launch(Dispatchers.IO) {
            if (noteTask.id == 0) {
                noteTaskDao.insert(noteTask)
            } else {
                noteTaskDao.update(noteTask)
            }
            loadNotesTasks(noteTask.type == "Tarea")
        }
    }

    fun deleteNoteTask(noteTask: NoteTask) {
        viewModelScope.launch(Dispatchers.IO) {
            noteTaskDao.delete(noteTask)
            loadNotesTasks(noteTask.type == "Tarea")  // Recarga los datos
        }
    }
}