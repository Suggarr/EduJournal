package com.edujournal.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects",
    indices = [Index(value = ["name"], unique = true)]
)
data class SubjectEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val abbreviation: String?
)
