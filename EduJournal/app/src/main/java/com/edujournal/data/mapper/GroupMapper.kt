package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.GroupEntity
import com.edujournal.domain.model.Group

fun GroupEntity.toDomain(): Group {
    return Group(
        id = id,
        name = name
    )
}

fun Group.toEntity(): GroupEntity{
    return GroupEntity(
        id = id,
        name = name
    )
}