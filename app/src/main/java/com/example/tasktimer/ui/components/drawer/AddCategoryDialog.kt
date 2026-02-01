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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.ui.components.CategoryDialogViewModel
import com.example.tasktimer.ui.theme.*

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Color) -> Unit,
    viewModel: CategoryDialogViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.reset()
    }
    
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
                    value = state.categoryName,
                    onValueChange = { viewModel.updateCategoryName(it) },
                    error = state.nameError
                )
                
                ColorPicker(
                    selectedColor = state.selectedColor,
                    onColorSelected = { viewModel.updateSelectedColor(it) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (state.categoryName.isNotBlank()) {
                        onConfirm(state.categoryName.trim(), state.selectedColor)
                        onDismiss()
                    }
                },
                enabled = state.categoryName.isNotBlank()
            ) {
                Text(
                    text = "Criar",
                    color = if (state.categoryName.isNotBlank()) PrimaryBlue else TextGray
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
    onValueChange: (String) -> Unit,
    error: Boolean = false
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
            errorBorderColor = Color(0xFFD32F2F),
            cursorColor = PrimaryBlue
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = error,
        supportingText = if (error) {
            { Text("Nome é obrigatório", color = Color(0xFFD32F2F)) }
        } else null
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
