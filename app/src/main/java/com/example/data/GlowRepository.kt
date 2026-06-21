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

        // 1. [04:00 AM] WAKE UP — CIRCADIAN RESET
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[04:00 AM] Wake Up — Scale Tracking & Sun", product = "Weight tracking + 10 min outdoor sunlight",
            purpose = "Immediately stand up, weigh yourself, and step into natural sunlight for 10 mins to set your circadian cortisol rhythm.", category = "BODY", glowPoints = 10
        ))

        // 2. [04:10 AM] MORNING DRINKS
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[04:10 AM] Morning Drinks: Anti-Inflammation Catalyst", product = "Turmeric + Ginger + Black pepper (Wait 15m) -> 1 Shot Amla Juice",
            purpose = "Flushes morning digestive toxins, reduces skin swelling, and supplies deep Vitamin C to block skin pigmentation.", category = "DIET", glowPoints = 15
        ))

        // 3. [04:25 AM] MORNING SKINCARE
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[04:25 AM] Morning Skincare: Shield & Hydration Active", product = "CeraVe Cleanser -> Niacinamide 10% + Zinc 1% -> Neutrogena Hydro Boost -> SPF 50",
            purpose = "Clearing morning sebum, shrinking pores, deep hydration, and establishing massive active UV defense.", category = "SKIN", glowPoints = 20
        ))

        // 4. [04:40 AM] HAIR PROTOCOL
        val hairProtProduct = if (isShampooDay) {
            "Minoxidil 5% + Nizoral Shampoo + OGX Biotin Conditioner + 2 min scalp massage"
        } else if (isOilDay) {
            "Minoxidil 5% + Castor/Rosemary/Coconut Oil Mix + 2 min scalp massage"
        } else {
            "Minoxidil 5% + 2 min scalp massage (temples & crown)"
        }
        val hairProtPurpose = if (isShampooDay) {
            "Combats dandruff, washes off DHT scale buildup, reconstructs hair matrix (Shampoo Monday/Wednesday/Saturday)"
        } else if (isOilDay) {
            "Delivers deep root nourishing and scalp stimulation to temple/crown structures (Oiling Tuesday/Thursday/Sunday)"
        } else {
            "Restores temple/crown capillary blood flow on active hair rest day (Friday)"
        }
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[04:40 AM] Hair Care & Scalp Massage", product = hairProtProduct,
            purpose = hairProtPurpose, category = "HAIR", glowPoints = 15
        ))

        // 5. [05:00 AM] MIND & BREATH RESET
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[05:00 AM] Mind & Breath Reset", product = "10 min deep breathing + visualization + 3 daily intentions",
            purpose = "Lowers morning cortisol, centers focus, and mentally logs target milestones BEFORE accessing sensory notifications.", category = "BODY", glowPoints = 10
        ))

        // 6. [05:15 AM] MORNING SUPPLEMENTS
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[05:15 AM] Morning Supplements (With breakfast)", product = "Creatine 5g, Fish Oil, Vitamin D3 2000 IU, Biotin 5000mcg, Zinc 30mg, Collagen Peptides 10g",
            purpose = "Supplements vital cell blocks to trigger high-grade hair matrix density and facial collagen integrity.", category = "SUPPLEMENTS", glowPoints = 15
        ))

        // 7. [05:30 AM] BREAKFAST
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[05:30 AM] Breakfast: Fuel & Protein Loading", product = "4 eggs + 100g oats + 1 banana + 250ml milk",
            purpose = "Delivers 35g+ high-bioavailability protein to fuel muscle protein synthesis and active keratin production.", category = "DIET", glowPoints = 15
        ))

        // 8. [06:00 AM] WORKOUT
        val workoutDetails = when (dayOfWeek) {
            "Monday" -> Pair("MONDAY — Push A", "Incline Bench (4 sets), DB Shoulder Press (3 sets), Incline DB Press (3 sets), Lateral Raises (4 sets), Cable Fly (3 sets), Tricep Pushdown (3 sets) + 20-30 min Zone 2 Cardio + stretching")
            "Tuesday" -> Pair("TUESDAY — Pull A", "Pull-Ups/Lat Pulldown (4 sets), Chest Supported Row (3 sets), Single Arm Lat Pulldown (3 sets), Face Pulls (4 sets), DB Curl (3 sets), Hammer Curl (3 sets)")
            "Wednesday" -> Pair("WEDNESDAY — Legs A", "Barbell Squats (4 sets), RDL (4 sets), Leg Press (3 sets), Leg Curl (3 sets), Standing Calf Raises (4 sets), Hanging Leg Raises (3 sets) + Cardio + stretching")
            "Thursday" -> Pair("THURSDAY — Push B", "Seated DB Press (4 sets), Incline Machine Press (3 sets), Lateral Raises (5 sets), Rear Delt Fly (4 sets), Tricep Overhead Extension (3 sets), Dips (3 sets)")
            "Friday" -> Pair("FRIDAY — Pull B", "Barbell Row (4 sets), Lat Pulldown Neutral (3 sets), Seated Cable Row (3 sets), Face Pulls (3 sets), Incline DB Curl (3 sets), Hammer Curl (3 sets) + Cardio + stretching")
            "Saturday" -> Pair("SATURDAY — Legs B", "Bulgarian Split Squats (3 sets), RDL (3 sets), Leg Extension (3 sets), Leg Curl (3 sets), Calf Raises (4 sets), Neck Curls (3 sets), Neck Extensions (3 sets), Side Neck Raises (3 sets)")
            else -> Pair("SUNDAY — Recovery & Mobility", "Active rest. Walking, stretching, mobility routines, weight tracking, and clean high-protein nutrition.")
        }
        val isRestDay = dayOfWeek == "Sunday"
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[06:00 AM] Gym Session: ${workoutDetails.first}", product = workoutDetails.second,
            purpose = if (isRestDay) "Allows myofibrillar recovery and total muscle density repair." else "Ensure progressive overload by logging weights and reps. Complete the 10 min stretch post-workout.",
            category = "BODY", glowPoints = if (isRestDay) 15 else 25
        ))

        // 9. [08:00 AM] POST WORKOUT SKINCARE REAPPLY
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "MORNING",
            title = "[08:00 AM] Post Workout Skincare & Refresh", product = "Shower + Vitamin C serum + Neutrogena Hydro Boost + SPF 50",
            purpose = "Protects dermal layers from daily oxidation stresses using Vitamin C, locks hydration, and secures outdoor active UV shielding.", category = "SKIN", glowPoints = 20
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
            title = "[12:30 PM] Lunch: Peak Nutritional Absorption", product = "Rice + Chicken/Paneer + Dal + Cruciferous vegetables + Olive Oil + Yogurt/Curd",
            purpose = "Target 35-40g protein. Largest macronutrient intake; supports gut microbiota biome and sustained growth.", category = "DIET", glowPoints = 15
        ))

        // 12. [02:00 PM] SUNLIGHT WALK
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[02:00 PM] Post-Lunch Sunlight Walk", product = "10-15 mins walking outdoors (keep SPF 50 shields active)",
            purpose = "Accelerates lymphatic digestion, balances blood glucose curves, and supplies natural active endocrine support.", category = "BODY", glowPoints = 10
        ))

        // 13. [04:00 PM] MID AFTERNOON JUICE
        val juiceRotating = when (dayOfWeek) {
            "Monday", "Wednesday", "Friday", "Saturday" -> Pair("Carrot + Beetroot Juice", "Carrot & Beetroot cold-pressed mix: elevates vascular circulation and triggers skin brightening.")
            "Tuesday", "Thursday" -> Pair("Lemon + Ginger Water", "Lemon & Ginger infused water: clears digestion pathways and stimulates active organic oxidation.")
            else -> Pair("Cucumber + Mint + Lemon + Green Apple + Ginger Juice", "Green Elixir: deep systemic blood purifier and high-density mineral skin food.")
        }
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[04:00 PM] Afternoon Juice: ${juiceRotating.first}", product = "Ingredients: Raw cold pressed extract + raw handful of clean nuts/fruits",
            purpose = juiceRotating.second, category = "DIET", glowPoints = 15
        ))

        // 14. [05:00 PM] LAST MEAL
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[05:00 PM] dinner: Clean Protein & Fasting Initiation", product = "Light greens + clean protein (Chicken/Paneer/Dal/Fish)",
            purpose = "Allows 3.5 hours minimum fasting window before deep sleep state block, maximizing GH secretion.", category = "DIET", glowPoints = 15
        ))

        // 15. [06:00 PM] WIND DOWN BEGINS
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "ANYTIME",
            title = "[06:00 PM] Screen Reduction & Light Dimming", product = "Ambient warm lighting + light static stretching",
            purpose = "Stop screens to restore visual cortex rest, triggering normal, non-suppressed melatonin synthesis.", category = "BODY", glowPoints = 10
        ))

        // 16. [07:00 PM] NIGHT SKINCARE (Simplified Rotate Program)
        val skinActivesText = when (dayOfWeek) {
            "Monday", "Thursday" -> Pair(
                "CeraVe Cleanser + Caffeine Eye Cream -> Alpha Arbutin 2% + Neutrogena Hydro Boost",
                "Mon/Thu Target: Pigmentation control, cheek/forehead tan reduction, secure skin barrier."
            )
            "Wednesday", "Saturday" -> Pair(
                "CeraVe Cleanser + Caffeine Eye Cream -> AHA/BHA Toner + Neutrogena Hydro Boost",
                "Wed/Sat Target: Gentle exfoliation, clear follicle clogging, and refine skin texture."
            )
            else -> Pair(
                "CeraVe Cleanser + Caffeine Eye Cream -> Neutrogena Hydro Boost + Pure Aloe Vera Gel",
                "Tue/Fri/Sun Barrier Recovery: Actives-free soothing cycle to rest the skin barrier & avoid irritation."
            )
        }
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[07:00 PM] Night Skincare Routine",
            product = skinActivesText.first,
            purpose = skinActivesText.second,
            category = "SKIN",
            glowPoints = 20
        ))

        // 17. [07:20 PM] NIGHT HAIR + OIL
        val nightHairProd = if (isOilDay) {
            "Minoxidil 5% + Castor/Coconut overnight oil + Finasteride 1mg"
        } else {
            "Minoxidil 5% + Finasteride 1mg"
        }
        val nightHairPurp = if (isOilDay) {
            "Suppresses scalp DHT systematically (via Finasteride) and locks deep moisture overnight (Oiling days: Tue/Thu/Sun)."
        } else {
            "Suppresses cell-destroying DHT systematically while sustaining standard follicle growth stimulation."
        }
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[07:20 PM] Night Scalp & Hair Protocol", product = nightHairProd,
            purpose = nightHairPurp, category = "HAIR", glowPoints = 20
        ))

        // 18. [07:30 PM] NIGHT SUPPLEMENTS
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[07:30 PM] Night Supplements Pack", product = "Vitamin E 15mg, MSM 1000–3000mg, Magnesium Glycinate 300–400mg",
            purpose = "Promotes deep cellular repair, supports hair matrix, and triggers high-density sleep (Magnesium Glycinate).", category = "SUPPLEMENTS", glowPoints = 15
        ))

        // 19. [07:45 PM] NIGHT MEDITATION & READING
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[07:45 PM] Mind Stillness & Box Breathing", product = "Box breathing x8 + book reading (strictly no phones/blue light)",
            purpose = "Decreases cardiac rhythm, down-regulates overnight stress hormones for pristine anabolic states.", category = "BODY", glowPoints = 10
        ))

        // 20. [08:00 PM] JAWLINE + POSTURE
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[08:00 PM] Facial Aesthetics & Neck Sculpting", product = "Chin Tucks (3x15) + Neck Curls + Face Yoga (10 min) + Mewing throughout day",
            purpose = "Corrects head positioning, sculpts submandibular definitions, and drains facial excess water retention.", category = "BODY", glowPoints = 15
        ))

        // 21. [08:30 PM] SLEEP
        tasks.add(TaskItemEntity(
            dayNumber = dayNumber, dateString = dateStr, timeOfDay = "NIGHT",
            title = "[08:30 PM] High-Yield Recovery Sleep", product = "Pitch-black, cool room (18-20°C), 7.5 to 8.5 hours total sleep duration",
            purpose = "Non-negotiable recovery block for hair mitosis, muscle rebuild, and growth hormone pulses.", category = "BODY", glowPoints = 15
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
