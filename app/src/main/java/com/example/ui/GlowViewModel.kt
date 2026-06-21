package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GlowViewModel(application: Application) : AndroidViewModel(application) {

    private val database = GlowDatabase.getDatabase(application)
    private val repository = GlowRepository(database.glowDao())

    // --- State: Selected Day Tracking ---
    // --- State: Calendar Lifelong Date Tracking ---
    private val _selectedDay = MutableStateFlow(1)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    private val _selectedDateString = MutableStateFlow(DateUtils.getTodayString())
    val selectedDateString: StateFlow<String> = _selectedDateString.asStateFlow()

    private val _startDate = MutableStateFlow(DateUtils.getTodayString())
    val startDate: StateFlow<String> = _startDate.asStateFlow()

    // --- State: Database Reactive Flows ---
    val tasksFlow: StateFlow<List<TaskItemEntity>> = selectedDateString
        .flatMapLatest { dateStr -> repository.getTasksForDay(dateStr) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterLogFlow: StateFlow<WaterLogEntity?> = selectedDateString
        .flatMapLatest { dateStr -> repository.getWaterLog(dateStr) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dietLogFlow: StateFlow<DietLogEntity?> = selectedDateString
        .flatMapLatest { dateStr -> repository.getDietLog(dateStr) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sleepLogFlow: StateFlow<SleepLogEntity?> = selectedDateString
        .flatMapLatest { dateStr -> repository.getSleepLog(dateStr) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val workoutLogFlow: StateFlow<WorkoutLogEntity?> = selectedDateString
        .flatMapLatest { dateStr -> repository.getWorkoutLog(dateStr) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allWorkoutLogsFlow: StateFlow<List<WorkoutLogEntity>> = repository.getAllWorkoutLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSleepLogsFlow: StateFlow<List<SleepLogEntity>> = repository.getAllSleepLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trackerLogFlow: StateFlow<TrackerLogEntity?> = selectedDateString
        .flatMapLatest { dateStr -> repository.getTrackerLog(dateStr) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val chatMessagesFlow: StateFlow<List<ChatMessageEntity>> = repository.getChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val progressPhotosFlow: StateFlow<List<ProgressPhotoEntity>> = repository.getProgressPhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- State: System States / Backup / Streak ---
    val currentStreakFlow: StateFlow<Int> = repository.getAppStateFlow("current_streak")
        .map { it?.valueStr?.toIntOrNull() ?: 1 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    val glowPointsFlow: StateFlow<Int> = repository.getAppStateFlow("total_glow_points")
        .map { it?.valueStr?.toIntOrNull() ?: 120 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 120)

    val lastBackupFlow: StateFlow<String> = repository.getAppStateFlow("last_backup")
        .map { it?.valueStr ?: "Never" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Never")

    // --- State: Lifelong User Settings & Profile Traits ---
    val userWeightFlow: StateFlow<String> = repository.getAppStateFlow("user_weight")
        .map { it?.valueStr ?: "67" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "67")

    val userHeightFlow: StateFlow<String> = repository.getAppStateFlow("user_height")
        .map { it?.valueStr ?: "179" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "179")

    val userTargetWeightFlow: StateFlow<String> = repository.getAppStateFlow("user_target_weight")
        .map { it?.valueStr ?: "75" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "75")

    val userBudgetLevelFlow: StateFlow<String> = repository.getAppStateFlow("user_budget_level")
        .map { it?.valueStr ?: "STANDARD" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "STANDARD")

    val userMetabolicGoalFlow: StateFlow<String> = repository.getAppStateFlow("user_metabolic_goal")
        .map { it?.valueStr ?: "RECOMP" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "RECOMP")

    // --- State: AI Chat Loading & State ---
    private val _aiSearching = MutableStateFlow(false)
    val aiSearching: StateFlow<Boolean> = _aiSearching.asStateFlow()

    // --- State: Simulated Notifications Overlay & Alarms ---
    private val _activeNotification = MutableStateFlow<String?>(null)
    val activeNotification: StateFlow<String?> = _activeNotification.asStateFlow()

    // Alarms definition (Morning skincare, Night skincare, Sunscreen, Minoxidil, Supplements, Water, Workout, Sleep, Peel, Dermarolling)
    private val _alarms = MutableStateFlow(
        listOf(
            AlarmItem("Wake Up", "04:00 AM", true, "wake_up"),
            AlarmItem("Morning Skincare", "04:25 AM", true, "morning_skincare"),
            AlarmItem("Minoxidil morning", "04:40 AM", true, "minoxidil_morning"),
            AlarmItem("Morning Supplements", "05:15 AM", true, "supplements_morning"),
            AlarmItem("Workout Push-Pull-Legs", "06:00 AM", true, "workout"),
            AlarmItem("Water Reminder (2 hr)", "Every 2 Hrs", true, "water"),
            AlarmItem("Night Skincare", "07:00 PM", true, "night_skincare"),
            AlarmItem("Minoxidil night", "07:20 PM", true, "minoxidil_night"),
            AlarmItem("Sleep Prep Routine", "08:30 PM", true, "sleep"),
            AlarmItem("Weekly Dermaroller Cue", "09:30 PM", false, "peel_dermaroll")
        )
    )
    val alarms: StateFlow<List<AlarmItem>> = _alarms.asStateFlow()

    init {
        viewModelScope.launch {
            val savedStart = repository.getAppState("start_date")
            val today = DateUtils.getTodayString()
            if (savedStart == null) {
                repository.saveAppState("start_date", today)
                _startDate.value = today
            } else {
                _startDate.value = savedStart
            }
            selectDate(today)
        }
    }

    // --- Actions: Basic Navigation ---
    fun selectDay(day: Int) {
        // Calculate the target date relative to start date
        val daysDiff = day - 1
        val targetDate = DateUtils.addDays(_startDate.value, daysDiff)
        selectDate(targetDate)
    }

    fun selectDate(dateStr: String) {
        _selectedDateString.value = dateStr
        viewModelScope.launch {
            val start = _startDate.value
            val diff = DateUtils.getDaysDifference(start, dateStr)
            val dayNumber = ((diff % 30) + 30) % 30 + 1
            _selectedDay.value = dayNumber // Sync day reference
            repository.ensureTasksGeneratedForDay(dayNumber, dateStr)
            ensureLogsInitializedForDay(dateStr, dayNumber)
        }
    }

    fun selectNextDay() {
        val next = DateUtils.addDays(_selectedDateString.value, 1)
        selectDate(next)
    }

    fun selectPreviousDay() {
        val prev = DateUtils.addDays(_selectedDateString.value, -1)
        selectDate(prev)
    }

    private suspend fun ensureLogsInitializedForDay(dateStr: String, dayNum: Int) {
        // Water
        repository.getWaterLogDirect(dateStr) ?: repository.insertWaterLog(
            WaterLogEntity(dateStr, amountMl = 0, goalMl = 3000)
        )
        // Diet
        repository.getDietLogDirect(dateStr) ?: repository.insertDietLog(
            DietLogEntity(dateStr, caloriesKcal = 0, proteinGrams = 0)
        )
        // Sleep
        repository.getSleepLogDirect(dateStr) ?: repository.insertSleepLog(
            SleepLogEntity(dateStr, hours = 0f, qualityScore = 0)
        )
        // Workout
        val defaultSplit = when (dayNum % 4) {
            1 -> "PUSH"
            2 -> "PULL"
            3 -> "LEGS"
            else -> "RECOVERY"
        }
        repository.getWorkoutLogDirect(dateStr) ?: repository.insertWorkoutLog(
            WorkoutLogEntity(dateStr, splitType = defaultSplit)
        )
        // Tracker logs
        repository.getTrackerLogDirect(dateStr) ?: repository.insertTrackerLog(
            TrackerLogEntity(
                dateString = dateStr,
                skinClarityScore = 75,
                hairGrowthScore = 55,
                templeRegrowthNotes = "Day $dayNum physical status recorded. Keep fighting!"
            )
        )
    }

    // --- Actions: Daily Checklist Operations ---
    fun toggleTask(task: TaskItemEntity) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            repository.updateTask(updated)

            // Dynamic Points Adjustment
            val currentPoints = glowPointsFlow.value
            val diff = if (updated.isCompleted) task.glowPoints else -task.glowPoints
            repository.saveAppState("total_glow_points", (currentPoints + diff).coerceAtLeast(0).toString())

            // Auto alert on special tasks completed
            if (updated.isCompleted && (task.category == "SPECIAL" || task.category == "HAIR" && task.title.contains("Dermarolling"))) {
                triggerOverlayNotification("🔥 Premium protocol achieved: ${task.title}! +${task.glowPoints} Glow Points.")
            }
        }
    }

    // --- Actions: Trackers Updates ---
    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val dateStr = selectedDateString.value
            val currentLog = repository.getWaterLogDirect(dateStr) ?: WaterLogEntity(dateStr, 0)
            val updatedMl = (currentLog.amountMl + amountMl).coerceAtLeast(0)
            repository.insertWaterLog(currentLog.copy(amountMl = updatedMl))

            if (updatedMl >= currentLog.goalMl && currentLog.amountMl < currentLog.goalMl) {
                triggerOverlayNotification("💧 Hydration target reached! Ideal skin cell pressure.")
            }
        }
    }

    fun updateDiet(calories: Int, protein: Int, notes: String) {
        viewModelScope.launch {
            val dateStr = selectedDateString.value
            val currentLog = repository.getDietLogDirect(dateStr) ?: DietLogEntity(dateStr)
            repository.insertDietLog(currentLog.copy(
                caloriesKcal = calories,
                proteinGrams = protein,
                notes = notes
            ))
        }
    }

    fun toggleJuice(juiceId: String) {
        viewModelScope.launch {
            val dateStr = selectedDateString.value
            val currentLog = repository.getDietLogDirect(dateStr) ?: DietLogEntity(dateStr)
            val currentJuices = currentLog.checkedJuices.split(",").filter { it.isNotEmpty() }.toMutableList()
            if (currentJuices.contains(juiceId)) {
                currentJuices.remove(juiceId)
            } else {
                currentJuices.add(juiceId)
            }
            val newJuicesStr = currentJuices.joinToString(",")
            repository.insertDietLog(currentLog.copy(checkedJuices = newJuicesStr))
            triggerOverlayNotification("🥤 Elixirs updated! Logged daily physiological solvent.")
        }
    }

    fun updateSleep(hours: Float, quality: Int, notes: String) {
        viewModelScope.launch {
            val dateStr = selectedDateString.value
            val currentLog = repository.getSleepLogDirect(dateStr) ?: SleepLogEntity(dateStr)
            repository.insertSleepLog(currentLog.copy(
                hours = hours,
                qualityScore = quality,
                notes = notes
            ))
            if (hours >= 7.5f && currentLog.hours < 7.5f) {
                triggerOverlayNotification("🛌 Deep recovery cycle locked (8 hrs). Keratin rebuilding active.")
            }
        }
    }

    fun updateWorkout(split: String, minutes: Int, exercises: String, weight: Float, completed: Boolean, setsJson: String = "") {
        viewModelScope.launch {
            val dateStr = selectedDateString.value
            val currentLog = repository.getWorkoutLogDirect(dateStr) ?: WorkoutLogEntity(dateStr, "RECOVERY")
            repository.insertWorkoutLog(currentLog.copy(
                splitType = split,
                durationMinutes = minutes,
                exercisesLogged = exercises,
                maxWeightKg = weight,
                isCompleted = completed,
                setsLogJson = if (setsJson.isNotEmpty()) setsJson else currentLog.setsLogJson
            ))
            if (completed && !currentLog.isCompleted) {
                triggerOverlayNotification("🏋️ $split Day finished. Sharper jawline and skin oxygenation stimulated!")
            }
        }
    }

    fun updateTrackerScores(skinClarity: Int, hairGrowth: Int, templeNotes: String) {
        viewModelScope.launch {
            val dateStr = selectedDateString.value
            val currentLog = repository.getTrackerLogDirect(dateStr) ?: TrackerLogEntity(dateStr)
            repository.insertTrackerLog(currentLog.copy(
                skinClarityScore = skinClarity,
                hairGrowthScore = hairGrowth,
                templeRegrowthNotes = templeNotes
            ))
        }
    }

    // --- Actions: Lifelong User Settings & Actions ---
    fun updateWeight(weight: String) {
        viewModelScope.launch {
            repository.saveAppState("user_weight", weight)
        }
    }

    fun updateHeight(height: String) {
        viewModelScope.launch {
            repository.saveAppState("user_height", height)
        }
    }

    fun updateTargetWeight(targetWeight: String) {
        viewModelScope.launch {
            repository.saveAppState("user_target_weight", targetWeight)
        }
    }

    fun updateBudgetLevel(level: String) {
        viewModelScope.launch {
            repository.saveAppState("user_budget_level", level)
            triggerOverlayNotification("🛒 Budget mode set to $level. Recalculated dietary protocol.")
        }
    }

    fun updateMetabolicGoal(goal: String) {
        viewModelScope.launch {
            repository.saveAppState("user_metabolic_goal", goal)
            triggerOverlayNotification("🧬 Metabolic trajectory updated to: $goal. Dynamic macro benchmarks re-calibrated.")
        }
    }

    fun updateAlarmTime(tag: String, newTime: String) {
        _alarms.value = _alarms.value.map {
            if (it.tag == tag) {
                it.copy(time = newTime)
            } else it
        }
        triggerOverlayNotification("⏰ Alarm '${tag.replace("_", " ")}' updated to $newTime.")
    }

    fun resetEntireProgram() {
        viewModelScope.launch {
            repository.saveAppState("user_weight", "67")
            repository.saveAppState("user_height", "179")
            repository.saveAppState("user_target_weight", "75")
            repository.saveAppState("user_budget_level", "STANDARD")
            repository.saveAppState("total_glow_points", "120")
            repository.saveAppState("current_streak", "1")
            val today = DateUtils.getTodayString()
            repository.saveAppState("start_date", today)
            _startDate.value = today
            selectDate(today)
            triggerOverlayNotification("🔄 All tracking logs & profiles reset to default baseline.")
        }
    }

    // --- Actions: Alarms Control ---
    fun toggleAlarm(key: String) {
        _alarms.value = _alarms.value.map {
            if (it.tag == key) {
                val updatedState = !it.isActive
                if (updatedState) {
                    triggerOverlayNotification("⏰ Alarm '${it.title}' activated for ${it.time}.")
                }
                it.copy(isActive = updatedState)
            } else it
        }
    }

    // --- Actions: AI Assistant Integration ---
    fun getCurrentRoutineContext(): String {
        val day = _selectedDay.value
        val tasks = tasksFlow.value
        val taskStr = if (tasks.isEmpty()) {
            "No tasks scheduled."
        } else {
            val completed = tasks.count { it.isCompleted }
            val total = tasks.size
            "Completed $completed out of $total tasks today."
        }

        val water = waterLogFlow.value
        val waterStr = if (water == null) {
            "Water logged: 0 ml (Goal: 3000 ml)."
        } else {
            "Water logged: ${water.amountMl} ml / ${water.goalMl} ml goal."
        }

        val diet = dietLogFlow.value
        val dietStr = if (diet == null) {
            "Diet not logged yet. No daily juices logged today."
        } else {
            val juicesList = diet.checkedJuices.split(",").filter { it.isNotEmpty() }
            val juicesStr = if (juicesList.isEmpty()) "None logged yet" else juicesList.joinToString(", ")
            "Diet stats: ${diet.caloriesKcal} kcal / ${diet.goalCalories} kcal goal, ${diet.proteinGrams}g / ${diet.goalProtein}g protein. Daily elixirs/juices checked: $juicesStr."
        }

        val sleep = sleepLogFlow.value
        val sleepStr = if (sleep == null) {
            "Sleep not logged yet."
        } else {
            "Sleep stats: ${sleep.hours} hours logged. Quality: ${sleep.qualityScore}/100. Notes: ${sleep.notes}"
        }

        val workout = workoutLogFlow.value
        val workoutStr = if (workout == null) {
            "Workout split today: RECOVERY / REST."
        } else {
            "Workout: ${workout.splitType} Split. Exercises: ${workout.exercisesLogged}. Weight: ${workout.maxWeightKg} kg. Progress: ${if (workout.isCompleted) "Completed" else "Incomplete"}."
        }

        val tracker = trackerLogFlow.value
        val trackerStr = if (tracker == null) {
            "Physical tracking stats: None logged for today."
        } else {
            "Physical tracking metrics - Skin Clarity: ${tracker.skinClarityScore}%, Hair Health: ${tracker.hairGrowthScore}%. Notes: ${tracker.templeRegrowthNotes}"
        }

        val photoCount = progressPhotosFlow.value.size

        return """
            The user is currently on Day $day of their 30-Day Glow-up Program.
            Here is their daily routine progress and live logs:
            - Daily Task List Checkpoints: $taskStr
            - Current Water Balance: $waterStr
            - Nutrition/Diet Log: $dietStr
            - Sleep/Recovery Log: $sleepStr
            - Physical Training (Gym): $workoutStr
            - Skin & Hair Health Tracker: $trackerStr
            - Visual progress photos in database: $photoCount photos uploaded so far.
        """.trimIndent()
    }

    fun askAiAssistant(query: String) {
        if (query.trim().isEmpty()) return

        viewModelScope.launch {
            // Save User message
            repository.insertChatMessage(ChatMessageEntity(sender = "USER", text = query))
            _aiSearching.value = true

            // Gather past logs for context if required
            val currentHistory = repository.getChatMessagesDirect()
            val historyPairs = currentHistory.map { pairSenderText(it.sender == "USER", it.text) }

            // Retrieve today's routine context
            val routineContext = getCurrentRoutineContext()
            
            // Format a prompt containing detailed routine logs
            val promptWithContext = """
                [USER ROUTINE CONTEXT]
                $routineContext
                
                [USER INITIATED QUERY]
                $query
            """.trimIndent()

            // Query Gemini REST API
            val response = GeminiService.askGlowUpCoach(promptWithContext, historyPairs)

            // Save AI message
            repository.insertChatMessage(ChatMessageEntity(sender = "AI", text = response))
            _aiSearching.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
            repository.insertChatMessage(ChatMessageEntity(
                sender = "AI",
                text = "Welcome back to the GlowUpX Elite Portal. Standard protocols loaded. Ask me anything about skincare, haircare, and body development."
            ))
        }
    }

    // --- Actions: Cloud Backup & Offline Sync Simulation ---
    fun performCloudBackup() {
        viewModelScope.launch {
            triggerOverlayNotification("☁️ Initializing GlowUpX Secure Cloud Handshake...")
            kotlinx.coroutines.delay(1200)
            triggerOverlayNotification("⚡ Syncing Room Database indices to Firebase server...")
            kotlinx.coroutines.delay(1000)

            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateString = formatter.format(Date())
            repository.saveAppState("last_backup", dateString)

            // Boost point reward for backup sync
            val currentPoints = glowPointsFlow.value
            repository.saveAppState("total_glow_points", (currentPoints + 20).toString())

            triggerOverlayNotification("🔒 Cloud Backup Complete! Sync Status: 100% Offline Secured.")
        }
    }

    // --- Actions: Progress Photos Mock ---
    fun addMockProgressPhoto(label: String) {
        viewModelScope.launch {
            val mockBase64 = "MOCK_IMAGE_DATA_GLOWUP"
            repository.insertProgressPhoto(
                ProgressPhotoEntity(
                    dayNumber = _selectedDay.value,
                    label = label,
                    photoBase64 = mockBase64
                )
            )
            triggerOverlayNotification("📷 Progress Photo Locked for Day ${_selectedDay.value}! Visual database revised.")
        }
    }

    // --- Actions: Real Progress Photos Upload ---
    fun addRealProgressPhoto(uri: Uri, label: String) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                if (originalBitmap != null) {
                    val maxDim = 500
                    val srcWidth = originalBitmap.width
                    val srcHeight = originalBitmap.height
                    val (width, height) = if (srcWidth > srcHeight) {
                        val ratio = srcWidth.toFloat() / srcHeight.toFloat()
                        Pair(maxDim, (maxDim / ratio).toInt())
                    } else {
                        val ratio = srcHeight.toFloat() / srcWidth.toFloat()
                        Pair((maxDim / ratio).toInt(), maxDim)
                    }
                    val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, width, height, true)
                    val outputStream = java.io.ByteArrayOutputStream()
                    scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
                    val bytes = outputStream.toByteArray()
                    val base64Str = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)

                    repository.insertProgressPhoto(
                        ProgressPhotoEntity(
                            dayNumber = _selectedDay.value,
                            label = label,
                            photoBase64 = base64Str
                        )
                    )
                    triggerOverlayNotification("📷 Daily Progress Photo Saved for Day ${_selectedDay.value}!")
                } else {
                    triggerOverlayNotification("❌ Failed to resolve content Uri.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                triggerOverlayNotification("❌ Image upload error: ${e.localizedMessage}")
            }
        }
    }

    // --- Assistant Helpers ---
    private fun triggerOverlayNotification(message: String) {
        viewModelScope.launch {
            _activeNotification.value = message
            kotlinx.coroutines.delay(4000)
            if (_activeNotification.value == message) {
                _activeNotification.value = null
            }
        }
    }

    fun clearOverlayNotification() {
        _activeNotification.value = null
    }
}

data class AlarmItem(
    val title: String,
    val time: String,
    val isActive: Boolean,
    val tag: String
)
