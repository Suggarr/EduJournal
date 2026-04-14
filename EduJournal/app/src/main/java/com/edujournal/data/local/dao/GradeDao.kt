package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.edujournal.data.local.database.entities.GradeEntity
import com.edujournal.domain.model.DisciplineGradeRecord
import com.edujournal.domain.model.JournalRow
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {

    @Query("SELECT * FROM grades WHERE lessonId = :lessonId")
    fun getGradesForLesson(lessonId: Long): Flow<List<GradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeEntity)

    @Query("""
        SELECT 
        students.id AS studentId,
        students.firstName AS studentFirstName,
        students.lastName AS studentLastName,
        lessons.id AS lessonId,
        grades.value AS gradeValue,
        grades.type AS gradeType
        FROM students
        CROSS JOIN lessons
        LEFT JOIN grades
        ON grades.studentId = students.id
        AND grades.lessonId = lessons.id
        WHERE students.groupId = :groupId
        AND lessons.groupId = :groupId
        AND lessons.subjectId = :subjectId
        AND lessons.subjectLessonTypeId = :subjectLessonTypeId
        AND lessons.semesterId = :semesterId
        ORDER BY students.lastName, students.firstName, lessons.date
        """)
    fun getJournal(
        groupId: Long,
        subjectId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): Flow<List<JournalRow>>

    @Query(
        """
        SELECT
            students.id AS studentId,
            students.firstName AS studentFirstName,
            students.lastName AS studentLastName,
            lesson_types.name AS lessonTypeName,
            grades.value AS gradeValue,
            grades.type AS gradeType
        FROM students
        LEFT JOIN lessons
            ON lessons.groupId = students.groupId
            AND lessons.subjectId = :subjectId
            AND lessons.semesterId = :semesterId
        LEFT JOIN lesson_types
            ON lesson_types.id = lessons.subjectLessonTypeId
        LEFT JOIN grades
            ON grades.studentId = students.id
            AND grades.lessonId = lessons.id
        WHERE students.groupId = :groupId
        ORDER BY students.lastName, students.firstName, lessons.date
        """
    )
    fun observeDisciplineGrades(
        groupId: Long,
        subjectId: Long,
        semesterId: Long
    ): Flow<List<DisciplineGradeRecord>>
}

