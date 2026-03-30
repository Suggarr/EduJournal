package com.edujournal.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subject_lesson_type_hours",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LessonTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subjectId", "lessonTypeId"], unique = true),
        Index(value = ["subjectId"]),
        Index(value = ["lessonTypeId"])
    ]
)
data class SubjectLessonTypeHoursEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val lessonTypeId: Long,
    val hours: Double?
)
