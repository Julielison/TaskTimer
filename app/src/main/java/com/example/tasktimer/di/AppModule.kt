package com.example.tasktimer.di

import com.example.tasktimer.data.FirebaseFocusStatsRepository
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.data.FocusStatsRepository
import com.example.tasktimer.data.SampleDataInserter
import com.example.tasktimer.ui.calendar.CalendarViewModel
import com.example.tasktimer.ui.components.CategoryDialogViewModel
import com.example.tasktimer.ui.components.drawer.DrawerViewModel
import com.example.tasktimer.ui.dashboard.FocusStatsViewModel
import com.example.tasktimer.ui.home.HomeViewModel
import com.example.tasktimer.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Repositories
    single { FirestoreRepository() }
    single<FocusStatsRepository> { FirebaseFocusStatsRepository(get()) }
    single { SampleDataInserter(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { DrawerViewModel(get()) }
    viewModel { CategoryDialogViewModel(get()) }
    viewModel { FocusStatsViewModel(get()) }
}
