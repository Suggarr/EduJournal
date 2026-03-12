package com.edujournal.data.local.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName="lessons",
    indices = [
        Index(
            value = ["groupId", "subjectId", "date"],
            unique = true
        )
    ]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val groupId: Long,
    val subjectId: Long,
    val lessonTypeId: Long,
    val date: LocalDate,
    val topic: String
)
