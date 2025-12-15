package com.example.tasktimer.data

import com.example.tasktimer.model.FocusStats
import com.example.tasktimer.model.PomodoroType
import com.example.tasktimer.ui.dashboard.StatsPeriod
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class FirebaseFocusStatsRepository(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
) : FocusStatsRepository {

    override suspend fun getStats(period: StatsPeriod): FocusStats {
        val (startDate, endDate) = getDateRange(period)
        
        // Buscar todas as tasks do período
        val allTasks = firestoreRepository.getTasksByDateRange(startDate, endDate)
        
        // Buscar tasks completadas do período
        val completedTasks = firestoreRepository.getCompletedTasksByDateRange(startDate, endDate)
        
        // Buscar todas as categorias para mapear os nomes
        val categories = firestoreRepository.getCategoriesMap()
        
        // Calcular minutos de foco (trabalho) baseado nas sessões REAIS
        val totalFocusMinutes = completedTasks
            .flatMap { it.pomodoroSessions }
            .filter { it.completed && it.type == PomodoroType.WORK }
            .sumOf { it.durationMinutes }
        
        // Calcular minutos de pausa baseado nas sessões REAIS
        val totalBreakMinutes = completedTasks
            .flatMap { it.pomodoroSessions }
            .filter { 
                it.completed && 
                (it.type == PomodoroType.SHORT_BREAK || it.type == PomodoroType.LONG_BREAK)
            }
            .sumOf { it.durationMinutes }
        
        // Calcular pomodoros completados (sessões de trabalho completadas)
        val completedPomodoros = completedTasks
            .flatMap { it.pomodoroSessions }
            .count { it.completed && it.type == PomodoroType.WORK }
        
        // Total de tasks completadas
        val tasksCompleted = completedTasks.size
        
        // Total de subtasks completadas
        val subtasksCompleted = completedTasks
            .flatMap { it.subtasks }
            .count { it.isCompleted }
        
        // Tasks de hoje
        val today = LocalDate.now()
        val todayTasks = allTasks.count { 
            it.dateTime.toLocalDate() == today 
        }
        
        // Foco de hoje (baseado em sessões)
        val todayFocusMinutes = completedTasks
            .filter { task ->
                task.completedAt?.toLocalDate() == today
            }
            .flatMap { it.pomodoroSessions }
            .filter { it.completed && it.type == PomodoroType.WORK }
            .sumOf { it.durationMinutes }
        
        // Foco por categoria (baseado em sessões)
        val focusByCategory = mutableMapOf<String, Int>()
        
        // Tasks COM categoria
        completedTasks
            .filter { it.categoryId != null }
            .groupBy { it.categoryId!! }
            .forEach { (categoryId, tasks) ->
                val minutes = tasks.flatMap { it.pomodoroSessions }
                    .filter { it.completed && it.type == PomodoroType.WORK }
                    .sumOf { it.durationMinutes }
                
                val categoryName = categories[categoryId] ?: "Sem nome"
                focusByCategory[categoryName] = minutes
            }
        
        // Tasks SEM categoria
        val uncategorizedMinutes = completedTasks
            .filter { it.categoryId == null }
            .flatMap { it.pomodoroSessions }
            .filter { it.completed && it.type == PomodoroType.WORK }
            .sumOf { it.durationMinutes }
        
        if (uncategorizedMinutes > 0) {
            focusByCategory["Sem categoria"] = uncategorizedMinutes
        }

        return FocusStats(
            totalFocusMinutes = totalFocusMinutes,
            totalBreakMinutes = totalBreakMinutes,
            completedPomodoros = completedPomodoros,
            tasksCompleted = tasksCompleted,
            subtasksCompleted = subtasksCompleted,
            todayTasks = todayTasks,
            todayFocusMinutes = todayFocusMinutes,
            totalTasks = allTasks.size,
            focusByCategory = focusByCategory
        )
    }

    private fun getDateRange(period: StatsPeriod): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (period) {
            StatsPeriod.TODAY -> today to today
            StatsPeriod.WEEK -> today.minus(6, ChronoUnit.DAYS) to today
            StatsPeriod.MONTH -> today.minus(29, ChronoUnit.DAYS) to today
            StatsPeriod.YEAR -> today.minus(364, ChronoUnit.DAYS) to today
        }
    }
}
