package com.edujournal.domain.usecase.group

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(group: Group): EntityWriteResult {
        val normalizedGroup = group.copy(name = group.name.normalizeSpaces())
        return try {
            val updated = repository.updateGroup(normalizedGroup)
            if (updated == 0) EntityWriteResult.NOT_FOUND else EntityWriteResult.SUCCESS
        } catch (_: SQLiteConstraintException) {
            EntityWriteResult.DUPLICATE
        }
    }
}
