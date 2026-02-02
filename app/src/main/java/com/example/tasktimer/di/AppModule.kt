package com.example.tasktimer.di

import com.example.tasktimer.data.FirebaseFocusStatsRepository
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.data.FocusStatsRepository
import com.example.tasktimer.data.RoomRepository
import com.example.tasktimer.data.SampleDataInserter
import com.example.tasktimer.data.local.AppDatabase
import com.example.tasktimer.data.local.RoomCategoryRepository
import com.example.tasktimer.data.local.RoomFocusStatsRepository
import com.example.tasktimer.data.local.RoomTaskRepository
import com.example.tasktimer.ui.calendar.CalendarViewModel
import com.example.tasktimer.ui.components.CategoryDialogViewModel
import com.example.tasktimer.ui.components.drawer.DrawerViewModel
import com.example.tasktimer.ui.dashboard.FocusStatsViewModel
import com.example.tasktimer.ui.home.HomeViewModel
import com.example.tasktimer.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single { AppDatabase.getDatabase(androidContext()) }
    
    // DAOs
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().categoryDao() }
    
    // Room Repositories
    single { RoomCategoryRepository(get()) }
    single { RoomTaskRepository(get(), get()) }
    single { RoomFocusStatsRepository(get(), get()) }
    
    // Firebase Repository (para sincronização)
    single { FirestoreRepository() }
    
    // Repositório unificado com sincronização Room + Firebase
    single { RoomRepository(get(), get(), get(), get<FirestoreRepository>()) }
    
    single { SampleDataInserter(get()) }

    // ViewModels - Agora usando RoomRepository
    viewModel { HomeViewModel(get<RoomRepository>(), get()) }
    viewModel { CalendarViewModel(get<RoomRepository>()) }
    viewModel { SearchViewModel(get<RoomRepository>()) }
    viewModel { DrawerViewModel(get<RoomRepository>()) }
    viewModel { CategoryDialogViewModel(get<RoomRepository>()) }
    viewModel { FocusStatsViewModel(get<RoomRepository>()) }
}
