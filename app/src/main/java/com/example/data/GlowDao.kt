package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GlowDao {

    // --- Task Items ---
    @Query("SELECT * FROM task_items WHERE dateString = :dateStr ORDER BY id ASC")
    fun getTasksForDay(dateStr: String): Flow<List<TaskItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItemEntity>)

    @Update
    suspend fun updateTask(task: TaskItemEntity)

    @Query("SELECT COUNT(*) FROM task_items WHERE dateString = :dateStr AND isCompleted = 1")
    fun getCompletedTaskCount(dateStr: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM task_items WHERE dateString = :dateStr")
    fun getTotalTaskCount(dateStr: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM task_items WHERE dateString = :dateStr")
    suspend fun getTotalTaskCountDirect(dateStr: String): Int

    // --- Water Logs ---
    @Query("SELECT * FROM water_logs WHERE dateString = :dateStr")
    fun getWaterLog(dateStr: String): Flow<WaterLogEntity?>

    @Query("SELECT * FROM water_logs WHERE dateString = :dateStr")
    suspend fun getWaterLogDirect(dateStr: String): WaterLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLogEntity)

    // --- Diet Logs ---
    @Query("SELECT * FROM diet_logs WHERE dateString = :dateStr")
    fun getDietLog(dateStr: String): Flow<DietLogEntity?>

    @Query("SELECT * FROM diet_logs WHERE dateString = :dateStr")
    suspend fun getDietLogDirect(dateStr: String): DietLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietLog(dietLog: DietLogEntity)

    // --- Sleep Logs ---
    @Query("SELECT * FROM sleep_logs WHERE dateString = :dateStr")
    fun getSleepLog(dateStr: String): Flow<SleepLogEntity?>

    @Query("SELECT * FROM sleep_logs ORDER BY dateString ASC")
    fun getAllSleepLogsFlow(): Flow<List<SleepLogEntity>>

    @Query("SELECT * FROM sleep_logs WHERE dateString = :dateStr")
    suspend fun getSleepLogDirect(dateStr: String): SleepLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(sleepLog: SleepLogEntity)

    // --- Workout Logs ---
    @Query("SELECT * FROM workout_logs WHERE dateString = :dateStr")
    fun getWorkoutLog(dateStr: String): Flow<WorkoutLogEntity?>

    @Query("SELECT * FROM workout_logs ORDER BY dateString ASC")
    fun getAllWorkoutLogsFlow(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE dateString = :dateStr")
    suspend fun getWorkoutLogDirect(dateStr: String): WorkoutLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(workoutLog: WorkoutLogEntity)

    // --- Tracker Logs (Skin & Hair) ---
    @Query("SELECT * FROM tracker_logs ORDER BY dateString ASC")
    fun getAllTrackerLogsFlow(): Flow<List<TrackerLogEntity>>

    @Query("SELECT * FROM tracker_logs WHERE dateString = :dateStr")
    fun getTrackerLog(dateStr: String): Flow<TrackerLogEntity?>

    @Query("SELECT * FROM tracker_logs WHERE dateString = :dateStr")
    suspend fun getTrackerLogDirect(dateStr: String): TrackerLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackerLog(trackerLog: TrackerLogEntity)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getChatMessagesDirect(): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()

    // --- Progress Photos ---
    @Query("SELECT * FROM progress_photos ORDER BY timestamp ASC")
    fun getProgressPhotos(): Flow<List<ProgressPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressPhoto(photo: ProgressPhotoEntity)

    // --- App States ---
    @Query("SELECT * FROM app_states WHERE keyStr = :key")
    suspend fun getAppState(key: String): AppStateEntity?

    @Query("SELECT * FROM app_states WHERE keyStr = :key")
    fun getAppStateFlow(key: String): Flow<AppStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppState(state: AppStateEntity)
}
