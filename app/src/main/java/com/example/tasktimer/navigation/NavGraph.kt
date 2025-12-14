package com.example.tasktimer.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.tasktimer.ui.calendar.CalendarScreen
import com.example.tasktimer.ui.home.HomeScreen
import com.example.tasktimer.ui.search.SearchScreen
import kotlinx.coroutines.launch

@Composable
fun NavGraph() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )
    val scope = rememberCoroutineScope()
    
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = true
    ) { page ->
        when (page) {
            0 -> HomeScreen(
                onNavigateToCalendar = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                onNavigateToSearch = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )
            1 -> CalendarScreen(
                onNavigateToHome = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                onNavigateToSearch = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )
            2 -> SearchScreen(
                onNavigateToHome = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                onNavigateToCalendar = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
        }
    }
}
