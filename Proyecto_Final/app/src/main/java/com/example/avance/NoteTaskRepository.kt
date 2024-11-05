package com.example.avance

class NoteTaskRepository(private val noteTaskDao: NoteTaskDao) {

    suspend fun insert(noteTask: NoteTask) {
        noteTaskDao.insert(noteTask)
    }

    suspend fun update(noteTask: NoteTask) {
        noteTaskDao.update(noteTask)
    }

    suspend fun delete(noteTask: NoteTask) {
        noteTaskDao.delete(noteTask)
    }

    suspend fun getNoteById(id: Int): NoteTask? {
        return noteTaskDao.getNoteById(id)
    }

    suspend fun getAllNotes(): List<NoteTask> {
        return noteTaskDao.getAllNotes()
    }

    suspend fun getAllTasks(): List<NoteTask> {
        return noteTaskDao.getAllTasks()
    }
}
