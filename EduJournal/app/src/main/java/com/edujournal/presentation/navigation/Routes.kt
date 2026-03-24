package com.edujournal.presentation.navigation

object Routes {
    // Вкладки
    const val MAIN_TAB = "main_tab"
    const val GROUPS_TAB = "groups_tab"
    const val SETTINGS_TAB = "settings_tab"

    // Экраны
    const val WELCOME = "welcome"
    const val SUBJECTS = "subjects"
    const val LESSON_TYPES = "lesson_types/{subjectId}"
    const val GROUPS = "groups/{subjectId}/{typeId}"
    const val STUDENTS = "students/{groupId}"
    const val JOURNAL = "journal/{groupId}"
    const val SETTINGS = "settings"

    fun lessonTypes(subjectId: Long) = "lesson_types/$subjectId"
    fun groups(subjectId: Long, typeId: Long) = "groups/$subjectId/$typeId"
    fun students(groupId: Long) = "students/$groupId"
    fun journal(groupId: Long) = "journal/$groupId"
}