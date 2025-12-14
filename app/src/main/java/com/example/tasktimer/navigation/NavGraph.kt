package com.example.tasktimer.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.tasktimer.ui.calendar.CalendarContent
import com.example.tasktimer.ui.home.HomeContent
import com.example.tasktimer.ui.search.SearchContent
import com.example.tasktimer.ui.theme.DarkBackground
import com.example.tasktimer.ui.theme.SelectedNav
import com.example.tasktimer.ui.theme.SurfaceDark
import com.example.tasktimer.ui.theme.TextGray
import com.example.tasktimer.ui.theme.TextWhite
import kotlinx.coroutines.launch

@Composable
fun NavGraph() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )
    val scope = rememberCoroutineScope()
    
    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = TextGray
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    label = { Text("Tasks") },
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = TextWhite,
                        indicatorColor = SelectedNav,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Calendário") },
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = TextWhite,
                        indicatorColor = SelectedNav,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Pesquisar") },
                    selected = pagerState.currentPage == 2,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = TextWhite,
                        indicatorColor = SelectedNav,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> HomeContent()
                1 -> CalendarContent()
                2 -> SearchContent()
            }
        }
    }
}
