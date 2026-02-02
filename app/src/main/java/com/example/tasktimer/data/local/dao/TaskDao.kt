package com.example.tasktimer.data.local.dao

import androidx.room.*
import com.example.tasktimer.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dateTime ASC")
    fun getAllFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    suspend fun getAll(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE dateTime >= :startDateTime AND dateTime < :endDateTime ORDER BY dateTime ASC")
    suspend fun getByDateRange(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND completedAt >= :startDateTime AND completedAt < :endDateTime")
    suspend fun getCompletedByDateRange(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId")
    suspend fun getByCategoryId(categoryId: String): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET categoryId = NULL WHERE categoryId = :categoryId")
    suspend fun removeCategoryFromTasks(categoryId: String)

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<TaskEntity>>
}
