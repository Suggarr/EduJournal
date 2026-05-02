package com.edujournal.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "topic_templates",
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubjectLessonTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectLessonTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("semesterId"),
        Index("subjectLessonTypeId"),
        Index(value = ["semesterId", "subjectLessonTypeId", "orderInType"], unique = true)
    ]
)
data class TopicTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val semesterId: Long,
    val subjectLessonTypeId: Long,
    val title: String,
    val orderInType: Int
)


