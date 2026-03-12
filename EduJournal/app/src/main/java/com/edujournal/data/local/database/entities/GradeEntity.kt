package com.edujournal.data.local.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grades",
    indices = [
        Index(
            value = ["studentId", "lessonId"],
            unique = true
        )
    ]
)
data class GradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val studentId: Long,

    val lessonId: Long,

    val value: Int?,

    val type: String,

    val comment: String?
)