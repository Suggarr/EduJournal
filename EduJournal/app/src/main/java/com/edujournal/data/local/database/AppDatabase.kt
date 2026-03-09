package com.edujournal.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.database.entities.GroupEntity

@Database(
    entities = [GroupEntity:: class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
}