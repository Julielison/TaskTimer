package com.example.tasktimer.ui.components.drawer

import com.example.tasktimer.model.Category
import com.example.tasktimer.ui.home.TaskFilter

sealed class DrawerMenuItem {
    abstract val title: String
    abstract val icon: String
    abstract val isSelected: Boolean
    
    data class FilterItem(
        override val title: String,
        override val icon: String,
        override val isSelected: Boolean,
        val filter: TaskFilter
    ) : DrawerMenuItem()
    
    data class CategoryItem(
        override val title: String,
        override val icon: String,
        override val isSelected: Boolean,
        val category: Category
    ) : DrawerMenuItem()
}

fun buildDrawerMenuItems(
    categories: List<Category>,
    selectedFilter: TaskFilter
): List<DrawerMenuItem> {
    return buildList {
        add(
            DrawerMenuItem.FilterItem(
                title = "Todas",
                icon = "📋",
                isSelected = selectedFilter is TaskFilter.All,
                filter = TaskFilter.All
            )
        )
        add(
            DrawerMenuItem.FilterItem(
                title = "Hoje",
                icon = "📅",
                isSelected = selectedFilter is TaskFilter.Today,
                filter = TaskFilter.Today
            )
        )
        addAll(
            categories.map { category ->
                DrawerMenuItem.CategoryItem(
                    title = category.name,
                    icon = CategoryIconMapper.getIcon(category.name),
                    isSelected = selectedFilter is TaskFilter.Category &&
                            selectedFilter.categoryId == category.id,
                    category = category
                )
            }
        )
    }
}
