package com.edujournal.presentation.viewmodel

import com.edujournal.domain.model.DisciplineGradeRecord
import com.edujournal.domain.model.Group
import com.edujournal.domain.model.Subject
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.model.enum.GradeType
import com.edujournal.domain.repository.GradeRepository
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.domain.usecase.grade.ObserveDisciplineGradesUseCase
import com.edujournal.domain.usecase.group.GetGroupsUseCase
import com.edujournal.domain.usecase.subject.ObserveSubjectsUseCase
import com.edujournal.domain.usecase.subjectlessontype.ObserveSubjectLessonTypesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `average grade is calculated from numeric grades`() = runTest(dispatcher) {
        val viewModel = createViewModel(
            grades = listOf(
                gradeRecord(gradeValue = 6, lessonId = 1L),
                gradeRecord(gradeValue = 8, lessonId = 2L),
                gradeRecord(gradeValue = 10, lessonId = 3L)
            )
        )

        val state = viewModel.observeState(groupId = 1L, subjectId = 1L, semesterId = 1L)
            .filterNotNull()
            .first()

        assertEquals(8.0, state.groupAverage!!, 0.0)
    }

    @Test
    fun `attendance percent is calculated from marked lessons`() = runTest(dispatcher) {
        val grades = (1L..8L).map { lessonId ->
            gradeRecord(gradeValue = null, gradeType = null, lessonId = lessonId)
        } + listOf(
            gradeRecord(gradeValue = null, gradeType = GradeType.ABSENT, lessonId = 9L),
            gradeRecord(gradeValue = null, gradeType = GradeType.ABSENT, lessonId = 10L)
        )
        val viewModel = createViewModel(grades = grades)

        val state = viewModel.observeState(groupId = 1L, subjectId = 1L, semesterId = 1L)
            .filterNotNull()
            .first()

        assertEquals(10, state.attendanceMarkedCount)
        assertEquals(8, state.attendancePresentCount)
        assertEquals(2, state.attendanceAbsentCount)
        assertEquals(80.0, state.attendancePercent!!, 0.0)
    }

    @Test
    fun `grade distribution counts repeated grades`() = runTest(dispatcher) {
        val viewModel = createViewModel(
            grades = listOf(
                gradeRecord(gradeValue = 4, lessonId = 1L),
                gradeRecord(gradeValue = 4, lessonId = 2L),
                gradeRecord(gradeValue = 7, lessonId = 3L)
            )
        )

        val state = viewModel.observeState(groupId = 1L, subjectId = 1L, semesterId = 1L)
            .filterNotNull()
            .first()

        assertEquals(2, state.gradeDistribution.first { it.grade == 4 }.count)
        assertEquals(1, state.gradeDistribution.first { it.grade == 7 }.count)
        assertEquals(0, state.gradeDistribution.first { it.grade == 5 }.count)
    }

    @Test
    fun `student with average below debtor threshold is included in debtors`() = runTest(dispatcher) {
        val viewModel = createViewModel(
            grades = listOf(
                gradeRecord(
                    studentId = 1L,
                    studentFirstName = "Иван",
                    studentLastName = "Иванов",
                    gradeValue = 3,
                    lessonId = 1L
                ),
                gradeRecord(
                    studentId = 2L,
                    studentFirstName = "Петр",
                    studentLastName = "Петров",
                    gradeValue = 8,
                    lessonId = 1L
                )
            )
        )

        val state = viewModel.observeState(groupId = 1L, subjectId = 1L, semesterId = 1L)
            .filterNotNull()
            .first()

        assertEquals(1, state.debtors.size)
        assertEquals(1L, state.debtors.first().studentId)
        assertTrue(state.debtors.first().studentName.contains("Иванов"))
    }

    private fun createViewModel(
        grades: List<DisciplineGradeRecord>
    ): AnalyticsViewModel {
        val gradeRepository = mockk<GradeRepository>()
        val subjectRepository = mockk<SubjectRepository>()
        val groupRepository = mockk<GroupRepository>()
        val lessonTypeRepository = mockk<SubjectLessonTypeRepository>()

        every { gradeRepository.observeDisciplineGrades(1L, 1L, 1L) } returns flowOf(grades)
        every { subjectRepository.observeSubjects() } returns flowOf(listOf(Subject(1L, "Математика", "МАТ")))
        every { groupRepository.getGroups() } returns flowOf(listOf(Group(1L, "1070")))
        every { lessonTypeRepository.observeLessonTypes(1L) } returns flowOf(
            listOf(SubjectLessonType(1L, 1L, "Практика", 10.0))
        )

        return AnalyticsViewModel(
            observeDisciplineGradesUseCase = ObserveDisciplineGradesUseCase(gradeRepository),
            observeSubjectsUseCase = ObserveSubjectsUseCase(subjectRepository),
            getGroupsUseCase = GetGroupsUseCase(groupRepository),
            observeSubjectLessonTypesUseCase = ObserveSubjectLessonTypesUseCase(lessonTypeRepository)
        )
    }

    private fun gradeRecord(
        studentId: Long = 1L,
        studentFirstName: String = "Иван",
        studentLastName: String = "Иванов",
        gradeValue: Int?,
        gradeType: GradeType? = GradeType.GRADE,
        lessonId: Long
    ): DisciplineGradeRecord {
        return DisciplineGradeRecord(
            studentId = studentId,
            studentFirstName = studentFirstName,
            studentLastName = studentLastName,
            lessonId = lessonId,
            lessonDate = LocalDate.of(2026, 5, lessonId.toInt()),
            lessonTypeName = "Практика",
            gradeValue = gradeValue,
            gradeType = gradeType?.name
        )
    }
}
