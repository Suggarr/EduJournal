package com.edujournal.data.local.database.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(@PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String
)