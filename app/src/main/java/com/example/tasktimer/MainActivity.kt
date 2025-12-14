package com.example.tasktimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.tasktimer.ui.calendar.CalendarScreen
import com.example.tasktimer.ui.home.HomeScreen
import com.example.tasktimer.ui.search.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("home") }

            when (currentScreen) {
                "home" -> HomeScreen(
                    onNavigateToCalendar = { currentScreen = "calendar" },
                    onNavigateToSearch = { currentScreen = "search" }
                )
                "calendar" -> CalendarScreen(
                    onNavigateToHome = { currentScreen = "home" },
                    onNavigateToSearch = { currentScreen = "search" }
                )
                "search" -> SearchScreen(
                    onNavigateToHome = { currentScreen = "home" },
                    onNavigateToCalendar = { currentScreen = "calendar" }
                )
                else -> HomeScreen(
                    onNavigateToCalendar = { currentScreen = "calendar" },
                    onNavigateToSearch = { currentScreen = "search" }
                )
            }
        }
    }
}