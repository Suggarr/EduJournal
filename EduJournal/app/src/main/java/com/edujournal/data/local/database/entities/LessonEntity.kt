package com.edujournal.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
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
        ),
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("groupId"),
        Index("subjectId"),
        Index("lessonTypeId"),
        Index("semesterId"),
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
    val semesterId: Long = 1L,
    val date: LocalDate,
    val topic: String
)
