package com.edujournal.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "homework_submissions",
    foreignKeys = [
        ForeignKey(
            entity = HomeworkEntity::class,
            parentColumns = ["id"],
            childColumns = ["homeworkId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("homeworkId"),
        Index("studentId"),
        Index(value = ["homeworkId", "studentId"], unique = true)
    ]
)
data class HomeworkSubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val homeworkId: Long,
    val studentId: Long,
    val status: String
)


