package com.trusov.notionwidget.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trusov.notionwidget.data.dto.filter.db_model.FilterDbModel
import com.trusov.notionwidget.data.dto.note.NoteDbModel
import com.trusov.notionwidget.di.ApplicationScope

@ApplicationScope
@Database(
    entities = [
        NoteDbModel::class,
        FilterDbModel::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun filtersDao(): FiltersDao

    companion object {
        private var db: AppDatabase? = null
        private const val DB_NAME = "app.db"
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                db = instance
                return instance
            }
        }
    }


}