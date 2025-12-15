package com.example.tasktimer.ui.components.drawer

import androidx.compose.ui.graphics.Color

object CategoryColors {
    val defaultColor = Color(0xFF4285F4)
    
    val predefinedColors = listOf(
        Color(0xFF4285F4), // Azul
        Color(0xFF34A853), // Verde
        Color(0xFFEA4335), // Vermelho
        Color(0xFFFBBC04), // Amarelo
        Color(0xFF9C27B0), // Roxo
        Color(0xFFFF6D00), // Laranja
        Color(0xFF00897B), // Turquesa
        Color(0xFFD81B60), // Rosa
        Color(0xFF546E7A), // Cinza azulado
        Color(0xFF6D4C41)  // Marrom
    )
}

object CategoryIconMapper {
    private val iconMap = mapOf(
        "trabalho" to "💼",
        "desenvolvimento" to "💻",
        "pessoal" to "🏠",
        "estudos" to "📚",
        "saúde" to "💊"
    )
    
    private const val DEFAULT_ICON = "📌"
    
    fun getIcon(categoryName: String): String {
        return iconMap[categoryName.lowercase()] ?: DEFAULT_ICON
    }
}
