package com.edujournal.domain.usecase.semester

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.Semester
import com.edujournal.domain.repository.SemesterRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import javax.inject.Inject

class UpdateSemesterUseCase @Inject constructor(
    private val repository: SemesterRepository
) {
    suspend operator fun invoke(semester: Semester): EntityWriteResult {
        require(semester.year > 0) { "INVALID_YEAR" }
        return try {
            val updatedRows = repository.updateSemester(semester)
            if (updatedRows == 0) EntityWriteResult.NOT_FOUND else EntityWriteResult.SUCCESS
        } catch (_: SQLiteConstraintException) {
            EntityWriteResult.DUPLICATE
        }
    }
}




