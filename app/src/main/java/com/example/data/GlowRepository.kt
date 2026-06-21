package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GlowRepository(private val glowDao: GlowDao) {

    // Task flows and DB mutations
    fun getTasksForDay(dateStr: String): Flow<List<TaskItemEntity>> = glowDao.getTasksForDay(dateStr)
    fun getCompletedTaskCount(dateStr: String): Flow<Int> = glowDao.getCompletedTaskCount(dateStr)
    fun getTotalTaskCount(dateStr: String): Flow<Int> = glowDao.getTotalTaskCount(dateStr)
    suspend fun insertTask(task: TaskItemEntity) = glowDao.insertTask(task)
    suspend fun updateTask(task: TaskItemEntity) = glowDao.updateTask(task)

    // Water logs
    fun getWaterLog(dateStr: String): Flow<WaterLogEntity?> = glowDao.getWaterLog(dateStr)
    suspend fun getWaterLogDirect(dateStr: String): WaterLogEntity? = glowDao.getWaterLogDirect(dateStr)
    suspend fun insertWaterLog(waterLog: WaterLogEntity) = glowDao.insertWaterLog(waterLog)

    // Diet logs
    fun getDietLog(dateStr: String): Flow<DietLogEntity?> = glowDao.getDietLog(dateStr)
    suspend fun getDietLogDirect(dateStr: String): DietLogEntity? = glowDao.getDietLogDirect(dateStr)
    suspend fun insertDietLog(dietLog: DietLogEntity) = glowDao.insertDietLog(dietLog)

    // Sleep logs
    fun getSleepLog(dateStr: String): Flow<SleepLogEntity?> = glowDao.getSleepLog(dateStr)
    fun getAllSleepLogsFlow(): Flow<List<SleepLogEntity>> = glowDao.getAllSleepLogsFlow()
    suspend fun getSleepLogDirect(dateStr: String): SleepLogEntity? = glowDao.getSleepLogDirect(dateStr)
    suspend fun insertSleepLog(sleepLog: SleepLogEntity) = glowDao.insertSleepLog(sleepLog)

    // Workout logs
    fun getWorkoutLog(dateStr: String): Flow<WorkoutLogEntity?> = glowDao.getWorkoutLog(dateStr)
    fun getAllWorkoutLogsFlow(): Flow<List<WorkoutLogEntity>> = glowDao.getAllWorkoutLogsFlow()
    suspend fun getWorkoutLogDirect(dateStr: String): WorkoutLogEntity? = glowDao.getWorkoutLogDirect(dateStr)
    suspend fun insertWorkoutLog(workoutLog: WorkoutLogEntity) = glowDao.insertWorkoutLog(workoutLog)

    // Tracker logs
    fun getAllTrackerLogsFlow(): Flow<List<TrackerLogEntity>> = glowDao.getAllTrackerLogsFlow()
    fun getTrackerLog(dateStr: String): Flow<TrackerLogEntity?> = glowDao.getTrackerLog(dateStr)
    suspend fun getTrackerLogDirect(dateStr: String): TrackerLogEntity? = glowDao.getTrackerLogDirect(dateStr)
    suspend fun insertTrackerLog(trackerLog: TrackerLogEntity) = glowDao.insertTrackerLog(trackerLog)

    // Chat
    fun getChatMessages(): Flow<List<ChatMessageEntity>> = glowDao.getChatMessages()
    suspend fun getChatMessagesDirect(): List<ChatMessageEntity> = glowDao.getChatMessagesDirect()
    suspend fun insertChatMessage(message: ChatMessageEntity) = glowDao.insertChatMessage(message)
    suspend fun clearChatHistory() = glowDao.clearChatHistory()

    // Photos
    fun getProgressPhotos(): Flow<List<ProgressPhotoEntity>> = glowDao.getProgressPhotos()
    suspend fun insertProgressPhoto(photo: ProgressPhotoEntity) = glowDao.insertProgressPhoto(photo)

    // UI state Key-Values
    suspend fun getAppState(key: String): String? = glowDao.getAppState(key)?.valueStr
    fun getAppStateFlow(key: String): Flow<AppStateEntity?> = glowDao.getAppStateFlow(key)
    suspend fun saveAppState(key: String, value: String) {
        glowDao.insertAppState(AppStateEntity(key, value))
    }

    // Generator logic to populate lifelong tasks for a calendar date
    suspend fun ensureTasksGeneratedForDay(dayNumber: Int, dateStr: String) {
        // Check if there are already tasks for this day in db
        val existingTotal = glowDao.getTotalTaskCountDirect(dateStr)
        if (existingTotal > 0) return // Already generated!

        val tasks = mutableListOf<TaskItemEntity>()
        val dayOfWeek = DateUtils.getDayOfWeeksName(dateStr)

        val isShampooDay = dayOfWeek in listOf("Monday", "Wednesday", "Saturday")
        val isOilDay = dayOfWeek in listOf("Tuesday", "Thursday", "Sunday")

        // 1. [04:00 AM] WAKE UP
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[04:00 AM] Wake Up — Circadian Reset", product = "Weight alignment & Natural sunlight / Bright light",
            purpose = "Immediately stand up, weigh yourself, and get 10 mins natural sunlight to set circadian rhythm for the entire day.", category = "BODY", glowPoints = 10
        ))

        // 2. [04:10 AM] MORNING DRINKS
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[04:10 AM] Morning Drinks: Anti-Inflammation Elixir", product = "Warm water + Turmeric + Ginger + Black pepper (Wait 15m) -> Amla juice shot",
            purpose = "Immediately flushes digestive toxins, reduces skin swelling. Vitamin C targets excess melanin.", category = "DIET", glowPoints = 15
        ))

        // 3. [04:25 AM] MORNING SKINCARE
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[04:25 AM] Morning Skincare: Cleanser & Barrier Build", product = "CeraVe cleanser + The Ordinary Niacinamide 10% + Zinc + Neutrogena Hydro Boost + SPF 50",
            purpose = "Clears sebum, controls oil, reduces redness and provides active UV barrier protection (SPF 50 is non-negotiable!).", category = "SKIN", glowPoints = 20
        ))

        // 4. [04:40 AM] HAIR PROTOCOL
        val hairProtProduct = if (isShampooDay) {
            "Minoxidil 5% on scalp + Nizoral Anti-Dandruff Shampoo + OGX Biotin Conditioner + 2 mins Silicone massager"
        } else if (isOilDay) {
            "Minoxidil 5% on scalp + Castor Oil + Rosemary + Coconut Oil Mix + 2 mins Silicone massager"
        } else {
            "Minoxidil 5% on scalp + 2 mins Silicone scalp massager"
        }
        val hairProtPurpose = if (isShampooDay) {
            "Combats dandruff, clears DHT crust, reconstructs follicles with silicone massager (Shampoo Monday/Wednesday/Saturday)"
        } else if (isOilDay) {
            "Deep root oiling & scalp stimulation on temples & crown on non-shampoo days."
        } else {
            "Stimulates temple circulation on hair rest day."
        }
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[04:40 AM] Morning Hair Care & Massager Protocol", product = hairProtProduct,
            purpose = hairProtPurpose, category = "HAIR", glowPoints = 15
        ))

        // 5. [05:00 AM] MEDITATION
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[05:00 AM] Morning Meditation & Daily Intention", product = "Mindfulness Breathing & Future Visualization",
            purpose = "Before checking phone: 5 deep slow breaths, imagine future self, and set 3 intentions for the day (10 mins total).", category = "BODY", glowPoints = 10
        ))

        // 6. [05:15 AM] MORNING SUPPLEMENTS
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[05:15 AM] Morning Supplements (Take with breakfast)", product = "Creatine 5g, Fish Oil Omega 3, Vitamin D 2000 IU, Biotin 5000mcg, Zinc 30mg, Collagen 10g",
            purpose = "Supplements system nutrients to catalyze hair matrix activity & dermal collagen rebuild.", category = "SUPPLEMENTS", glowPoints = 15
        ))

        // 7. [05:30 AM] BREAKFAST
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[05:30 AM] Breakfast: Protein Loading", product = "4 whole eggs + 100g oats + 1 banana + 250ml milk",
            purpose = "Provides 35g+ bioavailable protein to feed muscle protein synthesis and hair keratin structure.", category = "DIET", glowPoints = 15
        ))

        // 8. [06:00 AM] WORKOUT
        val zone2 = if (dayOfWeek in listOf("Monday", "Wednesday", "Friday")) " + Zone 2 Cardio (20-30 min jog/cycle)" else ""
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[06:00 AM] High Intensity Training (Workout / Cardio)", product = "PPL Split (6 days / week)$zone2 + 10 mins Stretch after",
            purpose = "75-90 mins maximum. Progressive overload every session. Stretch 10 mins after every session.", category = "BODY", glowPoints = 25
        ))

        // 9. [08:00 AM] POST WORKOUT
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[08:00 AM] Post Workout Recovery & Skincare Reapply", product = "Shower + Vitamin C serum + Neutrogena Hydro Boost + Reapply SPF 50 + Post meal (protein/carbs)",
            purpose = "Defends skin barrier with Vitamin C, locks hydration. Reapply SPF 50.", category = "SKIN", glowPoints = 20
        ))

        // 10. [08:30 AM] DEEP WORK
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[08:30 AM] Deep Work & Cognitive Sharpness", product = "Hardest tasks first + Electrolyte water + 30m movement breaks",
            purpose = "Hardest tasks first when brain is sharpest. Take 5 min movement break every 30 mins.", category = "BODY", glowPoints = 10
        ))

        // 11. [12:30 PM] LUNCH
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[12:30 PM] Lunch: Peak Nutritional Absorption", product = "Rice + Chicken/Paneer + Dal + Vegetables + Extra Virgin Olive Oil + Yogurt/Curd",
            purpose = "Target 35-40g protein. Biggest meal of the day; supports gut health and sustained energy.", category = "DIET", glowPoints = 15
        ))

        // 12. [02:00 PM] SUNLIGHT WALK
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[02:00 PM] Post-Lunch Sunlight Walk", product = "10-15 mins walking outdoors (keep SPF 50 on)",
            purpose = "Boosts digestion, regulates blood glucose, produces natural Vitamin D, and serves as a mental reset.", category = "BODY", glowPoints = 10
        ))

        // 13. [04:00 PM] MID AFTERNOON ELIXIR
        val juiceRotating = when (dayOfWeek) {
            "Monday", "Wednesday", "Friday", "Saturday" -> Pair("Carrot + Beetroot Juice", "Boosts circulation & triggers skin health.")
            "Tuesday", "Thursday" -> Pair("Lemon + Ginger Water Link", "Clears digestion & boosts active oxidation.")
            else -> Pair("Green Juice (Cucumber/Mint/Lemon/Apple/Ginger)", "Deep blood purifier and high mineral skin food.")
        }
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[04:00 PM] Mid-Afternoon Elixir: ${juiceRotating.first}", product = "Ingredients: Raw cold-pressed extract + light snack (peanuts / fruits)",
            purpose = juiceRotating.second, category = "DIET", glowPoints = 15
        ))

        // 14. [05:00 PM] LAST MEAL
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[05:00 PM] Last Meal (Dinner) & Digestion Fasting", product = "Light dinner (Vegetables + Protein) - stop eating after this",
            purpose = "3-4 hours before sleep minimum. Earlier you stop eating = better sleep quality.", category = "DIET", glowPoints = 15
        ))

        // 15. [06:00 PM] WIND DOWN BEGINS
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[06:00 PM] Night Shift Wind Down & Screen Reduction", product = "Warm lighting + Light stretching / gentle walk",
            purpose = "Stop screens gradually, prepare mind for sleep. Triggers natural melatonin cycle.", category = "BODY", glowPoints = 10
        ))

        // 16. [07:00 PM] NIGHT SKINCARE
        val skinActivesText = when (dayOfWeek) {
            "Monday", "Sunday" -> "COSRX AHA/BHA Toner + The Ordinary Retinol 0.2% + Neutrogena Hydro Boost + Aloe Vera Gel + Laneige Lip Mask"
            "Wednesday", "Friday" -> "COSRX AHA/BHA Toner + Neutrogena Hydro Boost + Aloe Vera Gel + Laneige Lip Mask"
            "Tuesday", "Saturday" -> "The Ordinary Alpha Arbutin 2% (Tan removal) + Neutrogena Hydro Boost + Aloe Vera Gel + Laneige Lip Mask"
            "Thursday" -> "The Ordinary Alpha Arbutin 2% (Tan removal) + The Ordinary Retinol 0.2% + Neutrogena Hydro Boost + Aloe Vera Gel + Laneige Lip Mask"
            else -> "COSRX AHA/BHA Toner + Neutrogena Hydro Boost + Aloe Vera Gel + Laneige Lip Mask"
        }
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[07:00 PM] Night Skincare Routine", product = "CeraVe Cleanser + $skinActivesText + Caffeine Eye Cream",
            purpose = "Cleanses day pollution, triggers skin cell turnover, fades forehead/cheek tan, and depuffs eyes.", category = "SKIN", glowPoints = 20
        ))

        // 17. [07:20 PM] NIGHT HAIR + OIL
        val nightHairProd = if (isOilDay) {
            "Minoxidil 5% PM Dose + Finasteride 1mg after food + Castor & Coconut Mix Overnight Hair Oil"
        } else {
            "Minoxidil 5% PM Dose + Finasteride 1mg after food"
        }
        val nightHairPurp = if (isOilDay) {
            "PM growth trigger + DHT blockage. Overnight oil locks moisture during recovery sleep state."
        } else {
            "PM growth trigger + DHT block. Critical for permanent temple and crown density regrowth."
        }
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[07:20 PM] Night Hair Growth Protocol", product = nightHairProd,
            purpose = nightHairPurp, category = "HAIR", glowPoints = 20
        ))

        // 18. [07:30 PM] NIGHT SUPPLEMENTS
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[07:30 PM] Night Supplements Pack", product = "Saw Palmetto, Vitamin E 15mg, MSM 1000-3000mg, Magnesium 300-400mg",
            purpose = "Reduces overnight DHT levels, enhances cellular recovery, and Magnesium triggers deep slow wave sleep.", category = "SUPPLEMENTS", glowPoints = 15
        ))

        // 19. [07:45 PM] NIGHT MEDITATION & READING
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[07:45 PM] Night Meditation & Box Breathing", product = "Box breathing x8 (4 in, 4 hold, 4 out, 4 hold) + Progressive Muscle Relaxation + Gratitude",
            purpose = "Lowers cortisol, balances nervous system. Red physical book reading — strictly no phone.", category = "BODY", glowPoints = 10
        ))

        // 20. [08:00 PM] JAWLINE + NECK WORK
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[08:00 PM] Facial Aesthetics & Neck Sculpting", product = "Chin tucks 3x15, Neck curls 3 sets, Neck extensions/raises + Face yoga 10 mins + Mewing all day",
            purpose = "Reduces facial water weight, trains cheek lines, sharpens submandibular angle, and builds posture.", category = "BODY", glowPoints = 15
        ))

        // 21. [08:30 PM] SLEEP
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[08:30 PM] Sleep State Activation", product = "Cool, dark, quiet room (target 7.5 hrs minimum)",
            purpose = "Non negotiable wake 4 AM link sleep 8:30 PM. Essential for mitotic tissue repair & cell synthesis.", category = "BODY", glowPoints = 15
        ))

        // --- 6. SPECIAL ROTATING 30-DAY TRANSFORMATION CHALLENGE TASK ---
        when (dayNumber) {
            1 -> tasks.add(SpecialTask("Hydration Detox Challenge", "Water target 3L", "Flush system detox", "SKIN", 15, dayNumber, dateStr))
            2 -> tasks.add(SpecialTask("Scalp Massage Challenge", "Silicone Massager", "Improve blood flow to temples", "HAIR", 15, dayNumber, dateStr))
            3 -> tasks.add(SpecialTask("AHA/BHA Exfoliation Challenge", "COSRX Toner", "Intense pore clearing session", "SKIN", 20, dayNumber, dateStr))
            4 -> tasks.add(SpecialTask("Hair Mask Treatment Challenge", "Deep repair mask", "Intense hair shaft repair & gloss", "HAIR", 20, dayNumber, dateStr))
            5 -> tasks.add(SpecialTask("Glow Assessment Challenge", "Visual self-inspection", "Identify pigmentation and tan status", "SPECIAL", 15, dayNumber, dateStr))
            6 -> tasks.add(SpecialTask("Scalp Scrub Challenge", "Sea Salt Scalp Scrub", "Dandruff flakes and residue removal", "HAIR", 20, dayNumber, dateStr))
            7 -> tasks.add(SpecialTask("Weekly Hydration Recovery Challenge", "Sheet Mask (Hyaluronic)", "Reset skin barrier, resting day", "SPECIAL", 25, dayNumber, dateStr))
            
            8 -> tasks.add(SpecialTask("Active Tan Removal Challenge", "Alpha Arbutin boost", "Target forehead and cheek tanning", "SKIN", 20, dayNumber, dateStr))
            9 -> tasks.add(SpecialTask("Scalp Scrub & Wash Challenge", "Teatree scrub", "Clean follicle openings", "HAIR", 20, dayNumber, dateStr))
            10 -> tasks.add(SpecialTask("Dermarolling Temple Challenge", "0.5mm Dermaroller + Minoxidil boost", "Stimulate hair follicles & collagen on temples. Disinfect dermaroller with rubbing alcohol first!", "HAIR", 30, dayNumber, dateStr))
            11 -> tasks.add(SpecialTask("Chemical Exfoliation Challenge", "AHA BHA Serum", "Remove superficial hyperpigmentation", "SKIN", 20, dayNumber, dateStr))
            12 -> tasks.add(SpecialTask("Aloe Vera Rich Mask Challenge", "Pure Aloe Vera Gel", "Soothe skin and repair barrier", "SKIN", 20, dayNumber, dateStr))
            13 -> tasks.add(SpecialTask("Intense Deep Hair Mask Challenge", "Macadamia Mask", "Locks moisture in dry hair strands", "HAIR", 20, dayNumber, dateStr))
            14 -> tasks.add(SpecialTask("Mid-Way Recovery Rest Challenge", "Zero active actives", "Skin and hair rest, full hydration focus", "SPECIAL", 25, dayNumber, dateStr))
            
            15 -> tasks.add(SpecialTask("At-home HydraFacial Challenge", "Steamer + Pore vacuum + Hydration water", "Deep clean congestion and blackheads", "SKIN", 30, dayNumber, dateStr))
            16 -> tasks.add(SpecialTask("Hair Deep Nourish Mask Challenge", "Argan Oil mask", "Soften brittle hair and style enhancement", "HAIR", 20, dayNumber, dateStr))
            17 -> tasks.add(SpecialTask("Red Light Therapy Challenge", "RLT Face mask / Scalp comb", "Boost cellular repair, scalp collagen", "SPECIAL", 25, dayNumber, dateStr))
            18 -> tasks.add(SpecialTask("Follicle Cleanse Scrub Challenge", "Scalp scrub", "Clear sebum blockage", "HAIR", 20, dayNumber, dateStr))
            19 -> tasks.add(SpecialTask("Glow Serum Retinol Challenge", "Ordinary Retinol", "Intimate wrinkle and acne marks treatment", "SKIN", 20, dayNumber, dateStr))
            20 -> tasks.add(SpecialTask("Advanced RLT Challenge", "RLT Scalp laser tool", "Stimulate lazy hair follicles in temples", "HAIR", 25, dayNumber, dateStr))
            21 -> tasks.add(SpecialTask("High Protein Loading Challenge", "Paneer/Eggs special meal", "Support muscle building and hair keratin", "DIET", 20, dayNumber, dateStr))
            
            22 -> tasks.add(SpecialTask("Soothing Aloe Mask Challenge", "Chilled Aloe Vera Gel", "Soothe redness and protect skin tone", "SKIN", 20, dayNumber, dateStr))
            23 -> tasks.add(SpecialTask("Toner Pore Clarification Challenge", "AHA BHA Toner", "Polish skin layer", "SKIN", 20, dayNumber, dateStr))
            24 -> tasks.add(SpecialTask("Premium Keratin Mask Challenge", "Salon grade mask", "Keratin restoration for supreme temple hair", "HAIR", 25, dayNumber, dateStr))
            25 -> tasks.add(SpecialTask("Mild Chemical Peel Challenge", "Lactic Acid/Salicylic Acid", "Tan removal and dramatic brightening", "SKIN", 30, dayNumber, dateStr))
            26 -> tasks.add(SpecialTask("High-Grade Hydration Challenge", "Snail Mucin + Hydro Boost", "Maximize skin glassiness and luster", "SKIN", 20, dayNumber, dateStr))
            27 -> tasks.add(SpecialTask("Dermarolling Temple Challenge", "0.5mm Dermaroller + Minoxidil boost", "Final intense trigger for temple regrowth", "HAIR", 35, dayNumber, dateStr))
            28 -> tasks.add(SpecialTask("Overnight Oil & Scalp Scrub Challenge", "Custom mix + Massage", "Clean and hydrate roots deeply", "HAIR", 20, dayNumber, dateStr))
            29 -> tasks.add(SpecialTask("Red Light + Peel recovery Challenge", "RLT light + Aloe", "Calm skin barrier, enhance final glow", "SPECIAL", 25, dayNumber, dateStr))
            else -> tasks.add(SpecialTask("30-Day Milestone Achieved!", "Comparison Camera", "Save progress photo, document lifelong results!", "SPECIAL", 50, dayNumber, dateStr))
        }

        glowDao.insertTasks(tasks)
    }

    private fun SpecialTask(title: String, product: String, purpose: String, category: String, points: Int, day: Int, date: String): TaskItemEntity {
        return TaskItemEntity(
            dayNumber = day, dateString = date, timeOfDay = "ANYTIME",
            title = "[CHALLENGE] $title", product = product, purpose = purpose, category = category, glowPoints = points
        )
    }
}
