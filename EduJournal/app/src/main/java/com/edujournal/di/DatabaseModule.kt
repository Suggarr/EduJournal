package com.edujournal.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.edujournal.data.local.dao.GradeDao
import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.dao.HomeworkDao
import com.edujournal.data.local.dao.HomeworkSubmissionDao
import com.edujournal.data.local.dao.LessonDao
import com.edujournal.data.local.dao.SubjectLessonTypeDao
import com.edujournal.data.local.dao.SemesterDao
import com.edujournal.data.local.dao.StudentDao
import com.edujournal.data.local.dao.SubjectDao
import com.edujournal.data.local.database.AppDatabase
import com.edujournal.data.local.datasource.GradeLocalDataSource
import com.edujournal.data.local.datasource.GroupLocalDataSource
import com.edujournal.data.local.datasource.HomeworkLocalDataSource
import com.edujournal.data.local.datasource.HomeworkSubmissionLocalDataSource
import com.edujournal.data.local.datasource.LessonLocalDataSource
import com.edujournal.data.local.datasource.SubjectLessonTypeLocalDataSource
import com.edujournal.data.local.datasource.SemesterLocalDataSource
import com.edujournal.data.local.datasource.StudentLocalDataSource
import com.edujournal.data.local.datasource.SubjectLocalDataSource
import com.edujournal.data.repository.GradeRepositoryImpl
import com.edujournal.data.repository.GroupRepositoryImpl
import com.edujournal.data.repository.HomeworkRepositoryImpl
import com.edujournal.data.repository.HomeworkSubmissionRepositoryImpl
import com.edujournal.data.repository.LessonRepositoryImpl
import com.edujournal.data.repository.SubjectLessonTypeRepositoryImpl
import com.edujournal.data.repository.SemesterRepositoryImpl
import com.edujournal.data.repository.StudentRepositoryImpl
import com.edujournal.data.repository.SubjectRepositoryImpl
import com.edujournal.domain.repository.GradeRepository
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.domain.repository.HomeworkRepository
import com.edujournal.domain.repository.HomeworkSubmissionRepository
import com.edujournal.domain.repository.LessonRepository
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.repository.SemesterRepository
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.domain.usecase.CreateGroupUseCase
import com.edujournal.domain.usecase.CreateHomeworkUseCase
import com.edujournal.domain.usecase.CreateSubjectLessonTypeUseCase
import com.edujournal.domain.usecase.CreateLessonUseCase
import com.edujournal.domain.usecase.CreateSemesterUseCase
import com.edujournal.domain.usecase.CreateStudentUseCase
import com.edujournal.domain.usecase.CreateSubjectUseCase
import com.edujournal.domain.usecase.DeleteGroupUseCase
import com.edujournal.domain.usecase.DeleteHomeworkUseCase
import com.edujournal.domain.usecase.DeleteSubjectLessonTypeUseCase
import com.edujournal.domain.usecase.DeleteSemesterUseCase
import com.edujournal.domain.usecase.DeleteStudentUseCase
import com.edujournal.domain.usecase.DeleteSubjectUseCase
import com.edujournal.domain.usecase.GetGradesForLessonUseCase
import com.edujournal.domain.usecase.GetGroupsUseCase
import com.edujournal.domain.usecase.GetJournalUseCase
import com.edujournal.domain.usecase.GetLessonsUseCase
import com.edujournal.domain.usecase.ObserveHomeworksUseCase
import com.edujournal.domain.usecase.ObserveHomeworkSubmissionsUseCase
import com.edujournal.domain.usecase.ObserveSubjectLessonTypesUseCase
import com.edujournal.domain.usecase.ObserveSemestersUseCase
import com.edujournal.domain.usecase.ObserveStudentsUseCase
import com.edujournal.domain.usecase.ObserveSubjectsUseCase
import com.edujournal.domain.usecase.SetGradeUseCase
import com.edujournal.domain.usecase.UpdateGroupUseCase
import com.edujournal.domain.usecase.UpdateHomeworkUseCase
import com.edujournal.domain.usecase.UpdateSubjectLessonTypeUseCase
import com.edujournal.domain.usecase.UpdateSemesterUseCase
import com.edujournal.domain.usecase.UpdateStudentUseCase
import com.edujournal.domain.usecase.UpdateSubjectUseCase
import com.edujournal.domain.usecase.UpsertHomeworkSubmissionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase{
        return Room.databaseBuilder(context,
            AppDatabase::class.java,
            "edujournal_db"
        )
        .fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val firstNames = listOf(
                    "Александр", "Алексей", "Андрей", "Антон", "Артем", "Богдан",
                    "Вадим", "Виктор", "Владислав", "Георгий", "Глеб", "Даниил",
                    "Денис", "Дмитрий", "Евгений", "Егор", "Иван", "Илья",
                    "Кирилл", "Константин", "Леонид", "Максим", "Михаил",
                    "Никита", "Олег", "Павел", "Роман", "Руслан", "Сергей",
                    "Степан", "Тимур", "Федор", "Ярослав"
                )
                val lastNames = listOf(
                    "Абрамов", "Алексеев", "Андреев", "Баранов", "Белов", "Беляев",
                    "Богданов", "Борисов", "Быков", "Васильев", "Виноградов",
                    "Власов", "Волков", "Воробьев", "Герасимов", "Голубев",
                    "Горбунов", "Григорьев", "Давыдов", "Егоров", "Елисеев",
                    "Жуков", "Зайцев", "Захаров", "Иванов", "Игнатьев", "Калинин",
                    "Карпов", "Киселев", "Ковалев", "Козлов", "Комаров",
                    "Кондратьев", "Корнилов", "Крылов", "Кузнецов", "Куликов",
                    "Лазарев", "Лебедев", "Макаров", "Медведев", "Миронов",
                    "Михайлов", "Морозов", "Назаров", "Никитин", "Новиков",
                    "Орлов", "Павлов", "Панин", "Петров", "Пономарев", "Попов",
                    "Романов", "Савельев", "Семенов", "Сергеев", "Сидоров",
                    "Смирнов", "Соколов", "Тарасов", "Федоров", "Филиппов",
                    "Чернов", "Шаров", "Шестаков", "Яковлев"
                )
                val middleNames = listOf(
                    "Александрович", "Алексеевич", "Андреевич", "Антонович",
                    "Артемович", "Богданович", "Вадимович", "Викторович",
                    "Владимирович", "Георгиевич", "Глебович", "Даниилович",
                    "Денисович", "Дмитриевич", "Евгеньевич", "Егорович",
                    "Иванович", "Ильич", "Кириллович", "Константинович",
                    "Леонидович", "Максимович", "Михайлович", "Никитич",
                    "Олегович", "Павлович", "Романович", "Русланович",
                    "Сергеевич", "Степанович", "Тимурович", "Федорович",
                    "Ярославович"
                )

                db.beginTransaction()
                try {
                    val usedFullNames = mutableSetOf<String>()

                    db.execSQL("INSERT INTO semesters (season, year) VALUES ('AUTUMN', 2025)")
                    db.execSQL("INSERT INTO semesters (season, year) VALUES ('SPRING', 2026)")
                    db.execSQL("INSERT INTO semesters (season, year) VALUES ('AUTUMN', 2026)")
                    db.execSQL("INSERT INTO semesters (season, year) VALUES ('SPRING', 2027)")

                    db.execSQL("INSERT INTO `groups` (name) VALUES ('10701322')")
                    db.execSQL("INSERT INTO `groups` (name) VALUES ('10701222')")
                    db.execSQL("INSERT INTO `groups` (name) VALUES ('10701122')")

                    db.execSQL("INSERT INTO subjects (name, abbreviation) VALUES ('Основы программной инженерии', 'ОПИ')")
                    db.execSQL("INSERT INTO subjects (name, abbreviation) VALUES ('Компьютерные системы и сети', 'КСиС')")
                    db.execSQL("INSERT INTO subjects (name, abbreviation) VALUES ('Базы данных', 'БД')")

                    val subjectsCursor = db.query("SELECT id FROM subjects ORDER BY id")
                    subjectsCursor.use { cursor ->
                        while (cursor.moveToNext()) {
                            val subjectId = cursor.getLong(0)
                            db.execSQL(
                                "INSERT INTO lesson_types (subjectId, name, hours) VALUES (?, ?, ?)",
                                arrayOf(subjectId, "Лекция", 20.0)
                            )
                            db.execSQL(
                                "INSERT INTO lesson_types (subjectId, name, hours) VALUES (?, ?, ?)",
                                arrayOf(subjectId, "Практика", 30.0)
                            )
                            db.execSQL(
                                "INSERT INTO lesson_types (subjectId, name, hours) VALUES (?, ?, ?)",
                                arrayOf(subjectId, "Лабораторная", 10.0)
                            )
                        }
                    }

                    val groupsCursor = db.query("SELECT id FROM `groups` ORDER BY id")
                    groupsCursor.use { cursor ->
                        while (cursor.moveToNext()) {
                            val groupId = cursor.getLong(0)
                            val groupSeed = (groupId % 1_000_003L).toInt()
                            for (i in 0 until 30) {
                                var step = 0
                                var firstName: String
                                var lastName: String
                                var middleName: String
                                var fullName: String
                                do {
                                    val base = i + step
                                    firstName = firstNames[(groupSeed + base * 7) % firstNames.size]
                                    lastName = lastNames[(groupSeed * 3 + base * 11) % lastNames.size]
                                    middleName = middleNames[(groupSeed * 5 + base * 13) % middleNames.size]
                                    fullName = "$lastName $firstName $middleName"
                                    step++
                                } while (fullName in usedFullNames)

                                usedFullNames += fullName
                                db.execSQL(
                                    "INSERT INTO students (firstName, lastName, middleName, groupId) VALUES (?, ?, ?, ?)",
                                    arrayOf(firstName, lastName, middleName, groupId)
                                )
                            }
                        }
                    }

                    val combinationsCursor = db.query(
                        """
                        SELECT g.id, s.id, lt.id
                        FROM `groups` g
                        CROSS JOIN subjects s
                        JOIN lesson_types lt ON lt.subjectId = s.id
                        """.trimIndent()
                    )
                    combinationsCursor.use { cursor ->
                        while (cursor.moveToNext()) {
                            val groupId = cursor.getLong(0)
                            val subjectId = cursor.getLong(1)
                            val subjectLessonTypeId = cursor.getLong(2)

                            val baseDate = LocalDate.of(2026, 9, 1)
                            val typeOffset = ((subjectLessonTypeId - 1L).coerceAtLeast(0L) * 40L).toInt()
                            for (i in 0 until 18) {
                                val date = baseDate.plusDays((typeOffset + i).toLong()).toString()
                                val topic = "Тема ${i + 1}"
                                db.execSQL(
                                    """
                                    INSERT OR IGNORE INTO lessons (groupId, subjectId, subjectLessonTypeId, semesterId, date, topic)
                                    VALUES (?, ?, ?, ?, ?, ?)
                                    """.trimIndent(),
                                    arrayOf(groupId, subjectId, subjectLessonTypeId, 1L, date, topic)
                                )
                            }
                        }
                    }

                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }
        }) //Добавил addCallback для инициализации начальных данных
        .build()
    }

    @Provides
    fun provideGroupDao(
        database: AppDatabase
    ): GroupDao = database.groupDao()

    @Provides
    fun provideGroupLocalDataSource(
        dao: GroupDao
    ): GroupLocalDataSource = GroupLocalDataSource(dao)

    @Provides
    fun provideGroupRepository(
        localDataSource: GroupLocalDataSource
    ): GroupRepository = GroupRepositoryImpl(localDataSource)

    @Provides
    fun provideCreateGroupUseCase(
        repository: GroupRepository
    ): CreateGroupUseCase = CreateGroupUseCase(repository)

    @Provides
    fun provideGetGroupsUseCase(
        repository: GroupRepository
    ): GetGroupsUseCase = GetGroupsUseCase(repository)

    @Provides
    fun provideStudentDao(
        database: AppDatabase
    ): StudentDao = database.studentDao()

    @Provides
    fun provideStudentLocalDataSource(
        dao: StudentDao
    ): StudentLocalDataSource =
        StudentLocalDataSource(dao)

    @Provides
    fun provideStudentRepository(
        localDataSource: StudentLocalDataSource
    ): StudentRepository =
        StudentRepositoryImpl(localDataSource)

    @Provides
    fun provideCreateStudentUseCase(
        repository: StudentRepository
    ): CreateStudentUseCase =
        CreateStudentUseCase(repository)

    @Provides
    fun provideObserveStudentsUseCase(
        repository: StudentRepository
    ): ObserveStudentsUseCase =
        ObserveStudentsUseCase(repository)

    @Provides
    fun provideSubjectDao(
        database: AppDatabase
    ): SubjectDao = database.subjectDao()

    @Provides
    fun provideSubjectLocalDataSource(
        dao: SubjectDao
    ): SubjectLocalDataSource =
        SubjectLocalDataSource(dao)

    @Provides
    fun provideSubjectRepository(
        localDataSource: SubjectLocalDataSource
    ): SubjectRepository =
        SubjectRepositoryImpl(localDataSource)

    @Provides
    fun provideSemesterDao(
        database: AppDatabase
    ): SemesterDao = database.semesterDao()

    @Provides
    fun provideSemesterLocalDataSource(
        dao: SemesterDao
    ): SemesterLocalDataSource = SemesterLocalDataSource(dao)

    @Provides
    fun provideSemesterRepository(
        localDataSource: SemesterLocalDataSource
    ): SemesterRepository = SemesterRepositoryImpl(localDataSource)

    @Provides
    fun provideCreateSemesterUseCase(
        repository: SemesterRepository
    ): CreateSemesterUseCase = CreateSemesterUseCase(repository)

    @Provides
    fun provideObserveSemestersUseCase(
        repository: SemesterRepository
    ): ObserveSemestersUseCase = ObserveSemestersUseCase(repository)

    @Provides
    fun provideUpdateSemesterUseCase(
        repository: SemesterRepository
    ): UpdateSemesterUseCase = UpdateSemesterUseCase(repository)

    @Provides
    fun provideDeleteSemesterUseCase(
        repository: SemesterRepository
    ): DeleteSemesterUseCase = DeleteSemesterUseCase(repository)

    @Provides
    fun provideCreateSubjectUseCase(
        repository: SubjectRepository,
        lessonTypeRepository: SubjectLessonTypeRepository
    ): CreateSubjectUseCase =
        CreateSubjectUseCase(repository, lessonTypeRepository)

    @Provides
    fun provideObserveSubjectsUseCase(
        repository: SubjectRepository
    ): ObserveSubjectsUseCase =
        ObserveSubjectsUseCase(repository)

    @Provides
    fun provideLessonTypeDao(
        database: AppDatabase
    ): SubjectLessonTypeDao = database.lessonTypeDao()

    @Provides
    fun provideLessonTypeLocalDataSource(
        dao: SubjectLessonTypeDao
    ): SubjectLessonTypeLocalDataSource =
        SubjectLessonTypeLocalDataSource(dao)

    @Provides
    fun provideLessonTypeRepository(
        localDataSource: SubjectLessonTypeLocalDataSource
    ): SubjectLessonTypeRepository =
        SubjectLessonTypeRepositoryImpl(localDataSource)

    @Provides
    fun provideCreateLessonTypeUseCase(
        repository: SubjectLessonTypeRepository
    ): CreateSubjectLessonTypeUseCase =
        CreateSubjectLessonTypeUseCase(repository)

    @Provides
    fun provideObserveLessonTypesUseCase(
        repository: SubjectLessonTypeRepository
    ): ObserveSubjectLessonTypesUseCase =
        ObserveSubjectLessonTypesUseCase(repository)

    @Provides
    fun provideLessonDao(
        database: AppDatabase
    ): LessonDao = database.lessonDao()

    @Provides
    fun provideLessonLocalDataSource(
        lessonDao: LessonDao
    ): LessonLocalDataSource {
        return LessonLocalDataSource(lessonDao)
    }

    @Provides
    fun provideLessonRepository(
        localDataSource: LessonLocalDataSource
    ): LessonRepository {
        return LessonRepositoryImpl(localDataSource)
    }

    @Provides
    fun provideCreateLessonUseCase(
        repository: LessonRepository
    ): CreateLessonUseCase =
        CreateLessonUseCase(repository)

    @Provides
    fun provideGetLessonsUseCase(
        repository: LessonRepository
    ): GetLessonsUseCase =
        GetLessonsUseCase(repository)

    @Provides
    fun provideGradeDao(
        database: AppDatabase
    ): GradeDao = database.gradeDao()

    @Provides
    fun provideGradeLocalDataSource(
        gradeDao: GradeDao
    ): GradeLocalDataSource {
        return GradeLocalDataSource(gradeDao)
    }

    @Provides
    fun provideGradeRepository(
        localDataSource: GradeLocalDataSource
    ): GradeRepository {
        return GradeRepositoryImpl(localDataSource)
    }

    @Provides
    fun provideSetGradeUseCase(
        repository: GradeRepository
    ): SetGradeUseCase =
        SetGradeUseCase(repository)

    @Provides
    fun provideGetGradesForLessonUseCase(
        repository: GradeRepository
    ): GetGradesForLessonUseCase =
        GetGradesForLessonUseCase(repository)

    @Provides
    fun provideGetJournalUseCase(
        repository: GradeRepository
    ): GetJournalUseCase {
        return GetJournalUseCase(repository)
    }

    @Provides
    fun provideHomeworkDao(
        database: AppDatabase
    ): HomeworkDao = database.homeworkDao()

    @Provides
    fun provideHomeworkLocalDataSource(
        dao: HomeworkDao
    ): HomeworkLocalDataSource = HomeworkLocalDataSource(dao)

    @Provides
    fun provideHomeworkRepository(
        localDataSource: HomeworkLocalDataSource
    ): HomeworkRepository = HomeworkRepositoryImpl(localDataSource)

    @Provides
    fun provideObserveHomeworksUseCase(
        repository: HomeworkRepository
    ): ObserveHomeworksUseCase = ObserveHomeworksUseCase(repository)

    @Provides
    fun provideCreateHomeworkUseCase(
        repository: HomeworkRepository
    ): CreateHomeworkUseCase = CreateHomeworkUseCase(repository)

    @Provides
    fun provideUpdateHomeworkUseCase(
        repository: HomeworkRepository
    ): UpdateHomeworkUseCase = UpdateHomeworkUseCase(repository)

    @Provides
    fun provideDeleteHomeworkUseCase(
        repository: HomeworkRepository
    ): DeleteHomeworkUseCase = DeleteHomeworkUseCase(repository)

    @Provides
    fun provideHomeworkSubmissionDao(
        database: AppDatabase
    ): HomeworkSubmissionDao = database.homeworkSubmissionDao()

    @Provides
    fun provideHomeworkSubmissionLocalDataSource(
        dao: HomeworkSubmissionDao
    ): HomeworkSubmissionLocalDataSource = HomeworkSubmissionLocalDataSource(dao)

    @Provides
    fun provideHomeworkSubmissionRepository(
        localDataSource: HomeworkSubmissionLocalDataSource
    ): HomeworkSubmissionRepository = HomeworkSubmissionRepositoryImpl(localDataSource)

    @Provides
    fun provideObserveHomeworkSubmissionsUseCase(
        repository: HomeworkSubmissionRepository
    ): ObserveHomeworkSubmissionsUseCase = ObserveHomeworkSubmissionsUseCase(repository)

    @Provides
    fun provideUpsertHomeworkSubmissionUseCase(
        repository: HomeworkSubmissionRepository
    ): UpsertHomeworkSubmissionUseCase = UpsertHomeworkSubmissionUseCase(repository)

    @Provides
    fun provideUpdateSubjectUseCase(
        repository: SubjectRepository
    ): UpdateSubjectUseCase =
        UpdateSubjectUseCase(repository)

    @Provides
    fun provideDeleteSubjectUseCase(
        repository: SubjectRepository
    ): DeleteSubjectUseCase = DeleteSubjectUseCase(repository)

    @Provides
    fun provideUpdateLessonTypeUseCase(
        repository: SubjectLessonTypeRepository
    ): UpdateSubjectLessonTypeUseCase =
        UpdateSubjectLessonTypeUseCase(repository)

    @Provides
    fun provideDeleteLessonTypeUseCase(
        repository: SubjectLessonTypeRepository
    ): DeleteSubjectLessonTypeUseCase = DeleteSubjectLessonTypeUseCase(repository)

    @Provides
    fun provideUpdateGroupUseCase(
        repository: GroupRepository
    ): UpdateGroupUseCase =
        UpdateGroupUseCase(repository)

    @Provides
    fun provideDeleteGroupUseCase(
        repository: GroupRepository
    ): DeleteGroupUseCase = DeleteGroupUseCase(repository)

    @Provides
    fun provideUpdateStudentUseCase(
        repository: StudentRepository
    ): UpdateStudentUseCase =
        UpdateStudentUseCase(repository)

    @Provides
    fun provideDeleteStudentUseCase(
        repository: StudentRepository
    ): DeleteStudentUseCase = DeleteStudentUseCase(repository)
}



