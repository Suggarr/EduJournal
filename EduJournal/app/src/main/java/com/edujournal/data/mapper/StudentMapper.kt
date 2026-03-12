package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.StudentEntity
import com.edujournal.domain.model.Student

fun StudentEntity.toDomain(): Student{
    return Student(
        id = id,
        firstName = firstName,
        lastName = lastName,
        middleName = middleName,
        groupId = groupId
    )
}

fun Student.toEntity(): StudentEntity{
    return StudentEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        middleName = middleName,
        groupId = groupId
    )
}