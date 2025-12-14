package com.example.tasktimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktimer.model.Category
import com.example.tasktimer.ui.home.TaskFilter
import com.example.tasktimer.ui.theme.PrimaryBlue
import com.example.tasktimer.ui.theme.SurfaceDark
import com.example.tasktimer.ui.theme.TextGray
import com.example.tasktimer.ui.theme.TextWhite


@Composable
fun DrawerContent(
    categories: List<Category>,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header do Drawer
            Text(
                text = "TaskTimer",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = TextGray.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Todas as Tasks
            DrawerItem(
                title = "Todas",
                icon = "📋",
                isSelected = selectedFilter is TaskFilter.All,
                onClick = { onFilterSelected(TaskFilter.All) }
            )

            // Tasks de Hoje
            DrawerItem(
                title = "Hoje",
                icon = "📅",
                isSelected = selectedFilter is TaskFilter.Today,
                onClick = { onFilterSelected(TaskFilter.Today) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categorias
            Text(
                text = "Categorias",
                color = TextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            categories.forEach { category ->
                DrawerItem(
                    title = category.name,
                    icon = getCategoryIcon(category.name),
                    color = category.color,
                    isSelected = selectedFilter is TaskFilter.Category && 
                                 selectedFilter.categoryId == category.id,
                    onClick = { onFilterSelected(TaskFilter.Category(category.id)) }
                )
            }
        }
    }
}

@Composable
fun DrawerItem(
    title: String,
    icon: String,
    color: Color? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            
            Text(
                text = title,
                color = if (isSelected) PrimaryBlue else TextWhite,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            
            if (color != null) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, CircleShape)
                )
            }
        }
    }
}

fun getCategoryIcon(categoryName: String): String {
    return when (categoryName.lowercase()) {
        "trabalho" -> "💼"
        "desenvolvimento" -> "💻"
        "pessoal" -> "🏠"
        "estudos" -> "📚"
        "saúde" -> "💊"
        else -> "📌"
    }
}
