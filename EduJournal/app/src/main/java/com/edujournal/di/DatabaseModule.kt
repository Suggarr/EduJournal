package com.edujournal.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.edujournal.data.local.dao.GradeDao
import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.dao.LessonDao
import com.edujournal.data.local.dao.LessonTypeDao
import com.edujournal.data.local.dao.StudentDao
import com.edujournal.data.local.dao.SubjectDao
import com.edujournal.data.local.dao.SubjectLessonTypeHoursDao
import com.edujournal.data.local.database.AppDatabase
import com.edujournal.data.local.datasource.GradeLocalDataSource
import com.edujournal.data.local.datasource.GroupLocalDataSource
import com.edujournal.data.local.datasource.LessonLocalDataSource
import com.edujournal.data.local.datasource.LessonTypeLocalDataSource
import com.edujournal.data.local.datasource.StudentLocalDataSource
import com.edujournal.data.local.datasource.SubjectLocalDataSource
import com.edujournal.data.local.datasource.SubjectLessonTypeHoursLocalDataSource
import com.edujournal.data.repository.GradeRepositoryImpl
import com.edujournal.data.repository.GroupRepositoryImpl
import com.edujournal.data.repository.LessonRepositoryImpl
import com.edujournal.data.repository.LessonTypeRepositoryImpl
import com.edujournal.data.repository.StudentRepositoryImpl
import com.edujournal.data.repository.SubjectRepositoryImpl
import com.edujournal.data.repository.SubjectLessonTypeHoursRepositoryImpl
import com.edujournal.domain.repository.GradeRepository
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.domain.repository.LessonRepository
import com.edujournal.domain.repository.LessonTypeRepository
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.domain.repository.SubjectLessonTypeHoursRepository
import com.edujournal.domain.usecase.CreateGroupUseCase
import com.edujournal.domain.usecase.CreateLessonTypeUseCase
import com.edujournal.domain.usecase.CreateLessonUseCase
import com.edujournal.domain.usecase.CreateStudentUseCase
import com.edujournal.domain.usecase.CreateSubjectUseCase
import com.edujournal.domain.usecase.DeleteGroupUseCase
import com.edujournal.domain.usecase.DeleteLessonTypeUseCase
import com.edujournal.domain.usecase.DeleteStudentUseCase
import com.edujournal.domain.usecase.DeleteSubjectUseCase
import com.edujournal.domain.usecase.GetGradesForLessonUseCase
import com.edujournal.domain.usecase.GetGroupsUseCase
import com.edujournal.domain.usecase.GetJournalUseCase
import com.edujournal.domain.usecase.GetLessonsUseCase
import com.edujournal.domain.usecase.ObserveLessonTypesUseCase
import com.edujournal.domain.usecase.ObserveSubjectLessonTypeHoursUseCase
import com.edujournal.domain.usecase.ObserveStudentsUseCase
import com.edujournal.domain.usecase.ObserveSubjectsUseCase
import com.edujournal.domain.usecase.SetGradeUseCase
import com.edujournal.domain.usecase.UpdateGroupUseCase
import com.edujournal.domain.usecase.UpdateLessonTypeUseCase
import com.edujournal.domain.usecase.UpdateStudentUseCase
import com.edujournal.domain.usecase.UpdateSubjectUseCase
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

                    db.execSQL("INSERT INTO lesson_types (name) VALUES ('Лекция')")
                    db.execSQL("INSERT INTO lesson_types (name) VALUES ('Практика')")
                    db.execSQL("INSERT INTO lesson_types (name) VALUES ('Лабораторная')")
                    db.execSQL("INSERT INTO lesson_types (name) VALUES ('Контрольная')")

                    db.execSQL("INSERT INTO `groups` (name) VALUES ('10701322')")
                    db.execSQL("INSERT INTO `groups` (name) VALUES ('10701222')")
                    db.execSQL("INSERT INTO `groups` (name) VALUES ('10701122')")

                    db.execSQL("INSERT INTO subjects (name, abbreviation) VALUES ('Основы программной инженерии', 'ОПИ')")
                    db.execSQL("INSERT INTO subjects (name, abbreviation) VALUES ('Компьютерные системы и сети', 'КСиС')")
                    db.execSQL("INSERT INTO subjects (name, abbreviation) VALUES ('Базы данных', 'БД')")

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
                        CROSS JOIN lesson_types lt
                        """.trimIndent()
                    )
                    combinationsCursor.use { cursor ->
                        while (cursor.moveToNext()) {
                            val groupId = cursor.getLong(0)
                            val subjectId = cursor.getLong(1)
                            val lessonTypeId = cursor.getLong(2)

                            val baseDate = LocalDate.of(2026, 9, 1)
                            val typeOffset = ((lessonTypeId - 1L).coerceAtLeast(0L) * 40L).toInt()
                            for (i in 0 until 18) {
                                val date = baseDate.plusDays((typeOffset + i).toLong()).toString()
                                val topic = "Тема ${i + 1}"
                                db.execSQL(
                                    """
                                    INSERT OR IGNORE INTO lessons (groupId, subjectId, lessonTypeId, date, topic)
                                    VALUES (?, ?, ?, ?, ?)
                                    """.trimIndent(),
                                    arrayOf(groupId, subjectId, lessonTypeId, date, topic)
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
    fun provideSubjectLessonTypeHoursDao(
        database: AppDatabase
    ): SubjectLessonTypeHoursDao = database.subjectLessonTypeHoursDao()

    @Provides
    fun provideSubjectLessonTypeHoursLocalDataSource(
        dao: SubjectLessonTypeHoursDao
    ): SubjectLessonTypeHoursLocalDataSource =
        SubjectLessonTypeHoursLocalDataSource(dao)

    @Provides
    fun provideSubjectLessonTypeHoursRepository(
        localDataSource: SubjectLessonTypeHoursLocalDataSource
    ): SubjectLessonTypeHoursRepository =
        SubjectLessonTypeHoursRepositoryImpl(localDataSource)

    @Provides
    fun provideCreateSubjectUseCase(
        repository: SubjectRepository,
        subjectLessonTypeHoursRepository: SubjectLessonTypeHoursRepository
    ): CreateSubjectUseCase =
        CreateSubjectUseCase(repository, subjectLessonTypeHoursRepository)

    @Provides
    fun provideObserveSubjectsUseCase(
        repository: SubjectRepository
    ): ObserveSubjectsUseCase =
        ObserveSubjectsUseCase(repository)

    @Provides
    fun provideObserveSubjectLessonTypeHoursUseCase(
        repository: SubjectLessonTypeHoursRepository
    ): ObserveSubjectLessonTypeHoursUseCase =
        ObserveSubjectLessonTypeHoursUseCase(repository)

    @Provides
    fun provideLessonTypeDao(
        database: AppDatabase
    ): LessonTypeDao = database.lessonTypeDao()

    @Provides
    fun provideLessonTypeLocalDataSource(
        dao: LessonTypeDao
    ): LessonTypeLocalDataSource =
        LessonTypeLocalDataSource(dao)

    @Provides
    fun provideLessonTypeRepository(
        localDataSource: LessonTypeLocalDataSource
    ): LessonTypeRepository =
        LessonTypeRepositoryImpl(localDataSource)

    @Provides
    fun provideCreateLessonTypeUseCase(
        repository: LessonTypeRepository
    ): CreateLessonTypeUseCase =
        CreateLessonTypeUseCase(repository)

    @Provides
    fun provideObserveLessonTypesUseCase(
        repository: LessonTypeRepository
    ): ObserveLessonTypesUseCase =
        ObserveLessonTypesUseCase(repository)

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
    fun provideUpdateSubjectUseCase(
        repository: SubjectRepository,
        subjectLessonTypeHoursRepository: SubjectLessonTypeHoursRepository
    ): UpdateSubjectUseCase =
        UpdateSubjectUseCase(repository, subjectLessonTypeHoursRepository)

    @Provides
    fun provideDeleteSubjectUseCase(
        repository: SubjectRepository
    ): DeleteSubjectUseCase = DeleteSubjectUseCase(repository)

    @Provides
    fun provideUpdateLessonTypeUseCase(
        repository: LessonTypeRepository
    ): UpdateLessonTypeUseCase =
        UpdateLessonTypeUseCase(repository)

    @Provides
    fun provideDeleteLessonTypeUseCase(
        repository: LessonTypeRepository
    ): DeleteLessonTypeUseCase = DeleteLessonTypeUseCase(repository)

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

