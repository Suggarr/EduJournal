package com.edujournal.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.edujournal.data.local.dao.GradeDao
import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.dao.HomeworkDao
import com.edujournal.data.local.dao.HomeworkSubmissionDao
import com.edujournal.data.local.dao.LessonDao
import com.edujournal.data.local.dao.SubjectLessonTypeDao
import com.edujournal.data.local.dao.SemesterDao
import com.edujournal.data.local.dao.StudentDao
import com.edujournal.data.local.dao.SubjectDao
import com.edujournal.data.local.dao.TopicTemplateDao
import com.edujournal.data.local.database.entities.GradeEntity
import com.edujournal.data.local.database.entities.GroupEntity
import com.edujournal.data.local.database.entities.HomeworkEntity
import com.edujournal.data.local.database.entities.HomeworkSubmissionEntity
import com.edujournal.data.local.database.entities.LessonEntity
import com.edujournal.data.local.database.entities.SubjectLessonTypeEntity
import com.edujournal.data.local.database.entities.SemesterEntity
import com.edujournal.data.local.database.entities.StudentEntity
import com.edujournal.data.local.database.entities.SubjectEntity
import com.edujournal.data.local.database.entities.SubjectSemesterEntity
import com.edujournal.data.local.database.entities.TopicTemplateEntity
import com.edujournal.utils.Converters

@Database(
    entities = [
        GroupEntity::class,
        StudentEntity::class,
        SubjectEntity::class,
        SemesterEntity::class,
        SubjectSemesterEntity::class,
        SubjectLessonTypeEntity::class,
        TopicTemplateEntity::class,
        LessonEntity::class,
        GradeEntity:: class,
        HomeworkEntity::class,
        HomeworkSubmissionEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun studentDao(): StudentDao
    abstract fun subjectDao(): SubjectDao
    abstract fun semesterDao(): SemesterDao
    abstract fun lessonTypeDao(): SubjectLessonTypeDao
    abstract fun lessonDao(): LessonDao
    abstract fun gradeDao(): GradeDao
    abstract fun topicTemplateDao(): TopicTemplateDao
    abstract fun homeworkDao(): HomeworkDao
    abstract fun homeworkSubmissionDao(): HomeworkSubmissionDao
}



