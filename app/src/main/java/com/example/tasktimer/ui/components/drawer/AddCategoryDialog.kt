package com.example.tasktimer.ui.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktimer.ui.theme.*

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Color) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(CategoryColors.defaultColor) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nova Categoria",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CategoryNameField(
                    value = categoryName,
                    onValueChange = { categoryName = it }
                )
                
                ColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onConfirm(categoryName.trim(), selectedColor)
                    }
                },
                enabled = categoryName.isNotBlank()
            ) {
                Text(
                    text = "Criar",
                    color = if (categoryName.isNotBlank()) PrimaryBlue else TextGray
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        },
        containerColor = SurfaceDark
    )
}

@Composable
private fun CategoryNameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Nome da categoria", color = TextGray) },
        placeholder = { Text("Ex: Trabalho", color = TextGray) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = TextGray,
            cursorColor = PrimaryBlue
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Escolha uma cor",
            color = TextGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryColors.predefinedColors.chunked(5).forEach { rowColors ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowColors.forEach { color ->
                        ColorOption(
                            color = color,
                            isSelected = selectedColor == color,
                            onSelected = { onColorSelected(color) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color = color, shape = CircleShape)
            .clickable(onClick = onSelected),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selecionado",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
