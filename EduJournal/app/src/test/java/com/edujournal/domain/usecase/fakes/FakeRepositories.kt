package com.edujournal.domain.usecase.fakes

import com.edujournal.domain.model.Group
import com.edujournal.domain.model.Lesson
import com.edujournal.domain.model.Student
import com.edujournal.domain.model.Subject
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.domain.repository.LessonRepository
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.domain.repository.TopicTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeStudentRepository : StudentRepository {
    var createResult: Long = 1L
    var updateResult: Int = 1
    val createdStudents = mutableListOf<Student>()
    val updatedStudents = mutableListOf<Student>()

    override fun observeStudents(groupId: Long): Flow<List<Student>> = flowOf(emptyList())
    override suspend fun createStudent(student: Student): Long { createdStudents.add(student); return createResult }
    override suspend fun updateStudent(student: Student): Int { updatedStudents.add(student); return updateResult }
    override suspend fun deleteStudent(studentId: Long) = Unit
}

class FakeTopicTemplateRepository : TopicTemplateRepository {
    var createResult: Long = 1L
    var updateResult: Int = 1
    val created = mutableListOf<TopicTemplate>()
    val updated = mutableListOf<TopicTemplate>()

    override fun observeByContext(semesterId: Long, subjectLessonTypeId: Long): Flow<List<TopicTemplate>> = flowOf(emptyList())
    override suspend fun create(item: TopicTemplate): Long { created.add(item); return createResult }
    override suspend fun update(item: TopicTemplate): Int { updated.add(item); return updateResult }
    override suspend fun deleteById(id: Long) = Unit
}

class FakeGroupRepository : GroupRepository {
    var createResult: Long = 1L
    var updateResult: Int = 1
    val created = mutableListOf<Group>()
    val updated = mutableListOf<Group>()

    override fun getGroups(): Flow<List<Group>> = flowOf(emptyList())
    override suspend fun createGroup(group: Group): Long { created.add(group); return createResult }
    override suspend fun updateGroup(group: Group): Int { updated.add(group); return updateResult }
    override suspend fun deleteGroup(id: Long) = Unit
}

class FakeSubjectRepository : SubjectRepository {
    var createSubjectResult = 1L
    var updateResult = 1
    var replacedSubjectId: Long = -1L
    var replacedSemesterIds: List<Long> = emptyList()
    val created = mutableListOf<Subject>()
    val updated = mutableListOf<Subject>()

    override fun observeSubjects(): Flow<List<Subject>> = flowOf(emptyList())
    override fun observeSubjectsBySemester(semesterId: Long): Flow<List<Subject>> = flowOf(emptyList())
    override fun observeSemesterIdsBySubject(subjectId: Long): Flow<List<Long>> = flowOf(emptyList())
    override suspend fun createSubject(subject: Subject): Long { created.add(subject); return createSubjectResult }
    override suspend fun updateSubject(subject: Subject): Int { updated.add(subject); return updateResult }
    override suspend fun replaceSubjectSemesters(subjectId: Long, semesterIds: List<Long>) { replacedSubjectId = subjectId; replacedSemesterIds = semesterIds }
    override suspend fun deleteSubject(subjectId: Long) = Unit
}

class FakeSubjectLessonTypeRepository : SubjectLessonTypeRepository {
    var deleteLessonTypeId: Long = -1L
    var observeResult: List<SubjectLessonType> = emptyList()
    var observeByIdResult: SubjectLessonType? = null
    var getByIdResult: SubjectLessonType? = null
    var createResult: Long = 1L
    var updateResult: Int = 1
    val updated = mutableListOf<SubjectLessonType>()
    val created = mutableListOf<SubjectLessonType>()

    override fun observeLessonTypes(subjectId: Long): Flow<List<SubjectLessonType>> = flowOf(observeResult)
    override fun observeById(id: Long): Flow<SubjectLessonType?> = flowOf(observeByIdResult)
    override suspend fun getById(id: Long): SubjectLessonType? = getByIdResult
    override suspend fun createLessonType(SubjectLessonType: SubjectLessonType): Long { created.add(SubjectLessonType); return createResult }
    override suspend fun updateLessonType(SubjectLessonType: SubjectLessonType): Int { updated.add(SubjectLessonType); return updateResult }
    override suspend fun deleteLessonType(typeId: Long) { deleteLessonTypeId = typeId }
}

class FakeLessonRepository : LessonRepository {
    var deletedLessonId: Long = -1L
    var observeLessonsResult: List<Lesson> = emptyList()
    var observeLessonByIdResult: Lesson? = null
    var insertResult: Long = 1L
    var updateResult: Int = 1
    val insertedLessons = mutableListOf<Lesson>()
    val updatedLessons = mutableListOf<Lesson>()

    override fun observeLessons(groupId: Long, subjectLessonTypeId: Long, semesterId: Long): Flow<List<Lesson>> = flowOf(observeLessonsResult)
    override fun observeLessonById(lessonId: Long): Flow<Lesson?> = flowOf(observeLessonByIdResult)
    override suspend fun insertLesson(lesson: Lesson): Long { insertedLessons.add(lesson); return insertResult }
    override suspend fun updateLesson(lesson: Lesson): Int { updatedLessons.add(lesson); return updateResult }
    override suspend fun deleteLesson(lessonId: Long) { deletedLessonId = lessonId }
}
