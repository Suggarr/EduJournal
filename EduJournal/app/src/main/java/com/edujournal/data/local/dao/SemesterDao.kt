package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edujournal.data.local.database.entities.SemesterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SemesterDao {
    @Query(
        """
        SELECT * FROM semesters
        ORDER BY year ASC,
        CASE season
            WHEN 'SPRING' THEN 0
            WHEN 'AUTUMN' THEN 1
            ELSE 2
        END ASC
        """
    )
    fun observeSemesters(): Flow<List<SemesterEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(semester: SemesterEntity): Long

    @Update
    suspend fun update(semester: SemesterEntity)

    @Query("DELETE FROM semesters WHERE id = :semesterId")
    suspend fun deleteById(semesterId: Long)
}

