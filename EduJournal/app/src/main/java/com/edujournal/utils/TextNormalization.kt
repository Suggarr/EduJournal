package com.edujournal.utils

private val MULTI_SPACE_REGEX = Regex("\\s+")

fun String.normalizeSpaces(): String = trim().replace(MULTI_SPACE_REGEX, " ")

fun String?.normalizeSpacesOrNull(): String? =
    this?.normalizeSpaces()?.takeIf { it.isNotEmpty() }
