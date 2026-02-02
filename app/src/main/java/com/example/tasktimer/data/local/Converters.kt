package com.example.tasktimer.data.local

import androidx.room.TypeConverter
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.PomodoroSession
import com.example.tasktimer.model.PomodoroType
import com.example.tasktimer.model.Subtask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromSubtaskList(value: String?): List<Subtask> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<Subtask>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun subtaskListToString(list: List<Subtask>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromPomodoroSessionList(value: String?): List<PomodoroSession> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<PomodoroSession>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun pomodoroSessionListToString(list: List<PomodoroSession>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromPomodoroConfig(value: String?): PomodoroConfig? {
        return value?.let { gson.fromJson(it, PomodoroConfig::class.java) }
    }

    @TypeConverter
    fun pomodoroConfigToString(config: PomodoroConfig?): String? {
        return config?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun fromPomodoroType(value: String?): PomodoroType? {
        return value?.let { PomodoroType.valueOf(it) }
    }

    @TypeConverter
    fun pomodoroTypeToString(type: PomodoroType?): String? {
        return type?.name
    }
}
