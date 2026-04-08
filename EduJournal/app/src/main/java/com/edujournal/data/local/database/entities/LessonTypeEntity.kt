package com.edujournal.data.local.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lesson_types",
    indices = [Index(value = ["name"], unique = true)]
)
data class LessonTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String
)
