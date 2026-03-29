package com.edujournal.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.edujournal.data.local.dao.GradeDao
import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.dao.LessonDao
import com.edujournal.data.local.dao.LessonTypeDao
import com.edujournal.data.local.dao.StudentDao
import com.edujournal.data.local.dao.SubjectDao
import com.edujournal.data.local.database.AppDatabase
import com.edujournal.data.local.datasource.GradeLocalDataSource
import com.edujournal.data.local.datasource.GroupLocalDataSource
import com.edujournal.data.local.datasource.LessonLocalDataSource
import com.edujournal.data.local.datasource.LessonTypeLocalDataSource
import com.edujournal.data.local.datasource.StudentLocalDataSource
import com.edujournal.data.local.datasource.SubjectLocalDataSource
import com.edujournal.data.repository.GradeRepositoryImpl
import com.edujournal.data.repository.GroupRepositoryImpl
import com.edujournal.data.repository.LessonRepositoryImpl
import com.edujournal.data.repository.LessonTypeRepositoryImpl
import com.edujournal.data.repository.StudentRepositoryImpl
import com.edujournal.data.repository.SubjectRepositoryImpl
import com.edujournal.domain.repository.GradeRepository
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.domain.repository.LessonRepository
import com.edujournal.domain.repository.LessonTypeRepository
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.domain.repository.SubjectRepository
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private val migration1to2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("PRAGMA foreign_keys=OFF")

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS lessons_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    groupId INTEGER NOT NULL,
                    subjectId INTEGER NOT NULL,
                    lessonTypeId INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    topic TEXT NOT NULL,
                    FOREIGN KEY(groupId) REFERENCES `groups`(id) ON DELETE CASCADE,
                    FOREIGN KEY(subjectId) REFERENCES subjects(id) ON DELETE CASCADE,
                    FOREIGN KEY(lessonTypeId) REFERENCES lesson_types(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO lessons_new (id, groupId, subjectId, lessonTypeId, date, topic)
                SELECT id, groupId, subjectId, lessonTypeId, date, topic FROM lessons
                """.trimIndent()
            )
            database.execSQL("DROP TABLE lessons")
            database.execSQL("ALTER TABLE lessons_new RENAME TO lessons")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_lessons_groupId ON lessons(groupId)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_lessons_subjectId ON lessons(subjectId)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_lessons_lessonTypeId ON lessons(lessonTypeId)")
            database.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_lessons_groupId_subjectId_date ON lessons(groupId, subjectId, date)"
            )

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS grades_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    studentId INTEGER NOT NULL,
                    lessonId INTEGER NOT NULL,
                    value INTEGER,
                    type TEXT NOT NULL,
                    comment TEXT,
                    FOREIGN KEY(studentId) REFERENCES students(id) ON DELETE CASCADE,
                    FOREIGN KEY(lessonId) REFERENCES lessons(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO grades_new (id, studentId, lessonId, value, type, comment)
                SELECT id, studentId, lessonId, value, type, comment FROM grades
                """.trimIndent()
            )
            database.execSQL("DROP TABLE grades")
            database.execSQL("ALTER TABLE grades_new RENAME TO grades")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_grades_studentId ON grades(studentId)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_grades_lessonId ON grades(lessonId)")
            database.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_grades_studentId_lessonId ON grades(studentId, lessonId)"
            )

            database.execSQL("PRAGMA foreign_keys=ON")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase{
        return Room.databaseBuilder(context,
            AppDatabase::class.java,
            "edujournal_db"
        )
        .addMigrations(migration1to2)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL("INSERT INTO lesson_types (name) VALUES ('Лекция')")
                db.execSQL("INSERT INTO lesson_types (name) VALUES ('Практика')")
                db.execSQL("INSERT INTO lesson_types (name) VALUES ('Лабораторная')")
                db.execSQL("INSERT INTO lesson_types (name) VALUES ('Контрольная')")
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
    fun provideCreateSubjectUseCase(
        repository: SubjectRepository
    ): CreateSubjectUseCase =
        CreateSubjectUseCase(repository)

    @Provides
    fun provideObserveSubjectsUseCase(
        repository: SubjectRepository
    ): ObserveSubjectsUseCase =
        ObserveSubjectsUseCase(repository)

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
        repository: SubjectRepository
    ): UpdateSubjectUseCase =
        UpdateSubjectUseCase(repository)

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
