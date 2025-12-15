package com.example.tasktimer.ui.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktimer.ui.theme.*

@Composable
fun DrawerHeader() {
    Column {
        Text(
            text = "TaskTimer",
            color = TextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = TextGray.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun DrawerMenuItems(
    items: List<DrawerMenuItem>,
    onItemClick: (DrawerMenuItem) -> Unit,
    onCategoryLongPress: (String) -> Unit = {}
) {
    items.forEach { item ->
        DrawerItem(
            title = item.title,
            icon = item.icon,
            color = (item as? DrawerMenuItem.CategoryItem)?.category?.color,
            isSelected = item.isSelected,
            onClick = { onItemClick(item) },
            onLongPress = if (item is DrawerMenuItem.CategoryItem) {
                { onCategoryLongPress(item.category.id) }
            } else null
        )
        
        if (item is DrawerMenuItem.FilterItem && item.title == "Hoje") {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DrawerFooter(onAddCategoryClick: () -> Unit) {
    Column {
        HorizontalDivider(color = TextGray.copy(alpha = 0.2f))
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAddCategoryClick)
                .padding(16.dp),
            color = Color.Transparent
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nova categoria",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                
                Text(
                    text = "Nova categoria",
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(
    title: String,
    icon: String,
    color: Color? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .let { modifier ->
                    if (onLongPress != null) {
                        modifier.combinedClickable(
                            onClick = onClick,
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongPress()
                            }
                        )
                    } else {
                        modifier.clickable(onClick = onClick)
                    }
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = icon, fontSize = 24.sp)
            
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
