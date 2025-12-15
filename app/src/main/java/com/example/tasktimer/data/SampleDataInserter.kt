package com.example.tasktimer.data

import androidx.compose.ui.graphics.Color
import com.example.tasktimer.model.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.util.UUID

class SampleDataInserter(
    private val repository: FirestoreRepository = FirestoreRepository()
) {

    suspend fun insertSampleData() {
        try {
            // 1. Inserir categorias primeiro
            val workCategoryId = insertWorkCategory()
            val studyCategoryId = insertStudyCategory()
            val personalCategoryId = insertPersonalCategory()
            
            delay(1000) // Aguardar categorias serem criadas
            
            // 2. Inserir tasks com sessões de Pomodoro
            insertCompletedWorkTask(workCategoryId)
            insertCompletedStudyTask(studyCategoryId)
            insertInProgressPersonalTask(personalCategoryId)
            insertUpcomingWorkTask(workCategoryId)
            
        } catch (e: Exception) {
            println("Erro ao inserir dados de exemplo: ${e.message}")
        }
    }

    private suspend fun insertWorkCategory(): String {
        return repository.addCategory(
            name = "Trabalho",
            color = Color(0xFF2196F3) // Azul
        )
    }

    private suspend fun insertStudyCategory(): String {
        return repository.addCategory(
            name = "Estudos",
            color = Color(0xFF4CAF50) // Verde
        )
    }

    private suspend fun insertPersonalCategory(): String {
        return repository.addCategory(
            name = "Pessoal",
            color = Color(0xFFFF9800) // Laranja
        )
    }

    private suspend fun insertCompletedWorkTask(categoryId: String) {
        val taskId = UUID.randomUUID().toString()
        
        val subtasks = listOf(
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Revisar código",
                isCompleted = true,
                order = 0
            ),
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Fazer testes",
                isCompleted = true,
                order = 1
            ),
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Deploy para produção",
                isCompleted = true,
                order = 2
            )
        )

        val pomodoroConfig = PomodoroConfig(
            workDurationMinutes = 25,
            breakDurationMinutes = 5,
            longBreakDurationMinutes = 15,
            pomodorosUntilLongBreak = 4,
            totalPomodoros = 4
        )

        val completedSessions = listOf(
            // Sessão 1 - Trabalho
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(3),
                endTime = LocalDateTime.now().minusHours(3).plusMinutes(25),
                durationMinutes = 25,
                type = PomodoroType.WORK,
                completed = true
            ),
            // Pausa curta 1
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(3).plusMinutes(25),
                endTime = LocalDateTime.now().minusHours(3).plusMinutes(30),
                durationMinutes = 5,
                type = PomodoroType.SHORT_BREAK,
                completed = true
            ),
            // Sessão 2 - Trabalho
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(2).minusMinutes(30),
                endTime = LocalDateTime.now().minusHours(2).minusMinutes(5),
                durationMinutes = 25,
                type = PomodoroType.WORK,
                completed = true
            ),
            // Pausa curta 2
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(2).minusMinutes(5),
                endTime = LocalDateTime.now().minusHours(2),
                durationMinutes = 5,
                type = PomodoroType.SHORT_BREAK,
                completed = true
            ),
            // Sessão 3 - Trabalho
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(2),
                endTime = LocalDateTime.now().minusHours(1).minusMinutes(35),
                durationMinutes = 25,
                type = PomodoroType.WORK,
                completed = true
            ),
            // Pausa curta 3
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(1).minusMinutes(35),
                endTime = LocalDateTime.now().minusHours(1).minusMinutes(30),
                durationMinutes = 5,
                type = PomodoroType.SHORT_BREAK,
                completed = true
            ),
            // Sessão 4 - Trabalho
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(1).minusMinutes(30),
                endTime = LocalDateTime.now().minusHours(1).minusMinutes(5),
                durationMinutes = 25,
                type = PomodoroType.WORK,
                completed = true
            )
        )

        val task = Task(
            id = taskId,
            title = "Desenvolver nova funcionalidade",
            description = "Implementar sistema de autenticação com Firebase",
            dateTime = LocalDateTime.now().minusHours(4),
            isCompleted = true,
            categoryId = categoryId,
            pomodoroConfig = pomodoroConfig,
            pomodoroSessions = completedSessions,
            subtasks = subtasks,
            createdAt = LocalDateTime.now().minusDays(1),
            completedAt = LocalDateTime.now().minusHours(1)
        )

        repository.addTaskWithSessions(task)
    }

    private suspend fun insertCompletedStudyTask(categoryId: String) {
        val taskId = UUID.randomUUID().toString()
        
        val subtasks = listOf(
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Ler capítulo 1",
                isCompleted = true,
                order = 0
            ),
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Fazer exercícios",
                isCompleted = true,
                order = 1
            )
        )

        val pomodoroConfig = PomodoroConfig(
            workDurationMinutes = 30,
            breakDurationMinutes = 10,
            longBreakDurationMinutes = 20,
            pomodorosUntilLongBreak = 3,
            totalPomodoros = 3
        )

        val completedSessions = listOf(
            // Sessão 1 - Trabalho
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(6),
                endTime = LocalDateTime.now().minusHours(5).minusMinutes(30),
                durationMinutes = 30,
                type = PomodoroType.WORK,
                completed = true
            ),
            // Pausa
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(5).minusMinutes(30),
                endTime = LocalDateTime.now().minusHours(5).minusMinutes(20),
                durationMinutes = 10,
                type = PomodoroType.SHORT_BREAK,
                completed = true
            ),
            // Sessão 2 - Trabalho
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusHours(5).minusMinutes(20),
                endTime = LocalDateTime.now().minusHours(4).minusMinutes(50),
                durationMinutes = 30,
                type = PomodoroType.WORK,
                completed = true
            )
        )

        val task = Task(
            id = taskId,
            title = "Estudar Kotlin Compose",
            description = "Aprender conceitos básicos de interface declarativa",
            dateTime = LocalDateTime.now().minusHours(7),
            isCompleted = true,
            categoryId = categoryId,
            pomodoroConfig = pomodoroConfig,
            pomodoroSessions = completedSessions,
            subtasks = subtasks,
            createdAt = LocalDateTime.now().minusDays(2),
            completedAt = LocalDateTime.now().minusHours(4)
        )

        repository.addTaskWithSessions(task)
    }

    private suspend fun insertInProgressPersonalTask(categoryId: String) {
        val taskId = UUID.randomUUID().toString()
        
        val subtasks = listOf(
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Comprar ingredientes",
                isCompleted = true,
                order = 0
            ),
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Preparar massa",
                isCompleted = false,
                order = 1
            ),
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Assar",
                isCompleted = false,
                order = 2
            )
        )

        val pomodoroConfig = PomodoroConfig(
            workDurationMinutes = 20,
            breakDurationMinutes = 5,
            longBreakDurationMinutes = 15,
            pomodorosUntilLongBreak = 4,
            totalPomodoros = 2
        )

        // Algumas sessões completadas, outras não
        val sessions = listOf(
            PomodoroSession(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                startTime = LocalDateTime.now().minusMinutes(45),
                endTime = LocalDateTime.now().minusMinutes(25),
                durationMinutes = 20,
                type = PomodoroType.WORK,
                completed = true
            )
        )

        val task = Task(
            id = taskId,
            title = "Fazer bolo para aniversário",
            description = "Preparar bolo de chocolate para festa",
            dateTime = LocalDateTime.now().plusHours(2),
            isCompleted = false,
            categoryId = categoryId,
            pomodoroConfig = pomodoroConfig,
            pomodoroSessions = sessions,
            subtasks = subtasks,
            createdAt = LocalDateTime.now().minusHours(2),
            completedAt = null
        )

        repository.addTaskWithSessions(task)
    }

    private suspend fun insertUpcomingWorkTask(categoryId: String) {
        val taskId = UUID.randomUUID().toString()
        
        val subtasks = listOf(
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Analisar requisitos",
                isCompleted = false,
                order = 0
            ),
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Criar mockups",
                isCompleted = false,
                order = 1
            ),
            Subtask(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                title = "Implementar UI",
                isCompleted = false,
                order = 2
            )
        )

        val pomodoroConfig = PomodoroConfig(
            workDurationMinutes = 25,
            breakDurationMinutes = 5,
            longBreakDurationMinutes = 15,
            pomodorosUntilLongBreak = 4,
            totalPomodoros = 6
        )

        val task = Task(
            id = taskId,
            title = "Redesign da tela principal",
            description = "Melhorar UX/UI da interface principal do app",
            dateTime = LocalDateTime.now().plusDays(1),
            isCompleted = false,
            categoryId = categoryId,
            pomodoroConfig = pomodoroConfig,
            pomodoroSessions = emptyList(), // Task futura, sem sessões ainda
            subtasks = subtasks,
            createdAt = LocalDateTime.now(),
            completedAt = null
        )

        repository.addTaskWithSessions(task)
    }
}
