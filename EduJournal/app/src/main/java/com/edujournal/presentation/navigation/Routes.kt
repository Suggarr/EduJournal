package com.edujournal.presentation.navigation

object Routes {
    // Вкладки
    const val MAIN_TAB = "main_tab"
    const val GROUPS_TAB = "groups_tab"
    const val SETTINGS_TAB = "settings_tab"

    // Экраны
    const val WELCOME = "welcome"
    const val SUBJECTS = "subjects"
    const val LESSON_TYPES = "lesson_types/{semesterId}/{subjectId}"
    const val GROUPS = "groups/{semesterId}/{subjectId}/{typeId}"
    const val STUDENTS = "students/{groupId}"
    const val JOURNAL = "journal/{semesterId}/{groupId}/{subjectId}/{typeId}"
    const val LESSON_TOPICS = "lesson_topics/{semesterId}/{groupId}/{subjectId}/{typeId}"
    const val SETTINGS = "settings"
    const val SEMESTERS = "semesters"

    fun lessonTypes(semesterId: Long, subjectId: Long) = "lesson_types/$semesterId/$subjectId"
    fun groups(semesterId: Long, subjectId: Long, typeId: Long) = "groups/$semesterId/$subjectId/$typeId"
    fun students(groupId: Long) = "students/$groupId"
    fun journal(semesterId: Long, groupId: Long, subjectId: Long, typeId: Long) =
        "journal/$semesterId/$groupId/$subjectId/$typeId"
    fun lessonTopics(semesterId: Long, groupId: Long, subjectId: Long, typeId: Long) =
        "lesson_topics/$semesterId/$groupId/$subjectId/$typeId"
}
