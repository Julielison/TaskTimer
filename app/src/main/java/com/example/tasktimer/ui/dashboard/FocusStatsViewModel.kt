package com.example.tasktimer.ui.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.FirebaseFocusStatsRepository
import kotlinx.coroutines.launch

class FocusStatsViewModel : ViewModel() {

    private val repository = FirebaseFocusStatsRepository()

    private val _state = mutableStateOf(FocusStatsState())
    val state: State<FocusStatsState> = _state

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val stats = repository.getStats(_state.value.selectedPeriod)

                _state.value = _state.value.copy(
                    isLoading = false,
                    stats = listOf(stats),
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível carregar os dados: ${e.message}"
                )
            }
        }
    }

    fun onPeriodChange(period: StatsPeriod) {
        _state.value = _state.value.copy(selectedPeriod = period)
        loadStats()
    }
}
