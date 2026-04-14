package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.edujournal.data.local.database.entities.SubjectEntity
import com.edujournal.data.local.database.entities.SubjectSemesterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects")
    fun observeSubjects(): Flow<List<SubjectEntity>>

    @Query(
        """
        SELECT subjects.* FROM subjects
        INNER JOIN subject_semesters ON subject_semesters.subjectId = subjects.id
        WHERE subject_semesters.semesterId = :semesterId
        ORDER BY subjects.name
        """
    )
    fun observeSubjectsBySemester(semesterId: Long): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(subject: SubjectEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(subject: SubjectEntity): Int

    @Query("SELECT EXISTS(SELECT 1 FROM subjects WHERE id = :id)")
    suspend fun existsById(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM subjects WHERE name = :name)")
    suspend fun existsByName(name: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM subjects WHERE name = :name AND id != :id)")
    suspend fun existsByNameExceptId(name: String, id: Long): Boolean

    @Query("SELECT semesterId FROM subject_semesters WHERE subjectId = :subjectId ORDER BY semesterId")
    fun observeSemesterIdsBySubject(subjectId: Long): Flow<List<Long>>

    @Query("DELETE FROM subject_semesters WHERE subjectId = :subjectId")
    suspend fun deleteSemestersBySubjectId(subjectId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubjectSemesters(items: List<SubjectSemesterEntity>)

    @Transaction
    suspend fun replaceSemestersBySubjectId(subjectId: Long, semesterIds: List<Long>) {
        deleteSemestersBySubjectId(subjectId)
        if (semesterIds.isNotEmpty()) {
            insertSubjectSemesters(
                semesterIds.distinct().map { semesterId ->
                    SubjectSemesterEntity(
                        subjectId = subjectId,
                        semesterId = semesterId
                    )
                }
            )
        }
    }

    @Query("DELETE FROM subjects WHERE id = :subjectId")
    suspend fun deleteById(subjectId: Long)
}
