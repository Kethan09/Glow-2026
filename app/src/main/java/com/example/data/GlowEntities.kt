package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_items")
data class TaskItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayNumber: Int,
    val dateString: String, // e.g., "Day 1", etc. or "2026-05-26"
    val timeOfDay: String, // "MORNING", "NIGHT", "ANYTIME"
    val title: String,
    val product: String,
    val purpose: String,
    val isCompleted: Boolean = false,
    val glowPoints: Int = 10,
    val category: String // "SKIN", "HAIR", "DIET", "WORKOUT", "SUPPLEMENTS", "SPECIAL"
)

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey val dateString: String, // e.g. "Day 1" or "2026-05-26"
    val amountMl: Int,
    val goalMl: Int = 3000
)

@Entity(tableName = "diet_logs")
data class DietLogEntity(
    @PrimaryKey val dateString: String,
    val caloriesKcal: Int = 0,
    val proteinGrams: Int = 0,
    val goalCalories: Int = 2500,
    val goalProtein: Int = 140, // standard high protein target
    val notes: String = "",
    val checkedJuices: String = ""
)

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey val dateString: String,
    val hours: Float = 0f,
    val qualityScore: Int = 0, // 0 to 100
    val notes: String = ""
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey val dateString: String,
    val splitType: String, // "PUSH", "PULL", "LEGS", "RECOVERY"
    val durationMinutes: Int = 0,
    val exercisesLogged: String = "", // comma-separated or JSON
    val maxWeightKg: Float = 0f,
    val isCompleted: Boolean = false,
    val setsLogJson: String = ""
)

@Entity(tableName = "tracker_logs")
data class TrackerLogEntity(
    @PrimaryKey val dateString: String,
    val skinClarityScore: Int = 70, // 0-100%
    val hairGrowthScore: Int = 50,  // 0-100%
    val templeRegrowthNotes: String = "No baseline changes yet.",
    val generalNotes: String = ""
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "USER", "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "progress_photos")
data class ProgressPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayNumber: Int,
    val label: String, // "Before", "Week 1", "Week 2", "Week 3", "Final"
    val photoBase64: String, // Base64 encoding mock
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_states")
data class AppStateEntity(
    @PrimaryKey val keyStr: String,
    val valueStr: String
)
