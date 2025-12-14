package com.example.tasktimer.ui.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tasktimer.data.MockTaskRepository

class FocusStatsViewModel : ViewModel() {

    private val _state = mutableStateOf(FocusStatsState())
    val state: State<FocusStatsState> = _state

    init {
        loadStats()
    }

    private fun loadStats() {
        try {
            val tasks = MockTaskRepository.getAllTasks()

            val stats = FocusStatsMapper.fromTasks(tasks)

            _state.value = _state.value.copy(
                isLoading = false,
                stats = listOf(stats)
            )

        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Não foi possível carregar os dados"
            )
        }
    }

    fun onPeriodChange(period: StatsPeriod) {
        _state.value = _state.value.copy(selectedPeriod = period)

        //AcHO QUE É AQUI QUE DISPARA A QUERIE PARA PEGAR OS DADOS
    }
}
