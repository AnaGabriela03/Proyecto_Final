package com.example.avance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteTask::class], version = 8)
abstract class NoteTaskDatabase : RoomDatabase() {
    abstract fun noteTaskDao(): NoteTaskDao

    companion object {
        @Volatile
        private var INSTANCE: NoteTaskDatabase? = null

        fun getDatabase(context: Context): NoteTaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteTaskDatabase::class.java,
                    "note_task_database"
                )
                    .fallbackToDestructiveMigration() // Esto elimina cualquier migración, pero recrea la base de datos si es necesario
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
