package com.edujournal.presentation.navigation

object Routes {
    const val MAIN_TAB = "main_tab"
    const val GROUPS_TAB = "groups_tab"
    const val SETTINGS_TAB = "settings_tab"

    const val WELCOME = "welcome"
    const val SUBJECTS = "subjects"
    const val LESSON_TYPES = "lesson_types/{semesterId}/{subjectId}"
    const val GROUPS = "groups/{semesterId}/{subjectId}/{subjectLessonTypeId}"
    const val STUDENTS = "students/{groupId}"
    const val JOURNAL = "journal/{semesterId}/{groupId}/{subjectId}/{subjectLessonTypeId}"
    const val ANALYTICS = "analytics/{semesterId}/{groupId}/{subjectId}"
    const val HOMEWORKS = "homeworks/{lessonId}"
    const val LESSON_TOPICS = "lesson_topics/{semesterId}/{groupId}/{subjectId}/{subjectLessonTypeId}"
    const val SETTINGS = "settings"
    const val SEMESTERS = "semesters"

    fun lessonTypes(semesterId: Long, subjectId: Long) = "lesson_types/$semesterId/$subjectId"
    fun groups(semesterId: Long, subjectId: Long, subjectLessonTypeId: Long) =
        "groups/$semesterId/$subjectId/$subjectLessonTypeId"
    fun students(groupId: Long) = "students/$groupId"
    fun journal(semesterId: Long, groupId: Long, subjectId: Long, subjectLessonTypeId: Long) =
        "journal/$semesterId/$groupId/$subjectId/$subjectLessonTypeId"
    fun analytics(semesterId: Long, groupId: Long, subjectId: Long) =
        "analytics/$semesterId/$groupId/$subjectId"
    fun homeworks(lessonId: Long) = "homeworks/$lessonId"
    fun lessonTopics(semesterId: Long, groupId: Long, subjectId: Long, subjectLessonTypeId: Long) =
        "lesson_topics/$semesterId/$groupId/$subjectId/$subjectLessonTypeId"
}

