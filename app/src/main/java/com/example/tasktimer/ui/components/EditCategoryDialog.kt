package com.example.tasktimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tasktimer.model.Category
import com.example.tasktimer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (String, Color) -> Unit
) {
    var categoryName by remember { mutableStateOf(category.name) }
    var selectedColor by remember { mutableStateOf(category.color) }
    var nameError by remember { mutableStateOf(false) }

    val colors = listOf(
        Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFF9800),
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF00BCD4),
        Color(0xFFFFEB3B), Color(0xFF795548), Color(0xFF607D8B),
        Color(0xFFF44336), Color(0xFF3F51B5), Color(0xFF8BC34A)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Editar Categoria",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { 
                        categoryName = it
                        nameError = false
                    },
                    label = { Text("Nome da categoria", color = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = TextGray,
                        errorBorderColor = Color(0xFFD32F2F),
                        cursorColor = PrimaryBlue
                    ),
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Nome é obrigatório", color = Color(0xFFD32F2F)) }
                    } else null
                )

                Column {
                    Text(
                        text = "Cor",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(colors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedColor == color) 3.dp else 0.dp,
                                        color = TextWhite,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = color }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextWhite
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (categoryName.trim().isBlank()) {
                                nameError = true
                            } else {
                                onConfirm(categoryName.trim(), selectedColor)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}
