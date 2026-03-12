package com.edujournal.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.edujournal.data.local.dao.GradeDao
import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.dao.LessonDao
import com.edujournal.data.local.dao.LessonTypeDao
import com.edujournal.data.local.dao.StudentDao
import com.edujournal.data.local.dao.SubjectDao
import com.edujournal.data.local.database.entities.GradeEntity
import com.edujournal.data.local.database.entities.GroupEntity
import com.edujournal.data.local.database.entities.LessonEntity
import com.edujournal.data.local.database.entities.LessonTypeEntity
import com.edujournal.data.local.database.entities.StudentEntity
import com.edujournal.data.local.database.entities.SubjectEntity
import com.edujournal.utils.Converters

@Database(
    entities = [
        GroupEntity::class,
        StudentEntity::class,
        SubjectEntity::class,
        LessonTypeEntity::class,
        LessonEntity::class,
        GradeEntity:: class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun studentDao(): StudentDao
    abstract fun subjectDao(): SubjectDao
    abstract fun lessonTypeDao(): LessonTypeDao
    abstract fun lessonDao(): LessonDao
    abstract fun gradeDao(): GradeDao
}