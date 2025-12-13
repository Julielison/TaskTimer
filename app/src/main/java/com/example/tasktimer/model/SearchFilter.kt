package com.example.tasktimer.model

import java.time.LocalDate

data class SearchFilter(
    val query: String = "",
    val categoryIds: List<Int> = emptyList(),
    val dateRange: DateRange? = null,
    val showCompleted: Boolean = true,
    val showOverdue: Boolean = true,
    val sortBy: SortOption = SortOption.DATE_ASC
)

data class DateRange(
    val start: LocalDate,
    val end: LocalDate
)

enum class SortOption {
    DATE_ASC,
    DATE_DESC,
    TITLE_ASC,
    TITLE_DESC,
    CATEGORY
}
