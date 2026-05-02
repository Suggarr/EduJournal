package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edujournal.data.local.database.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("""
        SELECT * FROM students 
        WHERE groupId = :groupId
        ORDER BY lastName
    """)
    fun getByGroup(groupId: Long): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(student: StudentEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(student: StudentEntity): Int

    @Query("SELECT EXISTS(SELECT 1 FROM students WHERE id = :id)")
    suspend fun existsById(id: Long): Boolean

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM students
            WHERE groupId = :groupId
              AND lastName = :lastName
              AND firstName = :firstName
              AND IFNULL(middleName, '') = IFNULL(:middleName, '')
        )
        """
    )
    suspend fun existsByFullNameInGroup(
        groupId: Long,
        lastName: String,
        firstName: String,
        middleName: String?
    ): Boolean

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM students
            WHERE groupId = :groupId
              AND lastName = :lastName
              AND firstName = :firstName
              AND IFNULL(middleName, '') = IFNULL(:middleName, '')
              AND id != :id
        )
        """
    )
    suspend fun existsByFullNameInGroupExceptId(
        id: Long,
        groupId: Long,
        lastName: String,
        firstName: String,
        middleName: String?
    ): Boolean

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Long)
}

