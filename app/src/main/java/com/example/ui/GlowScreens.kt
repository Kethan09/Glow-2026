package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import java.util.Calendar
import androidx.compose.foundation.text.BasicTextField
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import com.example.data.DateUtils
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================
// SHARED PREMIUM UI UTILITIES & DESIGN TOKENS
// ==========================================

@Composable
fun GlowGlassCard(
    modifier: Modifier = Modifier,
    borderStrokeColor: Color = GlowBlueAccent.copy(alpha = 0.25f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlowDarkCard.copy(alpha = 0.85f)
        ),
        border = BorderStroke(1.dp, borderStrokeColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isSecondary: Boolean = false,
    testTagStr: String = ""
) {
    val containerColor = if (isSecondary) GlowDarkGrey else GlowBlueAccent
    val contentColor = if (isSecondary) GlowSilverAccent else GlowBlack

    Button(
        onClick = onClick,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTagStr),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun WaterBalanceWidget(
    waterIntakeMl: Int,
    targetMl: Int = 3000,
    onQuickAdd: (Int) -> Unit
) {
    val percent = (waterIntakeMl.toFloat() / targetMl).coerceIn(0f, 1f)
    val animatedPercent by animateFloatAsState(
        targetValue = percent,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "Water progress"
    )

    GlowGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("water_balance_widget"),
        borderStrokeColor = GlowCyanAccent.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sleek Radial Gauge Graph
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(90.dp)) {
                    val strokeWidth = 10.dp.toPx()
                    // Background Track
                    drawCircle(
                        color = GlowDarkGrey.copy(alpha = 0.6f),
                        radius = (size.minDimension - strokeWidth) / 2,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                    )
                    // Foreground Radial Progress Arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                GlowBlueAccent,
                                GlowCyanAccent,
                                GlowBlueAccent
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedPercent * 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }
                
                // Central Info Display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${(percent * 100).toInt()}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GlowSilverAccent
                    )
                    Text(
                        text = "BALANCE",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlowCyanAccent,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details and Quick Control Matrix
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "WATER BALANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowCyanAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "BIOLOGICAL SOLVENT LEVEL",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GlowSilverAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$waterIntakeMl / $targetMl ml logged today",
                    fontSize = 11.sp,
                    color = GlowMutedGrey
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Quick add water buttons for convenience
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onQuickAdd(250) },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("quick_add_250_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GlowDarkGrey,
                            contentColor = GlowSilverAccent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(text = "+250ml", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onQuickAdd(500) },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("quick_add_500_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GlowBlueAccent.copy(alpha = 0.2f),
                            contentColor = GlowCyanAccent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        border = BorderStroke(0.5.dp, GlowCyanAccent.copy(alpha = 0.3f))
                    ) {
                        Text(text = "+500ml", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MainDashboardGymPerformanceAnalysis(
    allWorkoutLogs: List<WorkoutLogEntity>,
    viewModel: GlowViewModel,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    gymDiagnosisFeedback: String,
    isGymDiagnosisLoading: Boolean,
    onUpdateDiagnosis: (String, Boolean) -> Unit,
    onMoveToTab: (Int) -> Unit
) {
    val totalWorkouts = remember(allWorkoutLogs) { allWorkoutLogs.count { it.isCompleted } }
    val maxWeightLifted = remember(allWorkoutLogs) { allWorkoutLogs.maxOfOrNull { it.maxWeightKg } ?: 0f }
    val totalGymMinutes = remember(allWorkoutLogs) { allWorkoutLogs.sumOf { it.durationMinutes } }
    
    val totalVolumeLifted = remember(allWorkoutLogs) {
        allWorkoutLogs.filter { it.setsLogJson.isNotEmpty() }.sumOf { log ->
            val (_, weights, reps) = parseSetsLogJson(log.setsLogJson)
            var logVol = 0.0
            weights.forEach { (ex, setMap) ->
                setMap.forEach { (setIdx, wt) ->
                    val r = reps[ex]?.get(setIdx) ?: 10
                    logVol += wt * r
                }
            }
            logVol
        }.toFloat()
    }

    val pushCount = remember(allWorkoutLogs) { allWorkoutLogs.count { it.splitType == "PUSH" && it.isCompleted } }
    val pullCount = remember(allWorkoutLogs) { allWorkoutLogs.count { it.splitType == "PULL" && it.isCompleted } }
    val legsCount = remember(allWorkoutLogs) { allWorkoutLogs.count { it.splitType == "LEGS" && it.isCompleted } }
    
    val volumeTrendList = remember(allWorkoutLogs) {
        val activeLogs = allWorkoutLogs
            .filter { it.setsLogJson.isNotEmpty() }
            .sortedBy { it.dateString }
            .takeLast(5)
            
        activeLogs.map { log ->
            val (_, weights, reps) = parseSetsLogJson(log.setsLogJson)
            var logVol = 0f
            weights.forEach { (ex, setMap) ->
                setMap.forEach { (setIdx, wt) ->
                    val r = reps[ex]?.get(setIdx) ?: 10
                    logVol += wt * r
                }
            }
            log.splitType.substring(0, Math.min(log.splitType.length, 3)) to logVol
        }
    }

    GlowGlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🛡️ HYPERTROPHY ENGINE",
                    fontSize = 10.sp,
                    color = GlowCyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Overall Gym Performance",
                    fontSize = 16.sp,
                    color = GlowSilverAccent,
                    fontWeight = FontWeight.Black
                )
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GlowCyanAccent.copy(alpha = 0.15f))
                    .border(0.5.dp, GlowCyanAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .clickable { onMoveToTab(3) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("LOG GYM", fontSize = 9.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text("COMPLETED", fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                Text(
                    text = "$totalWorkouts Sessions",
                    fontSize = 14.sp,
                    color = GlowSilverAccent,
                    fontWeight = FontWeight.Black
                )
            }

            Column(
                modifier = Modifier.weight(1.2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TOTAL VOLUME LIFTED", fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                Text(
                    text = if (totalVolumeLifted >= 1000f) "${String.format("%.1f", totalVolumeLifted / 1000f)} Tons" else "${totalVolumeLifted.toInt()} kg",
                    fontSize = 14.sp,
                    color = GlowCyanAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.weight(0.9f),
                horizontalAlignment = Alignment.End
            ) {
                Text("PEAK LIFT", fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                Text(
                    text = "${maxWeightLifted.toInt()} kg",
                    fontSize = 14.sp,
                    color = GlowBlueAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "SPLIT DISTRIBUTION BALANCER",
            fontSize = 9.sp,
            color = GlowMutedGrey,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(6.dp))

        val totalActiveSessions = (pushCount + pullCount + legsCount).toFloat()
        val pushPct = if (totalActiveSessions > 0f) pushCount / totalActiveSessions else 0.33f
        val pullPct = if (totalActiveSessions > 0f) pullCount / totalActiveSessions else 0.33f
        val legsPct = if (totalActiveSessions > 0f) legsCount / totalActiveSessions else 0.34f

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .weight(pushPct)
                    .fillMaxHeight()
                    .background(Color(0xFF005CFF))
            )
            Box(
                modifier = Modifier
                    .weight(pullPct)
                    .fillMaxHeight()
                    .background(Color(0xFF00F5FF))
            )
            Box(
                modifier = Modifier
                    .weight(legsPct)
                    .fillMaxHeight()
                    .background(Color(0xFF10B981))
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF005CFF)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PUSH: $pushCount", fontSize = 8.sp, color = GlowSilverAccent)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00F5FF)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PULL: $pullCount", fontSize = 8.sp, color = GlowSilverAccent)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("LEGS: $legsCount", fontSize = 8.sp, color = GlowSilverAccent)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PROGRESSIVE OVERLOAD TONNAGE TREND",
            fontSize = 9.sp,
            color = GlowMutedGrey,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                drawLine(
                    color = GlowDarkGrey.copy(alpha = 0.2f),
                    start = androidx.compose.ui.geometry.Offset(0f, height * 0.2f),
                    end = androidx.compose.ui.geometry.Offset(width, height * 0.2f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = GlowDarkGrey.copy(alpha = 0.2f),
                    start = androidx.compose.ui.geometry.Offset(0f, height * 0.6f),
                    end = androidx.compose.ui.geometry.Offset(width, height * 0.6f),
                    strokeWidth = 1f
                )

                val totalBars = 5
                val barSpacing = width / totalBars
                val barWidth = barSpacing * 0.45f

                for (i in 0 until totalBars) {
                    val hasRealData = i < volumeTrendList.size
                    val splitLabel = if (hasRealData) volumeTrendList[i].first else {
                        when (i) {
                            0 -> "PSH"
                            1 -> "PLL"
                            2 -> "LEG"
                            3 -> "PSH"
                            else -> "PLL"
                        }
                    }
                    val volumeG = if (hasRealData) volumeTrendList[i].second else {
                        500f + (i * 120f)
                    }

                    val maxScale = 2500f
                    val barHeight = (volumeG / maxScale).coerceIn(0.1f, 1.0f) * (height * 0.7f)
                    val x = barSpacing * i + (barSpacing - barWidth) / 2f
                    val y = height - barHeight - 12f

                    val cornerRadius = 4f
                    
                    if (hasRealData) {
                        drawRoundRect(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(GlowCyanAccent, GlowBlueAccent.copy(alpha = 0.5f))
                            ),
                            topLeft = androidx.compose.ui.geometry.Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
                        )
                    } else {
                        drawRoundRect(
                            color = GlowCyanAccent.copy(alpha = 0.2f),
                            topLeft = androidx.compose.ui.geometry.Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                            )
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val totalBars = 5
                for (i in 0 until totalBars) {
                    val hasRealData = i < volumeTrendList.size
                    val splitLabel = if (hasRealData) volumeTrendList[i].first else {
                        when (i) {
                            0 -> "PSH"
                            1 -> "PLL"
                            2 -> "LEG"
                            3 -> "PSH"
                            else -> "PLL"
                        }
                    }
                    val isTarget = !hasRealData
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = splitLabel.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTarget) GlowMutedGrey else GlowSilverAccent
                            )
                        }
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp, 6.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(GlowCyanAccent)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("LOGGED SESSIONS", fontSize = 7.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp, 6.dp)
                        .border(1.dp, GlowCyanAccent.copy(alpha = 0.4f), RoundedCornerShape(1.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("HYPERTROPHY TARGETS", fontSize = 7.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gymDiagnosisFeedback.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(GlowDarkGrey)
                    .border(0.5.dp, GlowCyanAccent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "🧬 SPORTS SCIENCE VERDICT",
                        fontSize = 8.sp,
                        color = GlowCyanAccent,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = gymDiagnosisFeedback,
                        fontSize = 11.sp,
                        color = GlowSilverAccent,
                        lineHeight = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (isGymDiagnosisLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GlowCyanAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            GlowButton(
                text = if (gymDiagnosisFeedback.isEmpty()) "Compute Gym Overload Diagnosis" else "Re-Diagnose Strength Profile",
                onClick = {
                    coroutineScope.launch {
                        onUpdateDiagnosis("", true)
                        try {
                            val activeWorkoutDataExplanation = if (volumeTrendList.isNotEmpty()) {
                                "Logged split volumes are: " + volumeTrendList.joinToString(", ") { "${it.first}: ${it.second.toInt()}kg tonnage" }
                            } else {
                                "Trainee is starting Day 1. No compound lifts logged yet."
                            }

                            val prompt = """
                                Conduct a premium sports-science review of the client's current strength overload trajectory.
                                Workouts Logged: $totalWorkouts completed. Peak Weight Lifted: ${maxWeightLifted}kg. Total minutes spent: $totalGymMinutes min.
                                $activeWorkoutDataExplanation
                                Offer an elite level, compact analysis (max 95 words) detailing hypertrophy efficiency. Point out whether the trainee is on trajectory for progressive overloading. Give exactly 2 concrete action steps. Be intense, scientific, and direct. No conversational filler. Use premium visuals.
                            """.trimIndent()
                            val report = GeminiService.askGlowUpCoach(prompt)
                            onUpdateDiagnosis(report, false)
                        } catch (e: Exception) {
                            onUpdateDiagnosis(
                                "COMPUTED HYPERTROPHY INSIGHT: Dynamic overload parameters are aligned. To accelerate compound myofibrillar density, elevate bench press and squat loading by 5% every 4 active days while keeping set duration tightly capped. Safeguard 155g of premium protein structures today.",
                                false
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Refresh,
                isSecondary = true
            )
        }
    }
}

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================

@Composable
fun DashboardScreen(
    viewModel: GlowViewModel,
    modifier: Modifier = Modifier,
    onMoveToTab: (Int) -> Unit
) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val totalGlowPoints by viewModel.glowPointsFlow.collectAsState()
    val streak by viewModel.currentStreakFlow.collectAsState()

    val tasks by viewModel.tasksFlow.collectAsState()
    val waterLog by viewModel.waterLogFlow.collectAsState()
    val sleepLog by viewModel.sleepLogFlow.collectAsState()
    val trackerLog by viewModel.trackerLogFlow.collectAsState()

    val allWorkoutLogs by viewModel.allWorkoutLogsFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var gymDiagnosisFeedback by remember { mutableStateOf("") }
    var isGymDiagnosisLoading by remember { mutableStateOf(false) }

    // Calculate daily completion stats
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val progressPercent = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) * 100 else 0f

    val skinClarity = trackerLog?.skinClarityScore ?: 70
    val hairGrowth = trackerLog?.hairGrowthScore ?: 55
    val sleepScore = sleepLog?.qualityScore ?: 72
    val waterIntakeMl = waterLog?.amountMl ?: 0

    // Dynamic Glow Score base formula
    val calculatedGlowScore = ((progressPercent + skinClarity + hairGrowth + sleepScore) / 4).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBlack)
            .padding(horizontal = 16.dp)
            .testTag("dashboard-screen-root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Futuristic Brand Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GLOWUPX 2026",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = GlowSilverAccent,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(color = GlowBlueAccent.copy(alpha = 0.8f), blurRadius = 15f)
                        )
                    )
                    Text(
                        text = "Elite Decennial Operating System",
                        fontSize = 11.sp,
                        color = GlowMutedGrey,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Points Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(GlowDarkGrey)
                        .border(1.dp, GlowCyanAccent.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Points", tint = GlowCyanAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$totalGlowPoints GP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GlowSilverAccent)
                    }
                }
            }
        }

        // Daily Motivational Engine Card
        item {
            GlowGlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(GlowBlueAccent.copy(alpha = 0.15f))
                            .border(1.dp, GlowBlueAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Quote", tint = GlowBlueAccent)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "DIES $selectedDay — THE HARDEST DRILL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlowCyanAccent
                        )
                        Text(
                            text = "\"Without Sunscreen SPF 50, all tan removal and active skin whitening treatments will fail entirely. Defend your progress.\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = GlowSilverAccent,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // DAILY NON-NEGOTIABLES & WEEKLY EXTRAS
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DAILY LIFE-CONSTITUTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowCyanAccent,
                    letterSpacing = 1.sp
                )
                
                GlowGlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "DAILY NON-NEGOTIABLES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlowSilverAccent
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GlowCyanAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("9 HABITS", fontSize = 8.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        HorizontalDivider(color = GlowDarkGrey, thickness = 0.5.dp)
                        
                        // 3x3 Grid of Non-Negotiables
                        val nonNegotiables = listOf(
                            "SPF 50 Protection" to Icons.Default.Check,
                            "Minox Morn + Night" to Icons.Default.Check,
                            "3L-3.5L Water Intake" to Icons.Default.Check,
                            "160g Clean Protein" to Icons.Default.Check,
                            "8h Deep Sleep" to Icons.Default.Check,
                            "Mewing All-Day" to Icons.Default.Check,
                            "Sunlight Morning" to Icons.Default.Check,
                            "Zero Added Sugar" to Icons.Default.Check,
                            "No Outside Food" to Icons.Default.Check
                        )
                        
                        // Render in small grid
                        nonNegotiables.chunked(3).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowItems.forEach { (text, icon) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GlowDarkGrey.copy(alpha = 0.4f))
                                            .border(0.5.dp, GlowCyanAccent.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                            .padding(vertical = 8.dp, horizontal = 4.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = GlowCyanAccent,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = text,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GlowSilverAccent,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        // Weekly Extras Highlight
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GlowBlueAccent.copy(alpha = 0.08f))
                                .border(0.5.dp, GlowBlueAccent.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                "WEEKLY ELITE EXTRAS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlowBlueAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "• SUNDAY: 24-Hour Detox Fast + Full Rest\n" +
                                "• SUNDAY: Deep Hair Mask (Egg + Honey + Olive Oil)\n" +
                                "• PROGRESS PHOTOS: Shoot comparatives every 4 weeks\n" +
                                "• DAILY HABITS: Derma roller once/week + 30m movement breaks",
                                fontSize = 9.sp,
                                color = GlowMutedGrey,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Core Ring Progression Banner
        item {
            GlowGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circle Progress Visualizer
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(110.dp)) {
                            // Back ring
                            drawCircle(
                                color = GlowDarkGrey,
                                radius = size.minDimension / 2,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12.dp.toPx())
                            )
                            // Progress arc
                            drawArc(
                                color = GlowBlueAccent,
                                startAngle = -90f,
                                sweepAngle = (progressPercent / 100f) * 360f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 12.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${progressPercent.toInt()}%",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = GlowSilverAccent
                            )
                            Text(
                                text = "COMPLETED",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlowBlueAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Key Stats Breakdown
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "METRICS SNAPSHOT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlowMutedGrey
                        )
                        StatLine(label = "GLOW SCORE", value = "$calculatedGlowScore / 100", accent = GlowCyanAccent)
                        StatLine(label = "HAIR HEALTH", value = "$hairGrowth%", accent = GlowSilverAccent)
                        StatLine(label = "SKIN CLARITY", value = "$skinClarity%", accent = GlowBlueAccent)
                        StatLine(label = "WATER / SLEEP", value = "${waterIntakeMl}ml / ${sleepLog?.hours ?: 0f}h", accent = GlowMutedGrey)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Streak", tint = GlowCyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streak Day Streak Active",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlowCyanAccent
                            )
                        }
                    }
                }
            }
        }

        // GYM ANALYSIS OVERALL PERFORMANCE Card
        item {
            MainDashboardGymPerformanceAnalysis(
                allWorkoutLogs = allWorkoutLogs,
                viewModel = viewModel,
                coroutineScope = coroutineScope,
                gymDiagnosisFeedback = gymDiagnosisFeedback,
                isGymDiagnosisLoading = isGymDiagnosisLoading,
                onUpdateDiagnosis = { feedback, loading ->
                    gymDiagnosisFeedback = feedback
                    isGymDiagnosisLoading = loading
                },
                onMoveToTab = onMoveToTab
            )
        }

        // Water Balance radial gauge widget
        item {
            WaterBalanceWidget(
                waterIntakeMl = waterIntakeMl,
                onQuickAdd = { amount -> viewModel.addWater(amount) }
            )
        }

        // Feature Navigation Quick Access
        item {
            Column {
                Text(
                    text = "RAPID CONTROL MATRIX",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowMutedGrey,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickAccessButton(
                        label = "Daily Checklist",
                        sub = "$completedTasks/$totalTasks items",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f),
                        onClick = { onMoveToTab(1) } // Checklist
                    )
                    QuickAccessButton(
                        label = "Coaching Assistant",
                        sub = "AI Answers Rest",
                        icon = Icons.Default.Build,
                        modifier = Modifier.weight(1f),
                        onClick = { onMoveToTab(4) } // AI Chat
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickAccessButton(
                        label = "Workout & Diet",
                        sub = "Logs & Fuel",
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.weight(1f),
                        onClick = { onMoveToTab(3) } // Workout diet
                    )
                    QuickAccessButton(
                        label = "Progress Hub",
                        sub = "Trackers & Slide",
                        icon = Icons.Default.AccountBox,
                        modifier = Modifier.weight(1f),
                        onClick = { onMoveToTab(2) } // Trackers
                    )
                }
            }
        }

        // Daily Schedule Highlights Panel
        item {
            GlowGlassCard {
                Text(
                    text = "DIES $selectedDay PROTOCOLS SUMMARY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowBlueAccent,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (tasks.isEmpty()) {
                    Text(
                        text = "Loading today's protocols...",
                        color = GlowMutedGrey,
                        fontSize = 13.sp
                    )
                } else {
                    val sortedDashboardTasks = remember(tasks) {
                        tasks.sortedBy { task ->
                            val title = task.title
                            var weight = 100000
                            if (title.contains("[CHALLENGE]")) {
                                weight = 99999
                            } else {
                                val regex = "\\[(\\d{2}):(\\d{2})\\s*(AM|PM)\\]".toRegex()
                                val match = regex.find(title)
                                if (match != null) {
                                    var hours = match.groupValues[1].toIntOrNull() ?: 0
                                    val minutes = match.groupValues[2].toIntOrNull() ?: 0
                                    val amPm = match.groupValues[3]
                                    if (amPm == "PM" && hours < 12) hours += 12
                                    if (amPm == "AM" && hours == 12) hours = 0
                                    weight = hours * 60 + minutes
                                }
                            }
                            weight
                        }
                    }
                    sortedDashboardTasks.take(4).forEach { task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (task.isCompleted) GlowCyanAccent else GlowMutedGrey)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (task.isCompleted) GlowMutedGrey else GlowSilverAccent,
                                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = task.product,
                                    fontSize = 10.sp,
                                    color = GlowMutedGrey,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (tasks.size > 4) {
                        Text(
                            text = "+ ${tasks.size - 4} more elite items on your Daily Checklist",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GlowCyanAccent,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable { onMoveToTab(1) }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun StatLine(label: String, value: String, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 11.sp, color = accent, fontWeight = FontWeight.Black)
    }
}

@Composable
fun QuickAccessButton(
    label: String,
    sub: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GlowDarkCard)
            .border(1.dp, GlowBlueAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(GlowBlueAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = GlowBlueAccent, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GlowSilverAccent)
                Text(text = sub, fontSize = 10.sp, color = GlowMutedGrey)
            }
        }
    }
}

// ==========================================
// 2. DAILY CHECKLIST & 30-DAY CALENDAR SCREEN
// ==========================================

@Composable
fun GlowCalendarDialog(
    initialDateStr: String,
    onDismissRequest: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    var calendar by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                time = DateUtils.parseDate(initialDateStr) ?: Date()
            }
        )
    }

    val currentMonthYear = remember(calendar) {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        monthYearFormat.format(calendar.time)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = GlowDarkCard,
        titleContentColor = GlowSilverAccent,
        textContentColor = GlowMutedGrey,
        modifier = Modifier.border(1.dp, GlowBlueAccent.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        time = calendar.time
                        add(Calendar.MONTH, -1)
                    }
                    calendar = newCal
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Prev Month",
                        tint = GlowCyanAccent
                    )
                }

                Text(
                    text = currentMonthYear.uppercase(Locale.US),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = GlowSilverAccent
                )

                IconButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        time = calendar.time
                        add(Calendar.MONTH, 1)
                    }
                    calendar = newCal
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next Month",
                        tint = GlowCyanAccent
                    )
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val abbreviations = listOf("S", "M", "T", "W", "T", "F", "S")
                    abbreviations.forEach { abbr ->
                        Text(
                            text = abbr,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlowMutedGrey
                        )
                    }
                }

                HorizontalDivider(color = GlowDarkGrey, thickness = 0.5.dp)

                val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val tempCal = Calendar.getInstance().apply {
                    time = calendar.time
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val startDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)

                var dayValue = 1
                for (row in 0 until 6) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (col in 1..7) {
                            val cellIndex = row * 7 + col
                            val dayNumberToDraw = if (cellIndex >= startDayOfWeek && dayValue <= daysInMonth) {
                                dayValue++
                            } else null

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (dayNumberToDraw != null && DateUtils.formatDate(
                                                Calendar.getInstance().apply {
                                                    time = calendar.time
                                                    set(Calendar.DAY_OF_MONTH, dayNumberToDraw)
                                                }.time
                                            ) == initialDateStr
                                        ) GlowBlueAccent else Color.Transparent
                                    )
                                    .clickable(enabled = dayNumberToDraw != null) {
                                        dayNumberToDraw?.let { dayNum ->
                                            val selectedCal = Calendar.getInstance().apply {
                                                time = calendar.time
                                                set(Calendar.DAY_OF_MONTH, dayNum)
                                            }
                                            onDateSelected(DateUtils.formatDate(selectedCal.time))
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayNumberToDraw != null) {
                                    val isSelected = DateUtils.formatDate(
                                        Calendar.getInstance().apply {
                                            time = calendar.time
                                            set(Calendar.DAY_OF_MONTH, dayNumberToDraw)
                                        }.time
                                    ) == initialDateStr

                                    Text(
                                        text = "$dayNumberToDraw",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) GlowBlack else GlowSilverAccent
                                    )
                                }
                            }
                        }
                    }
                    if (dayValue > daysInMonth) break
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("CLOSE", color = GlowCyanAccent, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ChecklistScreen(
    viewModel: GlowViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val selectedDateStr by viewModel.selectedDateString.collectAsState()
    val tasks by viewModel.tasksFlow.collectAsState()

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val progressPercent = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) * 100 else 0f

    var activeCategoryFilter by remember { mutableStateOf("ALL") }
    var showCalendarDialog by remember { mutableStateOf(false) }

    val filteredTasks = when (activeCategoryFilter) {
        "ALL" -> tasks
        "SKIN" -> tasks.filter { it.category == "SKIN" }
        "HAIR" -> tasks.filter { it.category == "HAIR" }
        "BODY" -> tasks.filter { it.category in listOf("WORKOUT", "DIET") }
        else -> tasks.filter { it.category == activeCategoryFilter }
    }.sortedBy { task ->
        val title = task.title
        var weight = 100000
        if (title.contains("[CHALLENGE]")) {
            weight = 99999
        } else {
            val regex = "\\[(\\d{2}):(\\d{2})\\s*(AM|PM)\\]".toRegex()
            val match = regex.find(title)
            if (match != null) {
                var hours = match.groupValues[1].toIntOrNull() ?: 0
                val minutes = match.groupValues[2].toIntOrNull() ?: 0
                val amPm = match.groupValues[3]
                if (amPm == "PM" && hours < 12) hours += 12
                if (amPm == "AM" && hours == 12) hours = 0
                weight = hours * 60 + minutes
            }
        }
        weight
    }

    // Centered surrounding 11 calendar days list
    val ribbonDates = remember(selectedDateStr) {
        val list = mutableListOf<String>()
        for (i in -5..5) {
            list.add(DateUtils.addDays(selectedDateStr, i))
        }
        list
    }

    if (showCalendarDialog) {
        GlowCalendarDialog(
            initialDateStr = selectedDateStr,
            onDismissRequest = { showCalendarDialog = false },
            onDateSelected = {
                viewModel.selectDate(it)
                showCalendarDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBlack)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Lifelong Horizon Banner Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LIFELONG TRANSFORMATION CALENDAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowMutedGrey
                )
                Text(
                    text = DateUtils.formatReadable(selectedDateStr).uppercase(Locale.US),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = GlowCyanAccent
                )
            }

            IconButton(
                onClick = { showCalendarDialog = true },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GlowDarkCard)
                    .border(0.5.dp, GlowBlueAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Pick Custom Date",
                    tint = GlowBlueAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic 11-Day Horizontal Navigation Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day stepper back
            IconButton(
                onClick = { viewModel.selectPreviousDay() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GlowDarkCard)
                    .size(32.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Prev Day", tint = GlowSilverAccent, modifier = Modifier.size(18.dp))
            }

            // Ribbon List of dates
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(ribbonDates) { dateString ->
                    val isSelected = dateString == selectedDateStr
                    val dayNum = DateUtils.getDayOfMonthNumber(dateString)
                    val dayAbbr = DateUtils.getDayOfWeekAbbreviation(dateString)

                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) GlowBlueAccent else GlowDarkCard)
                            .border(
                                1.dp,
                                if (isSelected) GlowCyanAccent else GlowBlueAccent.copy(alpha = 0.12f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectDate(dateString) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text(
                                text = dayAbbr,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GlowBlack else GlowMutedGrey
                            )
                            Text(
                                text = dayNum,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) GlowBlack else GlowSilverAccent
                            )
                        }
                    }
                }
            }

            // Day stepper next
            IconButton(
                onClick = { viewModel.selectNextDay() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GlowDarkCard)
                    .size(32.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Day", tint = GlowSilverAccent, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Focus Tag for the current dynamic phase
        val phaseText = when (selectedDay) {
            in 1..7 -> "WEEKLY PHASE 1: SYSTEMIC DETOX, INITIAL HYDRATION & SCALP RESET"
            in 8..14 -> "WEEKLY PHASE 2: ACTIVE BRIGHTENING, TAN REMOVAL & TEMPLE ACTIVATION"
            in 15..21 -> "WEEKLY PHASE 3: METABOLIC ABSORPTION & EXPERT CELLULAR STIMULATION"
            else -> "WEEKLY PHASE 4: MAXIMUM GLOW CONVERGENCE, RECOVERY & COMPRESSION"
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(GlowDarkCard)
                .border(0.5.dp, GlowCyanAccent.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Text(
                text = "$phaseText (DAY $selectedDay)",
                fontSize = 10.sp,
                color = GlowCyanAccent,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                letterSpacing = 0.2.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress breakdown header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DAILY HABITS CHECKPOINTS",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = GlowSilverAccent
                )
                Text(
                    text = "$completedTasks of $totalTasks protocols recorded today",
                    fontSize = 11.sp,
                    color = GlowMutedGrey
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlowDarkGrey)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${progressPercent.toInt()}% COMPLETE",
                    fontSize = 10.sp,
                    color = GlowCyanAccent,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filters = listOf("ALL", "SKIN", "HAIR", "BODY", "SUPPLEMENTS")
            filters.forEach { filter ->
                val isActive = activeCategoryFilter == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) GlowDarkGrey else Color.Transparent)
                        .border(
                            0.5.dp,
                            if (isActive) GlowBlueAccent else GlowMutedGrey.copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeCategoryFilter = filter }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) GlowBlueAccent else GlowMutedGrey
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tasks checklist list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No protocols match the selected criteria.",
                            color = GlowMutedGrey,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskRowItem(task = task, onCheckedChange = { viewModel.toggleTask(task) })
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun TaskRowItem(
    task: TaskItemEntity,
    onCheckedChange: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val borderColor = if (task.isCompleted) GlowCyanAccent.copy(alpha = 0.3f) else GlowBlueAccent.copy(alpha = 0.15f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlowDarkCard)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // High fidelity custom checkbox with a dedicated 48dp touch target
        Box(
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .size(40.dp)
                .clickable { onCheckedChange() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (task.isCompleted) GlowCyanAccent else GlowDarkGrey)
                    .border(
                        1.dp,
                        if (task.isCompleted) Color.Transparent else GlowBlueAccent.copy(alpha = 0.6f),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = GlowBlack,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Habit details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) GlowMutedGrey else GlowSilverAccent,
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Time of day tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(GlowDarkGrey)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.timeOfDay,
                        fontSize = 7.sp,
                        color = if (task.timeOfDay == "MORNING") GlowBlueAccent else GlowCyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (task.product.isNotBlank()) {
                        Text(
                            text = "PRODUCT / INGREDIENT:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = GlowCyanAccent,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = task.product,
                            fontSize = 11.sp,
                            color = GlowSilverAccent,
                            lineHeight = 15.sp
                        )
                    }
                    if (task.purpose.isNotBlank()) {
                        Text(
                            text = "PURPOSE & PROTOCOL IMPACT:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = GlowBlueAccent,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = task.purpose,
                            fontSize = 11.sp,
                            color = GlowMutedGrey,
                            lineHeight = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "TAP CARD TO COLLAPSE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlowMutedGrey.copy(alpha = 0.6f),
                        letterSpacing = 0.3.sp
                    )
                }
            } else {
                Text(
                    text = "${task.product} — ${task.purpose}",
                    fontSize = 11.sp,
                    color = GlowMutedGrey,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Award glow points
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "+${task.glowPoints} GP",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (task.isCompleted) GlowMutedGrey else GlowCyanAccent
            )
            Text(
                text = task.category,
                fontSize = 8.sp,
                color = GlowMutedGrey,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==========================================
// 3. DIET & WORKOUT PLANNER SCREEN
// ==========================================

// ==========================================
// 3. DIET & WORKOUT PLANNER SCREEN
// ==========================================

private data class LoggedExSet(
    val maxWeight: Float,
    val volume: Float,
    val weights: Map<Int, Float>,
    val reps: Map<Int, Int>
)

private fun parseSetsLogJson(jsonStr: String?): Triple<Map<String, Set<Int>>, Map<String, Map<Int, Float>>, Map<String, Map<Int, Int>>> {
    val completed = mutableMapOf<String, Set<Int>>()
    val weights = mutableMapOf<String, Map<Int, Float>>()
    val reps = mutableMapOf<String, Map<Int, Int>>()
    if (jsonStr.isNullOrEmpty()) return Triple(completed, weights, reps)
    try {
        val root = org.json.JSONObject(jsonStr)
        val exercisesObj = root.optJSONObject("exercises") ?: return Triple(completed, weights, reps)
        val keys = exercisesObj.keys()
        while (keys.hasNext()) {
            val exName = keys.next()
            val arr = exercisesObj.optJSONArray(exName) ?: continue
            val completedSets = mutableSetOf<Int>()
            val exWeights = mutableMapOf<Int, Float>()
            val exReps = mutableMapOf<Int, Int>()
            for (i in 0 until arr.length()) {
                val setObj = arr.optJSONObject(i) ?: continue
                val setNum = setObj.optInt("setNum")
                if (setNum > 0) {
                    val done = setObj.optBoolean("completed", false)
                    if (done) completedSets.add(setNum)
                    val w = setObj.optDouble("weight", 0.0).toFloat()
                    val r = setObj.optInt("reps", 0)
                    exWeights[setNum] = w
                    exReps[setNum] = r
                }
            }
            completed[exName] = completedSets
            weights[exName] = exWeights
            reps[exName] = exReps
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return Triple(completed, weights, reps)
}

private fun serializeSetsLogJson(
    completed: Map<String, Set<Int>>,
    weights: Map<String, Map<Int, Float>>,
    reps: Map<String, Map<Int, Int>>
): String {
    try {
        val root = org.json.JSONObject()
        val exercisesObj = org.json.JSONObject()
        val allExNames = (completed.keys + weights.keys + reps.keys).toSet()
        for (exName in allExNames) {
            val arr = org.json.JSONArray()
            val exWeights = weights[exName] ?: emptyMap()
            val exReps = reps[exName] ?: emptyMap()
            val compSets = completed[exName] ?: emptySet()
            for (setNum in 1..8) {
                if (exWeights.containsKey(setNum) || exReps.containsKey(setNum) || compSets.contains(setNum)) {
                    val setObj = org.json.JSONObject()
                    setObj.put("setNum", setNum)
                    setObj.put("completed", compSets.contains(setNum))
                    setObj.put("weight", (exWeights[setNum] ?: 0f).toDouble())
                    setObj.put("reps", exReps[setNum] ?: 0)
                    arr.put(setObj)
                }
            }
            exercisesObj.put(exName, arr)
        }
        root.put("exercises", exercisesObj)
        return root.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

private fun computeProgressiveOverloadSummary(exName: String, allLogs: List<WorkoutLogEntity>): String {
    val history = mutableListOf<Float>()
    for (log in allLogs) {
        val (_, weights, _) = parseSetsLogJson(log.setsLogJson)
        val exWeights = weights[exName]
        if (exWeights != null && exWeights.isNotEmpty()) {
            val maxW = exWeights.values.maxOrNull() ?: 0f
            if (maxW > 0) {
                history.add(maxW)
            }
        }
    }
    if (history.size >= 2) {
        val currentMax = history.last()
        val prevMax = history[history.size - 2]
        if (currentMax > prevMax) {
            return "⚡ OVERLOADED +${currentMax - prevMax}kg!"
        } else if (currentMax == prevMax) {
            return "🎯 MATCHED BASELINE"
        }
    }
    return ""
}

@Composable
fun GymPPLScreen(
    viewModel: GlowViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDateStr by viewModel.selectedDateString.collectAsState()
    val workoutLog by viewModel.workoutLogFlow.collectAsState()
    val allWorkoutLogs by viewModel.allWorkoutLogsFlow.collectAsState()

    var activeViewDay by remember { mutableStateOf("Monday") }

    // Synchronize selected day of week on startup
    LaunchedEffect(selectedDateStr) {
        activeViewDay = DateUtils.getDayOfWeeksName(selectedDateStr)
    }

    // Interactive exercises completed checklist helper states (local per day session)
    var checkedExercises by remember { mutableStateOf(setOf<String>()) }
    var completedSetsMap by remember { mutableStateOf(mapOf<String, Set<Int>>()) }
    var completedWeightMap by remember { mutableStateOf(mapOf<String, Map<Int, Float>>()) }
    var completedRepsMap by remember { mutableStateOf(mapOf<String, Map<Int, Int>>()) }

    // Real-time Rest Interval timer state
    var timerSeconds by remember { mutableStateOf(90) }
    var timerRunning by remember { mutableStateOf(false) }
    var timerPresetSelected by remember { mutableStateOf(90) }

    var editingSetInfo by remember { mutableStateOf<Triple<String, Int, Pair<Float, Int>>?>(null) }

    var gymCoachFeedback by remember { mutableStateOf("") }
    var isGymCoachLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(workoutLog) {
        if (workoutLog != null) {
            val (comp, weights, reps) = parseSetsLogJson(workoutLog?.setsLogJson)
            completedSetsMap = comp
            completedWeightMap = weights
            completedRepsMap = reps

            val loggedExs = workoutLog?.exercisesLogged ?: ""
            checkedExercises = if (loggedExs.isNotEmpty()) {
                loggedExs.split(", ").map { it.trim() }.toSet()
            } else {
                emptySet()
            }
        } else {
            completedSetsMap = emptyMap()
            completedWeightMap = emptyMap()
            completedRepsMap = emptyMap()
            checkedExercises = emptySet()
        }
    }

    val saveChanges: (Map<String, Set<Int>>, Map<String, Map<Int, Float>>, Map<String, Map<Int, Int>>, Set<String>) -> Unit = { comp, weights, reps, checked ->
        val json = serializeSetsLogJson(comp, weights, reps)
        val maxW = weights.values.flatMap { it.values }.maxOrNull() ?: 0f
        viewModel.updateWorkout(
            split = workoutLog?.splitType ?: when(activeViewDay) {
                "Sunday" -> "REST"
                "Monday", "Thursday" -> "PUSH"
                "Tuesday", "Friday" -> "PULL"
                else -> "LEGS"
            },
            minutes = workoutLog?.durationMinutes ?: 45,
            exercises = checked.joinToString(", "),
            weight = if (maxW > (workoutLog?.maxWeightKg ?: 0f)) maxW else (workoutLog?.maxWeightKg ?: 0f),
            completed = workoutLog?.isCompleted ?: false,
            setsJson = json
        )
    }

    fun getPreviousSetPerf(exName: String, setNum: Int): Pair<Float, Int> {
        val sortedList = allWorkoutLogs.filter { it.dateString < selectedDateStr && it.setsLogJson.isNotEmpty() }
        for (log in sortedList.reversed()) {
            val (_, weights, reps) = parseSetsLogJson(log.setsLogJson)
            val exWeights = weights[exName]
            val exReps = reps[exName]
            if (exWeights != null && exWeights.containsKey(setNum)) {
                return (exWeights[setNum] ?: 0f) to (exReps?.get(setNum) ?: 0)
            }
        }
        return 20f to 10
    }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds -= 1
            }
            timerRunning = false
            // Save updates
            saveChanges(completedSetsMap, completedWeightMap, completedRepsMap, checkedExercises)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Core Header
        item {
            Column {
                Text(text = "AESTHETICS GYM SPLIT (PPL)", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Hypertrophy Operating System", fontSize = 24.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Text(text = "Aesthetics, posture, jawline looksmaxing split", fontSize = 12.sp, color = GlowMutedGrey)
            }
        }

        // 1. Mon - Sun Horizontal Selector Strip
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                items(days) { day ->
                    val isToday = DateUtils.getDayOfWeeksName(selectedDateStr) == day
                    val isSelected = activeViewDay == day

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) GlowBlueAccent 
                                else if (isToday) GlowDarkGrey.copy(alpha = 0.6f) 
                                else GlowDarkCard
                            )
                            .border(
                                1.dp,
                                if (isSelected) GlowBlueAccent 
                                else if (isToday) GlowCyanAccent.copy(alpha = 0.4f) 
                                else GlowDarkGrey,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { activeViewDay = day }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day.take(3).uppercase(), 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Black, 
                                color = if (isSelected) GlowBlack else GlowSilverAccent
                            )
                            if (isToday) {
                                Text(text = "TODAY", fontSize = 8.sp, color = if (isSelected) GlowBlack else GlowCyanAccent, fontWeight = FontWeight.Bold)
                            } else {
                                Text(
                                    text = when(day) {
                                        "Sunday" -> "REST"
                                        "Monday", "Thursday" -> "PUSH"
                                        "Tuesday", "Friday" -> "PULL"
                                        else -> "LEGS"
                                    }, 
                                    fontSize = 8.sp, 
                                    color = if (isSelected) GlowBlack.copy(alpha = 0.7f) else GlowMutedGrey
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Progressive Overload Analytics Dashboard Card
        item {
            GlowGlassCard {
                Column {
                    Text(text = "PROGRESSIVE OVERLOAD HUB", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                    Text(text = "Strength Overload Analytics", fontSize = 18.sp, color = GlowSilverAccent, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Real-time progressive overload calculations. Increasing weight or total sets volume triggers hypertrophic muscle repair.",
                        fontSize = 11.sp, color = GlowMutedGrey
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val allLogsWithSets = remember(allWorkoutLogs) {
                        allWorkoutLogs.filter { it.setsLogJson.isNotEmpty() }
                    }
                    if (allLogsWithSets.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GlowDarkGrey.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No sets logged yet. Complete sets in the tracker below with weight & reps to begin progressive overload analysis!",
                                fontSize = 11.sp,
                                color = GlowMutedGrey,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Dashboard Tab Selectors
                        var selectedHubTab by remember { mutableStateOf(0) } // 0: History Log, 1: Period Analysis, 2: Overload Chart
                        var selectedPeriodType by remember { mutableStateOf("Weekly") } // "Weekly", "Monthly", "Yearly"
                        var chartMetricType by remember { mutableStateOf("Weight") } // "Weight", "Volume"
                        var showSimulator by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val tabs = listOf("LOG HISTORY", "PERIOD TOTALS", "VISUAL CHART")
                            tabs.forEachIndexed { idx, tabTitle ->
                                val isActive = selectedHubTab == idx
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isActive) GlowCyanAccent.copy(alpha = 0.15f) else GlowBlack)
                                        .border(0.5.dp, if (isActive) GlowCyanAccent else GlowDarkGrey, RoundedCornerShape(8.dp))
                                        .clickable { selectedHubTab = idx }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tabTitle,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) GlowCyanAccent else GlowMutedGrey
                                    )
                                }
                            }
                        }

                        // Exercise Aggregator List
                        val allLoggedExercises = remember(allLogsWithSets) {
                            val setEx = mutableSetOf<String>()
                            allLogsWithSets.forEach { log ->
                                val (_, weights, _) = parseSetsLogJson(log.setsLogJson)
                                setEx.addAll(weights.keys)
                            }
                            val sorted = setEx.toList().sorted()
                            if (sorted.isNotEmpty()) {
                                listOf("⚡ TOTAL COMBINED") + sorted
                            } else {
                                sorted
                            }
                        }
                        
                        var selectedAnalyticsEx by remember { mutableStateOf("") }
                        if (selectedAnalyticsEx.isEmpty() && allLoggedExercises.isNotEmpty()) {
                            selectedAnalyticsEx = allLoggedExercises.first()
                        }
                        
                        if (allLoggedExercises.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "SELECT EXERCISE SOURCE FOR MINING:",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GlowSilverAccent,
                                    letterSpacing = 0.5.sp
                                )
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(allLoggedExercises) { exName ->
                                        val isSelected = selectedAnalyticsEx == exName
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSelected) GlowCyanAccent.copy(alpha = 0.2f) else GlowDarkGrey)
                                                .border(0.5.dp, if (isSelected) GlowCyanAccent else GlowDarkGrey, RoundedCornerShape(6.dp))
                                                .clickable { selectedAnalyticsEx = exName }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = exName,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) GlowCyanAccent else GlowSilverAccent
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // HYPERTROPHY SIMULATOR TOGGLE
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GlowBlueAccent.copy(alpha = 0.08f))
                                    .border(0.5.dp, GlowBlueAccent.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
                                    .clickable { showSimulator = !showSimulator }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🧠", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Aesthetics Overload Simulator & 1RM Calculator",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GlowBlueAccent
                                        )
                                    }
                                    Text(
                                        if (showSimulator) "COLLAPSE ▲" else "EXPAND TOOL ▼",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GlowMutedGrey
                                    )
                                }
                            }

                            if (showSimulator) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GlowBlack.copy(alpha = 0.4f))
                                        .border(0.5.dp, GlowDarkGrey, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            "COGNITIVE LIFT PLANNING ENGINE",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = GlowSilverAccent
                                        )
                                        Text(
                                            "Calculate your theoretical mechanical limits to forecast the next progressive stimulus:",
                                            fontSize = 9.sp,
                                            color = GlowMutedGrey
                                        )
                                        
                                        var simWeight by remember { mutableStateOf("60") }
                                        var simReps by remember { mutableStateOf("10") }
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Current Weight (kg)", fontSize = 8.sp, color = GlowSilverAccent)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                BasicTextField(
                                                    value = simWeight,
                                                    onValueChange = { simWeight = it },
                                                    textStyle = TextStyle(color = GlowSilverAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(GlowDarkGrey, RoundedCornerShape(4.dp))
                                                        .padding(6.dp)
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Completed Reps", fontSize = 8.sp, color = GlowSilverAccent)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                BasicTextField(
                                                    value = simReps,
                                                    onValueChange = { simReps = it },
                                                    textStyle = TextStyle(color = GlowSilverAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(GlowDarkGrey, RoundedCornerShape(4.dp))
                                                        .padding(6.dp)
                                                )
                                            }
                                        }
                                        
                                        val parseW = simWeight.toFloatOrNull() ?: 0f
                                        val parseR = simReps.toIntOrNull() ?: 0
                                        
                                        if (parseW > 0 && parseR > 0) {
                                            val oneRepMax = parseW * (1f + (parseR / 30f))
                                            val volume = parseW * parseR
                                            val nextWeightTarget = parseW + 2.5f
                                            val nextRepsTarget = parseR + 1
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .background(GlowDarkCard, RoundedCornerShape(6.dp))
                                                        .padding(8.dp)
                                                ) {
                                                    Column {
                                                        Text("EST. 1-REP MAX", fontSize = 7.sp, color = GlowMutedGrey)
                                                        Text("${String.format("%.1f", oneRepMax)} kg", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Black)
                                                    }
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .background(GlowDarkCard, RoundedCornerShape(6.dp))
                                                        .padding(8.dp)
                                                ) {
                                                    Column {
                                                        Text("TOTAL VOLUME", fontSize = 7.sp, color = GlowMutedGrey)
                                                        Text("${volume.toInt()} kg", fontSize = 11.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                                                    }
                                                }
                                            }
                                            
                                            // Hypotrophic stimulus prescription
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(GlowBlueAccent.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                                    .padding(6.dp)
                                            ) {
                                                Text(
                                                    text = "🎯 NEXT SESSION GOAL (CHOOSE ONE TO FORCE HYPERTROPHY):\n" +
                                                           "• Force overload with heavier sets: $nextWeightTarget kg for $parseR reps\n" +
                                                           "• Force overload with volume sets: $parseW kg for $nextRepsTarget reps (+${(parseW).toInt()} kg volume increase!)",
                                                    fontSize = 8.sp,
                                                    color = GlowBlueAccent,
                                                    lineHeight = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            val exHistory = remember(allLogsWithSets, selectedAnalyticsEx) {
                                val list = mutableListOf<Triple<String, Float, Float>>()
                                allLogsWithSets.forEach { log ->
                                    val (_, weights, reps) = parseSetsLogJson(log.setsLogJson)
                                    if (selectedAnalyticsEx == "⚡ TOTAL COMBINED") {
                                        var maxW = 0f
                                        var volume = 0f
                                        weights.forEach { (exName, exWeights) ->
                                            val exReps = reps[exName]
                                            exWeights.forEach { (setNum, w) ->
                                                val r = exReps?.get(setNum) ?: 0
                                                maxW = maxOf(maxW, w)
                                                volume += w * r
                                            }
                                        }
                                        if (volume > 0) {
                                            list.add(Triple(log.dateString, maxW, volume))
                                        }
                                    } else {
                                        val exWeights = weights[selectedAnalyticsEx]
                                        val exReps = reps[selectedAnalyticsEx]
                                        if (exWeights != null && exWeights.isNotEmpty()) {
                                            val maxW = exWeights.values.maxOrNull() ?: 0f
                                            val volume = exWeights.keys.sumOf { s -> 
                                                ((exWeights[s] ?: 0f) * (exReps?.get(s) ?: 0)).toDouble()
                                            }.toFloat()
                                            if (maxW > 0) {
                                                list.add(Triple(log.dateString, maxW, volume))
                                            }
                                        }
                                    }
                                }
                                list
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GlowCyanAccent.copy(alpha = 0.05f))
                                    .border(0.5.dp, GlowCyanAccent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🤖", fontSize = 13.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "GLOWUPX AI LIFT COMPLIANCE COACH",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = GlowCyanAccent,
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        if (isGymCoachLoading) {
                                            CircularProgressIndicator(
                                                color = GlowCyanAccent,
                                                modifier = Modifier.size(12.dp),
                                                strokeWidth = 1.5.dp
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(GlowCyanAccent.copy(alpha = 0.15f))
                                                    .clickable {
                                                        coroutineScope.launch {
                                                            isGymCoachLoading = true
                                                            gymCoachFeedback = ""
                                                            val maxLift = exHistory.map { it.second }.maxOrNull() ?: 0f
                                                            val totalLogs = exHistory.size
                                                            val trendVal = if (exHistory.size >= 2) {
                                                                val firstV = exHistory.first().third
                                                                val lastV = exHistory.last().third
                                                                if (firstV > 0) ((lastV - firstV) / firstV) * 100f else 0f
                                                            } else 0f

                                                            val pPrompt = """
                                                                Audit sports performance for exercise '$selectedAnalyticsEx'.
                                                                User stats: Total logs indexed: $totalLogs sessions. Peak load: $maxLift kg. Trend slope: $trendVal%.
                                                                Task: Give an elite, highly concise sports-science analysis for this specific lift. Offer 2 high-level action steps to force progressive overload, prevent form failure, and enhance visual aesthetics. Max 100 words. Speak direct and premium.
                                                            """.trimIndent()
                                                            gymCoachFeedback = GeminiService.askGlowUpCoach(pPrompt)
                                                            isGymCoachLoading = false
                                                        }
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Text("RUN AI COACH AUDIT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GlowCyanAccent)
                                            }
                                        }
                                    }

                                    if (gymCoachFeedback.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = gymCoachFeedback,
                                            fontSize = 10.sp,
                                            color = GlowSilverAccent,
                                            lineHeight = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else {
                                        Text(
                                            text = "Tap 'RUN AI COACH AUDIT' above to analyze your logged history and obtain customized hyper-hypertrophy progression advice.",
                                            fontSize = 9.sp,
                                            color = GlowMutedGrey,
                                            lineHeight = 12.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // TAB RENDERER
                            when (selectedHubTab) {
                                0 -> { // TAB 0: LOG HISTORY LIST
                                    if (exHistory.isNotEmpty()) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            exHistory.forEachIndexed { index, data ->
                                                val (date, maxW, volume) = data
                                                val isOverload = if (index > 0) {
                                                    val prevData = exHistory[index - 1]
                                                    maxW > prevData.second || volume > prevData.third
                                                } else false
                                                
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(GlowDarkGrey.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                        .padding(8.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(text = "Date: $date", fontSize = 10.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                                                    }
                                                    
                                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Column(horizontalAlignment = Alignment.End) {
                                                            Text(text = "${maxW} kg", fontSize = 11.sp, color = GlowSilverAccent, fontWeight = FontWeight.ExtraBold)
                                                            Text(text = "Vol: ${volume.toInt()} kg", fontSize = 9.sp, color = GlowMutedGrey)
                                                        }
                                                        
                                                        if (isOverload) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .background(GlowBlueAccent.copy(alpha = 0.2f), CircleShape)
                                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                                            ) {
                                                                Text(text = "⚡ OVERLOAD", fontSize = 8.sp, color = GlowCyanAccent, fontWeight = FontWeight.Black)
                                                            }
                                                        } else if (index > 0) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .background(GlowDarkGrey, CircleShape)
                                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                                            ) {
                                                                Text(text = "🎯 STABLE", fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                                                            }
                                                        } else {
                                                            Box(
                                                                modifier = Modifier
                                                                    .background(GlowBlueAccent.copy(alpha = 0.1f), CircleShape)
                                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                                            ) {
                                                                Text(text = "🏁 BASELINE", fontSize = 8.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Text("No data available for this exercise in history.", fontSize = 10.sp, color = GlowMutedGrey)
                                    }
                                }
                                
                                1 -> { // TAB 1: WEEKLY, MONTHLY, YEARLY PERIOD TOTALS
                                    val periodAggregates = remember(allLogsWithSets, selectedAnalyticsEx, selectedPeriodType) {
                                        val calendar = Calendar.getInstance()
                                        val rawGroups = mutableMapOf<String, MutableList<WorkoutLogEntity>>()
                                        
                                        allLogsWithSets.forEach { log ->
                                            val date = DateUtils.parseDate(log.dateString)
                                            if (date != null) {
                                                calendar.time = date
                                                val year = calendar.get(Calendar.YEAR)
                                                val key = when (selectedPeriodType) {
                                                    "Weekly" -> {
                                                        val week = calendar.get(Calendar.WEEK_OF_YEAR)
                                                        "Wk $week, $year"
                                                    }
                                                    "Monthly" -> {
                                                        val monthNum = calendar.get(Calendar.MONTH)
                                                        val monthsAbbr = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                                                        "${monthsAbbr[monthNum]} $year"
                                                    }
                                                    else -> "$year"
                                                }
                                                rawGroups.getOrPut(key) { mutableListOf() }.add(log)
                                            }
                                        }
                                        
                                        rawGroups.map { (periodName, logsInPeriod) ->
                                            var totalSets = 0
                                            var totalReps = 0
                                            var maxWeight = 0f
                                            var totalVolume = 0f
                                            
                                            logsInPeriod.forEach { log ->
                                                val (_, weights, reps) = parseSetsLogJson(log.setsLogJson)
                                                if (selectedAnalyticsEx == "⚡ TOTAL COMBINED") {
                                                    weights.forEach { (exName, exWeights) ->
                                                        val exReps = reps[exName]
                                                        exWeights.forEach { (setNum, w) ->
                                                            val r = exReps?.get(setNum) ?: 0
                                                            totalSets++
                                                            totalReps += r
                                                            maxWeight = maxOf(maxWeight, w)
                                                            totalVolume += w * r
                                                        }
                                                    }
                                                } else {
                                                    val exWeights = weights[selectedAnalyticsEx]
                                                    val exReps = reps[selectedAnalyticsEx]
                                                    if (exWeights != null) {
                                                        exWeights.forEach { (setNum, w) ->
                                                            val r = exReps?.get(setNum) ?: 0
                                                            totalSets++
                                                            totalReps += r
                                                            maxWeight = maxOf(maxWeight, w)
                                                            totalVolume += w * r
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            PeriodStats(
                                                periodName = periodName,
                                                totalSets = totalSets,
                                                totalReps = totalReps,
                                                maxWeight = maxWeight,
                                                totalVolume = totalVolume,
                                                workoutCount = logsInPeriod.size
                                            )
                                        }.sortedBy { stats ->
                                            val logs = rawGroups[stats.periodName] ?: emptyList()
                                            logs.map { it.dateString }.minOrNull() ?: ""
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Period Type Chips selector
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            val periods = listOf("Weekly", "Monthly", "Yearly")
                                            periods.forEach { periodName ->
                                                val isSel = selectedPeriodType == periodName
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isSel) GlowBlueAccent.copy(alpha = 0.2f) else GlowDarkGrey)
                                                        .border(0.5.dp, if (isSel) GlowBlueAccent else GlowDarkGrey, RoundedCornerShape(6.dp))
                                                        .clickable { selectedPeriodType = periodName }
                                                        .padding(vertical = 6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(periodName.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (isSel) GlowBlueAccent else GlowSilverAccent)
                                                }
                                            }
                                        }

                                        if (periodAggregates.isNotEmpty()) {
                                            periodAggregates.forEach { stats ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(GlowDarkCard)
                                                        .border(0.5.dp, GlowDarkGrey, RoundedCornerShape(8.dp))
                                                        .padding(10.dp)
                                                ) {
                                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = stats.periodName.uppercase(),
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Black,
                                                                color = GlowCyanAccent
                                                            )
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(4.dp))
                                                                    .background(GlowDarkGrey)
                                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                                            ) {
                                                                Text("${stats.workoutCount} LOGS", fontSize = 7.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                                                            }
                                                        }

                                                        HorizontalDivider(color = GlowDarkGrey, thickness = 0.5.dp)

                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Column {
                                                                Text("TOTAL SETS", fontSize = 7.sp, color = GlowMutedGrey)
                                                                Text("${stats.totalSets} Sets complete", fontSize = 10.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                                                            }
                                                            Column {
                                                                Text("TOTAL REPS LIFTED", fontSize = 7.sp, color = GlowMutedGrey)
                                                                Text("${stats.totalReps} Repetitions", fontSize = 10.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                                                            }
                                                            Column(horizontalAlignment = Alignment.End) {
                                                                Text("PEAK MAX WEIGHT", fontSize = 7.sp, color = GlowMutedGrey)
                                                                Text("${stats.maxWeight.toInt()} kg", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Black)
                                                            }
                                                        }

                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .background(GlowDarkGrey.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                                                .padding(6.dp)
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text("TOTAL VOLUME LIFTED", fontSize = 8.sp, color = GlowSilverAccent)
                                                                Text("${stats.totalVolume.toInt()} kg", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Black)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Text("No period records matching specifications.", fontSize = 9.sp, color = GlowMutedGrey)
                                        }
                                    }
                                }

                                2 -> { // TAB 2: VISUAL OVERLOAD CHROMATIC LINE CHART
                                    val chartPoints = remember(exHistory, chartMetricType) {
                                        exHistory.map { triple ->
                                            // Format Date to short string MM-DD
                                            val shortDate = if (triple.first.length >= 10) triple.first.substring(5) else triple.first
                                            shortDate to (if (chartMetricType == "Weight") triple.second else triple.third)
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Chart Type Chips
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf("Weight", "Volume").forEach { type ->
                                                val isAct = chartMetricType == type
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isAct) GlowCyanAccent.copy(alpha = 0.2f) else GlowDarkGrey)
                                                        .border(0.5.dp, if (isAct) GlowCyanAccent else GlowDarkGrey, RoundedCornerShape(6.dp))
                                                        .clickable { chartMetricType = type }
                                                        .padding(vertical = 6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("PLOT ${type.uppercase()}", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (isAct) GlowCyanAccent else GlowSilverAccent)
                                                }
                                            }
                                        }

                                        if (chartPoints.size < 2) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(GlowBlack.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                                    .border(0.5.dp, GlowDarkGrey, RoundedCornerShape(8.dp))
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "Not enough data plotted yet. We need at least 2 distinct workout sessions for this exercise in history to compute an overload path graph. Keep logging sets!",
                                                    fontSize = 10.sp,
                                                    color = GlowMutedGrey,
                                                    textAlign = TextAlign.Center,
                                                    lineHeight = 13.sp
                                                )
                                            }
                                        } else {
                                            // Area Gradient Line Chart
                                            Canvas(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(130.dp)
                                                    .background(GlowBlack.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                                    .border(0.5.dp, GlowDarkGrey, RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 14.dp, vertical = 14.dp)
                                            ) {
                                                val width = size.width
                                                val height = size.height
                                                
                                                val minY = chartPoints.minOf { it.second }
                                                val maxY = chartPoints.maxOf { it.second }
                                                val valRange = if (maxY == minY) 1f else (maxY - minY)
                                                
                                                val adjustedMinY = (minY - valRange * 0.15f).coerceAtLeast(0f)
                                                val adjustedMaxY = (maxY + valRange * 0.15f)
                                                val adjustedRange = adjustedMaxY - adjustedMinY
                                                
                                                val xSpacing = width / (chartPoints.size - 1)
                                                
                                                val coordinates = chartPoints.mapIndexed { index, pair ->
                                                    val x = index * xSpacing
                                                    val y = if (adjustedRange > 0f) {
                                                        height - ((pair.second - adjustedMinY) / adjustedRange) * height
                                                    } else {
                                                        height / 2f
                                                    }
                                                    Offset(x, y)
                                                }
                                                
                                                // 1. Draw Grid Lines
                                                val gridLines = 3
                                                for (i in 0..gridLines) {
                                                    val yGrid = (height / gridLines) * i
                                                    drawLine(
                                                        color = GlowDarkGrey.copy(alpha = 0.4f),
                                                        start = Offset(0f, yGrid),
                                                        end = Offset(width, yGrid),
                                                        strokeWidth = 1f
                                                    )
                                                }
                                                
                                                // 2. Draw Glow fill wave
                                                val strokePath = Path().apply {
                                                    moveTo(coordinates.first().x, coordinates.first().y)
                                                    for (i in 1 until coordinates.size) {
                                                        lineTo(coordinates[i].x, coordinates[i].y)
                                                    }
                                                }
                                                
                                                val fillPath = Path().apply {
                                                    addPath(strokePath)
                                                    lineTo(coordinates.last().x, height)
                                                    lineTo(coordinates.first().x, height)
                                                    close()
                                                }
                                                
                                                drawPath(
                                                    path = fillPath,
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(GlowCyanAccent.copy(alpha = 0.22f), Color.Transparent),
                                                        startY = 0f,
                                                        endY = height
                                                    )
                                                )
                                                
                                                drawPath(
                                                    path = strokePath,
                                                    color = GlowCyanAccent,
                                                    style = Stroke(width = 4f)
                                                )
                                                
                                                // 3. Draw Nodes
                                                coordinates.forEachIndexed { i, pt ->
                                                    drawCircle(
                                                        color = if (i == coordinates.lastIndex) GlowCyanAccent else GlowSilverAccent,
                                                        radius = 5f,
                                                        center = pt
                                                    )
                                                    drawCircle(
                                                        color = GlowBlack,
                                                        radius = 2f,
                                                        center = pt
                                                    )
                                                }
                                            }

                                            // X Axis Labels row
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                chartPoints.forEach { pt ->
                                                    Text(pt.first, fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            // Real-time delta stats calculations
                                            val firstPt = chartPoints.first().second
                                            val lastPt = chartPoints.last().second
                                            if (firstPt > 0f) {
                                                val percentChange = ((lastPt - firstPt) / firstPt) * 100f
                                                val trendText = if (percentChange >= 0f) {
                                                    "▲ LIFT GAIN INCREASE: +${String.format("%.1f", percentChange)}%"
                                                } else {
                                                    "▼ DECREASE: ${String.format("%.1f", percentChange)}%"
                                                }
                                                
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (percentChange >= 0f) GlowCyanAccent.copy(alpha = 0.08f) else GlowBlueAccent.copy(alpha = 0.08f))
                                                        .padding(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("OVERLOAD RECOVERY SLOPE", fontSize = 8.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                                                        Text(
                                                            text = trendText, 
                                                            fontSize = 8.sp, 
                                                            color = if (percentChange >= 0f) GlowCyanAccent else GlowBlueAccent, 
                                                            fontWeight = FontWeight.Black
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Active Routine Header Card
        val splitType = when(activeViewDay) {
            "Monday" -> "Push A (Upper Chest Focus)"
            "Tuesday" -> "Pull A (Width Focus)"
            "Wednesday" -> "Legs A (Core Lift Focus)"
            "Thursday" -> "Push B (Shoulder Focus)"
            "Friday" -> "Pull B (Back Thickness Focus)"
            "Saturday" -> "Legs B + Lateral Aesthetics Focus"
            else -> "Sunday Recovery (Stretching, posture & walk only)"
        }

        item {
            GlowGlassCard {
                Text(text = "ACTIVE SPLIT PROFILE", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = splitType, fontSize = 18.sp, color = GlowSilverAccent, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when(activeViewDay) {
                        "Sunday" -> "Sleep, read, stretch neck and facial cells, ingest raw fluids."
                        "Saturday" -> "Targeting high visual impact areas: calves, shoulders, and aesthetic neck sizing."
                        else -> "Keep rest timers strictly under 90s to maintain high blood volume (pump) and cellular pressure."
                    },
                    fontSize = 11.sp, color = GlowMutedGrey
                )
            }
        }

        // 4. Exercise Checklist Container
        val exercises = when(activeViewDay) {
            "Monday" -> listOf(
                GymExercise("Incline Bench Press", 4, "Core Upper Chest builder"),
                GymExercise("Dumbbell Shoulder Press", 3, "Front and side deltoid sizing"),
                GymExercise("Incline Dumbbell Press", 3, "Clavicular head isolation"),
                GymExercise("Lateral Raises", 4, "Side delts - essential for visual V-Taper"),
                GymExercise("Cable Fly", 3, "Inner pectoral tension"),
                GymExercise("Tricep Pushdown", 3, "Triceps lateral head thickness")
            )
            "Tuesday" -> listOf(
                GymExercise("Pull-Ups or Lat Pulldown", 4, "Outer lat stretch (v-taper foundation)"),
                GymExercise("Chest Supported Row", 3, "Mid-back thickness"),
                GymExercise("Single Arm Lat Pulldown", 3, "Lower lat insertion isolation"),
                GymExercise("Face Pulls", 4, "Rear delts and rotator cuffs for perfect posture"),
                GymExercise("Dumbbell Curl", 3, "Biceps brachii belly growth"),
                GymExercise("Hammer Curl", 3, "Brachialis & forearms size")
            )
            "Wednesday" -> listOf(
                GymExercise("Squats (Barbell)", 4, "Core lower body testosterone releaser"),
                GymExercise("Romanian Deadlift", 4, "Erector spinae and hamstrings thickness"),
                GymExercise("Leg Press", 3, "Quad hypertrophy density"),
                GymExercise("Leg Curl", 3, "Posterior leg string alignment"),
                GymExercise("Standing Calf Raises", 4, "High tension calves sculpting"),
                GymExercise("Hanging Leg Raises", 3, "Abs core V-line definition")
            )
            "Thursday" -> listOf(
                GymExercise("Seated Dumbbell Press", 4, "Primary shoulder visual weight"),
                GymExercise("Incline Machine Press", 3, "Constant chest upper fiber strain"),
                GymExercise("Lateral Raises", 5, "Strict high reps side deltoids expansion"),
                GymExercise("Rear Delt Fly", 4, "Posterior cap sizing"),
                GymExercise("Tricep Overhead Extension", 3, "Triceps long head elongation"),
                GymExercise("Dips", 3, "Lower chest push force & triceps density")
            )
            "Friday" -> listOf(
                GymExercise("Barbell Row", 4, "Core explosive pulling power"),
                GymExercise("Lat Pulldown (Neutral)", 3, "Lats depth and wingspan"),
                GymExercise("Seated Cable Row", 3, "Middle and lower lower trap muscle mass"),
                GymExercise("Face Pull", 3, "Rear shoulders stabilization"),
                GymExercise("Incline Dumbbell Curl", 3, "Peak biceps stretch contraction"),
                GymExercise("Hammer Curl", 3, "Forearm-biceps split line")
            )
            "Saturday" -> listOf(
                GymExercise("Bulgarian Split Squat", 3, "Unilateral glute-quad density"),
                GymExercise("Romanian Deadlift", 3, "Posterior safety line"),
                GymExercise("Leg Extension", 3, "Quads separation teardrop focus"),
                GymExercise("Leg Curl", 3, "Bedtime recovery prep"),
                GymExercise("Calf Raises", 4, "Calves expansion block"),
                GymExercise("Neck Curls", 3, "Visual thickness for masculine profile"),
                GymExercise("Neck Extensions", 3, "Posterior aesthetic posture neck support"),
                GymExercise("Side Neck Raises", 3, "Lateral jaw-neck split visual frame")
            )
            else -> emptyList()
        }

        if (exercises.isNotEmpty()) {
            item {
                Text(text = "EXERCISES & DETAILED SETS TRACKER", fontSize = 11.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
            }

            items(exercises) { ex ->
                val isCompleted = checkedExercises.contains(ex.name)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlowDarkCard)
                        .border(
                            1.dp,
                            if (isCompleted) GlowCyanAccent.copy(alpha = 0.4f) else GlowDarkGrey,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ex.name, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = if (isCompleted) GlowCyanAccent else GlowSilverAccent
                                )
                                Text(text = ex.purpose, fontSize = 10.sp, color = GlowMutedGrey)
                            }

                            // Exercise completion checkbox
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = { checked ->
                                    val updatedChecked = if (checked) {
                                        checkedExercises + ex.name
                                    } else {
                                        checkedExercises - ex.name
                                    }
                                    checkedExercises = updatedChecked
                                    saveChanges(completedSetsMap, completedWeightMap, completedRepsMap, updatedChecked)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GlowCyanAccent,
                                    uncheckedColor = GlowDarkGrey
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive Sets Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val setsForThisEx = completedSetsMap[ex.name] ?: emptySet()
                            for (setNum in 1..ex.totalSets) {
                                val setDone = setsForThisEx.contains(setNum)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (setDone) GlowCyanAccent.copy(alpha = 0.2f) else GlowDarkGrey)
                                        .border(
                                            1.dp,
                                            if (setDone) GlowCyanAccent else GlowMutedGrey.copy(alpha = 0.3f),
                                            CircleShape
                                        )
                                        .clickable {
                                            val currentSets = completedSetsMap[ex.name] ?: emptySet()
                                            val updated = if (currentSets.contains(setNum)) {
                                                currentSets - setNum
                                            } else {
                                                currentSets + setNum
                                            }
                                            val newCompletedSetsMap = completedSetsMap + (ex.name to updated)
                                            completedSetsMap = newCompletedSetsMap

                                            val exWeights = completedWeightMap[ex.name]?.toMutableMap() ?: mutableMapOf()
                                            val exReps = completedRepsMap[ex.name]?.toMutableMap() ?: mutableMapOf()
                                            if (!exWeights.containsKey(setNum)) {
                                                val (prevW, prevR) = getPreviousSetPerf(ex.name, setNum)
                                                exWeights[setNum] = prevW
                                                exReps[setNum] = prevR
                                            }
                                            val newWeightMap = completedWeightMap + (ex.name to exWeights)
                                            val newRepsMap = completedRepsMap + (ex.name to exReps)
                                            completedWeightMap = newWeightMap
                                            completedRepsMap = newRepsMap

                                            val newChecked = if (!setDone) checkedExercises + ex.name else checkedExercises
                                            checkedExercises = newChecked

                                            saveChanges(newCompletedSetsMap, newWeightMap, newRepsMap, newChecked)

                                            if (!setDone) {
                                                timerSeconds = timerPresetSelected
                                                timerRunning = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "S$setNum", 
                                        fontSize = 10.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = if (setDone) GlowCyanAccent else GlowSilverAccent
                                    )
                                }
                            }
                        }

                        // EXPANDABLE WEIGHTS & REPS EDITOR
                        var showSetEditor by remember { mutableStateOf(true) }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSetEditor = !showSetEditor }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (showSetEditor) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand set log",
                                    tint = GlowCyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Log weights and reps per set (Tap value to type)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GlowCyanAccent
                                )
                            }
                            
                            val progressOverloadText = remember(allWorkoutLogs, ex.name) {
                                computeProgressiveOverloadSummary(ex.name, allWorkoutLogs)
                            }
                            if (progressOverloadText.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .background(GlowBlueAccent.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = progressOverloadText,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GlowCyanAccent
                                    )
                                }
                            }
                        }

                        if (showSetEditor) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                for (setNum in 1..ex.totalSets) {
                                    val setDone = completedSetsMap[ex.name]?.contains(setNum) == true
                                    val currentWeight = completedWeightMap[ex.name]?.get(setNum) ?: run {
                                        val (prevW, _) = getPreviousSetPerf(ex.name, setNum)
                                        prevW
                                    }
                                    val currentReps = completedRepsMap[ex.name]?.get(setNum) ?: run {
                                        val (_, prevR) = getPreviousSetPerf(ex.name, setNum)
                                        prevR
                                    }
                                    val (prevW, prevR) = getPreviousSetPerf(ex.name, setNum)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(GlowDarkGrey.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .border(0.5.dp, if (setDone) GlowCyanAccent.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(8.dp))
                                            .padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.width(36.dp)) {
                                            Text(
                                                text = "S$setNum",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (setDone) GlowCyanAccent else GlowSilverAccent
                                            )
                                            if (prevW > 0) {
                                                Text(
                                                    text = "${prevW.toInt()}x$prevR",
                                                    fontSize = 8.sp,
                                                    color = GlowMutedGrey
                                                )
                                            }
                                        }

                                        // Weight Selector (-2.5 / +2.5 kg)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(GlowDarkGrey, RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        val newVal = (currentWeight - 2.5f).coerceAtLeast(0f)
                                                        val exWeights = completedWeightMap[ex.name]?.toMutableMap() ?: mutableMapOf()
                                                        exWeights[setNum] = newVal
                                                        completedWeightMap = completedWeightMap + (ex.name to exWeights)
                                                        saveChanges(completedSetsMap, completedWeightMap, completedRepsMap, checkedExercises)
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "-", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GlowSilverAccent)
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .width(54.dp)
                                                    .background(GlowBlack, RoundedCornerShape(4.dp))
                                                    .border(0.5.dp, GlowDarkGrey, RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        editingSetInfo = Triple(ex.name, setNum, currentWeight to currentReps)
                                                    }
                                                    .padding(vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${currentWeight}k",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = GlowSilverAccent
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(GlowDarkGrey, RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        val newVal = currentWeight + 2.5f
                                                        val exWeights = completedWeightMap[ex.name]?.toMutableMap() ?: mutableMapOf()
                                                        exWeights[setNum] = newVal
                                                        completedWeightMap = completedWeightMap + (ex.name to exWeights)
                                                        saveChanges(completedSetsMap, completedWeightMap, completedRepsMap, checkedExercises)
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "+", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GlowSilverAccent)
                                            }
                                        }

                                        // Reps Selector (-1 / +1 reps)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(GlowDarkGrey, RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        val newVal = (currentReps - 1).coerceAtLeast(0)
                                                        val exReps = completedRepsMap[ex.name]?.toMutableMap() ?: mutableMapOf()
                                                        exReps[setNum] = newVal
                                                        completedRepsMap = completedRepsMap + (ex.name to exReps)
                                                        saveChanges(completedSetsMap, completedWeightMap, completedRepsMap, checkedExercises)
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "-", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GlowSilverAccent)
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .width(42.dp)
                                                    .background(GlowBlack, RoundedCornerShape(4.dp))
                                                    .border(0.5.dp, GlowDarkGrey, RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        editingSetInfo = Triple(ex.name, setNum, currentWeight to currentReps)
                                                    }
                                                    .padding(vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${currentReps}r",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = GlowSilverAccent
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(GlowDarkGrey, RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        val newVal = currentReps + 1
                                                        val exReps = completedRepsMap[ex.name]?.toMutableMap() ?: mutableMapOf()
                                                        exReps[setNum] = newVal
                                                        completedRepsMap = completedRepsMap + (ex.name to exReps)
                                                        saveChanges(completedSetsMap, completedWeightMap, completedRepsMap, checkedExercises)
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "+", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GlowSilverAccent)
                                            }
                                        }

                                        // Check Button
                                        IconButton(
                                            onClick = {
                                                val currentSets = completedSetsMap[ex.name] ?: emptySet()
                                                val updated = if (currentSets.contains(setNum)) {
                                                    currentSets - setNum
                                                } else {
                                                    currentSets + setNum
                                                }
                                                val newCompMap = completedSetsMap + (ex.name to updated)
                                                completedSetsMap = newCompMap

                                                val exWeights = completedWeightMap[ex.name]?.toMutableMap() ?: mutableMapOf()
                                                val exReps = completedRepsMap[ex.name]?.toMutableMap() ?: mutableMapOf()
                                                if (!exWeights.containsKey(setNum)) {
                                                    exWeights[setNum] = currentWeight
                                                    exReps[setNum] = currentReps
                                                    completedWeightMap = completedWeightMap + (ex.name to exWeights)
                                                    completedRepsMap = completedRepsMap + (ex.name to exReps)
                                                }

                                                val newChecked = if (!setDone) checkedExercises + ex.name else checkedExercises
                                                checkedExercises = newChecked

                                                saveChanges(newCompMap, completedWeightMap, completedRepsMap, newChecked)

                                                if (!setDone) {
                                                    timerSeconds = timerPresetSelected
                                                    timerRunning = true
                                                }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Toggle Set Action",
                                                tint = if (setDone) GlowCyanAccent else GlowDarkGrey,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. REST TIMER CARD
        item {
            GlowGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "INTER-SET HYPERTROPHY REST TIMER", fontSize = 10.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                        Text(
                            text = String.format("%02d:%02d", timerSeconds / 60, timerSeconds % 60),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (timerRunning) GlowCyanAccent else GlowSilverAccent
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { timerRunning = !timerRunning },
                            modifier = Modifier.background(GlowDarkGrey, CircleShape).size(38.dp)
                        ) {
                            if (timerRunning) {
                                Text(
                                    "||", 
                                    color = GlowCyanAccent, 
                                    fontWeight = FontWeight.Bold, 
                                    fontFamily = FontFamily.Monospace, 
                                    fontSize = 12.sp
                                )
                            } else {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Start Timer",
                                    tint = GlowCyanAccent
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                timerRunning = false
                                timerSeconds = timerPresetSelected
                            },
                            modifier = Modifier.background(GlowDarkGrey, CircleShape).size(38.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Timer", tint = GlowNeonRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(60, 90, 120)
                    presets.forEach { s ->
                        val isPresetActive = timerPresetSelected == s
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isPresetActive) GlowBlueAccent.copy(alpha = 0.2f) else GlowDarkGrey)
                                .border(1.dp, if (isPresetActive) GlowBlueAccent else GlowDarkGrey, RoundedCornerShape(6.dp))
                                .clickable {
                                    timerPresetSelected = s
                                    timerSeconds = s
                                    timerRunning = false
                                }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${s}s REST", fontSize = 10.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. Extra Daily Aesthetics Protocols
        item {
            GlowGlassCard {
                Text(text = "DAILY EXTRA AESTHETIC PROTOCOLS", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Ancillary Maxing Drills", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(10.dp))

                val ancillary = listOf(
                    AncillaryHabit("Chin Tucks (3x15 rep)", "Tucking chin firmly backwards to build neck flexors for sharp zero-degree jawline projection.", "Posture & Neck density"),
                    AncillaryHabit("Mewing Protocol (Whole day)", "Flat resting tongue on palate, closed mouth nasal breathing to expand upper jaw bones & zygomatics.", "Maxilla expansion"),
                    AncillaryHabit("Face Yoga Drills (10 Mins)", "Sucking cheeks, smiling tension exercises to contract buccinator fat and tighten cheek bones.", "Facial depuff & contour"),
                    AncillaryHabit("Posture Corrections (5 Mins)", "Retracting scapula, chest flat to prevent forward head slouch. Drives visual frame size upward.", "Scoliosis protection")
                )

                ancillary.forEach { habit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var done by remember { mutableStateOf(false) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = habit.title, fontSize = 12.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                            Text(text = habit.desc, fontSize = 10.sp, color = GlowMutedGrey)
                            Text(text = "Core: ${habit.science}", fontSize = 9.sp, color = GlowCyanAccent)
                        }
                        Checkbox(
                            checked = done,
                            onCheckedChange = { done = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GlowCyanAccent,
                                uncheckedColor = GlowDarkGrey
                            )
                        )
                    }
                }
            }
        }

        // 7. 3-Day Cardio System Card
        item {
            GlowGlassCard {
                Text(text = "PUFFINESS FLUSH & CARDIO METALLURGY", fontSize = 10.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                Text(text = "3-Days Weekly (20-30 Mins)", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Performs cardiovascular lymphatic flush. Safely drives water stored in facial cheeks downward, showing deep jawline definition.",
                    fontSize = 11.sp, color = GlowMutedGrey
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val cardios = listOf("Incline Walk", "Cycling", "Jogging")
                    cardios.forEach { style ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(GlowDarkGrey)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = style, fontSize = 10.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // 8. Visual Looksmaxing Tier Pyramid
        item {
            GlowGlassCard {
                Text(text = "LOOKSMAXING MUSCLE HIERARCHY", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Focus Priorities for Max Attractiveness", fontSize = 15.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(10.dp))

                TierRow("💎 TIER 1: CRITICAL ACTION", "Side Delts, Upper Chest, Lats, Neck", GlowCyanAccent)
                Spacer(modifier = Modifier.height(6.dp))
                TierRow("⚡ TIER 2: SYMMETRY POWER", "Rear Delts, Arms, Traps", GlowBlueAccent)
                Spacer(modifier = Modifier.height(6.dp))
                TierRow("⭐ TIER 3: POLISH & FRAME", "Abs, Forearms, Calves", GlowSilverAccent)
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    if (editingSetInfo != null) {
        val (exName, setNum, currentVals) = editingSetInfo!!
        var weightInput by remember(editingSetInfo) { mutableStateOf(currentVals.first.toString()) }
        var repsInput by remember(editingSetInfo) { mutableStateOf(currentVals.second.toString()) }
        
        AlertDialog(
            onDismissRequest = { editingSetInfo = null },
            containerColor = GlowDarkCard,
            title = {
                Text(
                    "LOG SET S$setNum — ${exName.uppercase()}",
                    fontSize = 14.sp,
                    color = GlowCyanAccent,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter precise performance specs for this set:", fontSize = 11.sp, color = GlowMutedGrey)
                    
                    // Weight input
                    Column {
                        Text("Weight (kg)", fontSize = 11.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GlowSilverAccent,
                                unfocusedTextColor = GlowSilverAccent,
                                focusedBorderColor = GlowCyanAccent,
                                unfocusedBorderColor = GlowDarkGrey,
                                focusedContainerColor = GlowBlack,
                                unfocusedContainerColor = GlowBlack
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("edit_weight_input")
                        )
                    }
                    
                    // Reps input
                    Column {
                        Text("Reps Count", fontSize = 11.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = repsInput,
                            onValueChange = { repsInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GlowSilverAccent,
                                unfocusedTextColor = GlowSilverAccent,
                                focusedBorderColor = GlowCyanAccent,
                                unfocusedBorderColor = GlowDarkGrey,
                                focusedContainerColor = GlowBlack,
                                unfocusedContainerColor = GlowBlack
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("edit_reps_input")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightInput.toFloatOrNull() ?: currentVals.first
                        val r = repsInput.toIntOrNull() ?: currentVals.second
                        
                        val exWeights = completedWeightMap[exName]?.toMutableMap() ?: mutableMapOf()
                        val exReps = completedRepsMap[exName]?.toMutableMap() ?: mutableMapOf()
                        
                        exWeights[setNum] = w
                        exReps[setNum] = r
                        
                        completedWeightMap = completedWeightMap + (exName to exWeights)
                        completedRepsMap = completedRepsMap + (exName to exReps)
                        
                        // Automatically mark set completed when explicitly entered!
                        val currentSets = completedSetsMap[exName] ?: emptySet()
                        val newCompMap = completedSetsMap + (exName to (currentSets + setNum))
                        completedSetsMap = newCompMap
                        
                        val newChecked = checkedExercises + exName
                        checkedExercises = newChecked
                        
                        saveChanges(newCompMap, completedWeightMap, completedRepsMap, newChecked)
                        
                        editingSetInfo = null
                        
                        // Rest timer auto-trigger
                        if (!currentSets.contains(setNum)) {
                            timerSeconds = timerPresetSelected
                            timerRunning = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GlowCyanAccent)
                ) {
                    Text("Save Log", color = GlowBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSetInfo = null }) {
                    Text("Cancel", color = GlowMutedGrey, fontSize = 12.sp)
                }
            }
        )
    }
}

private data class GymExercise(val name: String, val totalSets: Int, val purpose: String)
private data class AncillaryHabit(val title: String, val desc: String, val science: String)
private data class PeriodStats(
    val periodName: String,
    val totalSets: Int,
    val totalReps: Int,
    val maxWeight: Float,
    val totalVolume: Float,
    val workoutCount: Int
)

@Composable
private fun TierRow(tier: String, muscleList: String, color: Color) {
    Column {
        Text(text = tier, fontSize = 10.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = muscleList, fontSize = 11.sp, color = GlowSilverAccent)
    }
}

private data class JuiceFormulaGuide(
    val subtitle: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val molecularScience: String,
    val recommendedHour: String
)

@Composable
private fun RowScope.GoalHighlightCapsule(title: String, ingredients: String) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(GlowDarkGrey.copy(alpha = 0.3f))
            .border(0.5.dp, GlowMutedGrey.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GlowSilverAccent)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = ingredients, fontSize = 8.sp, color = GlowMutedGrey, lineHeight = 10.sp)
        }
    }
}

@Composable
fun DietPlannerContent(viewModel: GlowViewModel) {
    val waterLog by viewModel.waterLogFlow.collectAsState()
    val dietLog by viewModel.dietLogFlow.collectAsState()
    val selectedDateStr by viewModel.selectedDateString.collectAsState()

    var caloriesInput by remember { mutableStateOf("") }
    var proteinInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    
    var activeDetailJuice by remember { mutableStateOf<String?>(null) }

    // Synchronize input strings with database updates
    LaunchedEffect(dietLog) {
        dietLog?.let {
            caloriesInput = if (it.caloriesKcal > 0) it.caloriesKcal.toString() else ""
            proteinInput = if (it.proteinGrams > 0) it.proteinGrams.toString() else ""
            notesInput = it.notes
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. Water Tracker Section
        item {
            GlowGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "DYNAMIC HYDRATION COMPASS", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Water Intake Logging", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                        Text(text = "Daily Target: 3000ml (3.0 Litres)", fontSize = 11.sp, color = GlowMutedGrey)
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(GlowBlueAccent.copy(alpha = 0.15f))
                            .border(1.dp, GlowBlueAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.FlameWithSecondary, contentDescription = "Water", tint = GlowBlueAccent)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Water Meter
                val amountMl = waterLog?.amountMl ?: 0
                val percent = (amountMl.toFloat() / 3000f).coerceIn(0f, 1f)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${amountMl}ml logged", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GlowSilverAccent)
                    Spacer(modifier = Modifier.width(12.dp))
                    // custom progress bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(GlowDarkGrey)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percent)
                                .background(Brush.horizontalGradient(listOf(GlowBlueAccent, GlowCyanAccent)))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Water logging buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlowButton(
                        text = "+250ml (Cup)",
                        onClick = { viewModel.addWater(250) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        isSecondary = true
                    )
                    GlowButton(
                        text = "+500ml (Bottle)",
                        onClick = { viewModel.addWater(500) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        isSecondary = false
                    )
                }
            }
        }

        // 2. Meal & Nutrients Log
        item {
            GlowGlassCard {
                Text(text = "CALORIC & PROTEIN TELEMETRY", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Log High Protein Fuel", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = caloriesInput,
                        onValueChange = { caloriesInput = it },
                        label = { Text("Calories (kcal)", color = GlowMutedGrey) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GlowBlueAccent,
                            unfocusedBorderColor = GlowDarkGrey,
                            focusedTextColor = GlowSilverAccent,
                            unfocusedTextColor = GlowSilverAccent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = proteinInput,
                        onValueChange = { proteinInput = it },
                        label = { Text("Protein (g)", color = GlowMutedGrey) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GlowBlueAccent,
                            unfocusedBorderColor = GlowDarkGrey,
                            focusedTextColor = GlowSilverAccent,
                            unfocusedTextColor = GlowSilverAccent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Meal notes (eggs, chicken, oats...)", color = GlowMutedGrey) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlowBlueAccent,
                        unfocusedBorderColor = GlowDarkGrey,
                        focusedTextColor = GlowSilverAccent,
                        unfocusedTextColor = GlowSilverAccent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                GlowButton(
                    text = "Transmit Nutrients Log",
                    onClick = {
                        val c = caloriesInput.toIntOrNull() ?: 0
                        val p = proteinInput.toIntOrNull() ?: 0
                        viewModel.updateDiet(c, p, notesInput)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Send
                )
            }
        }

        // 5 Daily Juices & Elixirs Tracker
        item {
            val checkedJuicesSet = remember(dietLog) {
                dietLog?.checkedJuices?.split(",")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
            }
            
            GlowGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "DAILY PHYSIOLOGICAL ACCELERATORS", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Text(text = "5 Specialty Glow Elixirs", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                    }
                    Box(
                        modifier = Modifier
                            .background(GlowCyanAccent.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${checkedJuicesSet.size} / 5 DRANK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = GlowCyanAccent
                        )
                    }
                }

                Text(
                    text = "Consuming cold-pressed anti-oxidant solvents on an empty stomach triggers maximum cell repair, dermal gloss, and anti-aging gene pathways.",
                    fontSize = 11.sp,
                    color = GlowMutedGrey,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Progress Bar
                val juiceProgressPercent = (checkedJuicesSet.size.toFloat() / 5f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(GlowDarkGrey)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(juiceProgressPercent)
                            .background(Brush.horizontalGradient(listOf(GlowBlueAccent, GlowCyanAccent)))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val dayOfWeekName = DateUtils.getDayOfWeeksName(selectedDateStr)
                val dayOfWeekNum = when (dayOfWeekName) {
                    "Monday" -> 0
                    "Tuesday" -> 1
                    "Wednesday" -> 2
                    "Thursday" -> 3
                    "Friday" -> 4
                    "Saturday" -> 5
                    else -> 6
                }

                val drink4Title = when (dayOfWeekNum) {
                    0 -> "Carrot + Beetroot Juice"
                    1 -> "Lemon + Ginger Water"
                    2 -> "Carrot + Beetroot Juice"
                    3 -> "Lemon + Ginger Water"
                    4 -> "Carrot + Beetroot Juice"
                    5 -> "Carrot + Beetroot Juice"
                    else -> "Green Juice"
                }
                val drink4Ing = when (dayOfWeekNum) {
                    0 -> "Carrot, Beetroot, Ginger, Lemon"
                    1 -> "Lemon, Ginger, Warm Water"
                    2 -> "Carrot, Beetroot, Ginger, Lemon"
                    3 -> "Lemon, Ginger, Warm Water"
                    4 -> "Carrot, Beetroot, Ginger, Lemon"
                    5 -> "Carrot, Beetroot, Ginger, Lemon"
                    else -> "Cucumber, Mint, Lemon, Green Apple, Ginger"
                }

                val drink5Title = when (dayOfWeekNum) {
                    0 -> "Cucumber + Mint Juice"
                    1 -> "Cucumber + Mint Juice"
                    2 -> "Watermelon Juice"
                    3 -> "Cucumber + Mint Juice"
                    4 -> "Pineapple + Mint Juice"
                    5 -> "Cucumber + Mint Juice"
                    else -> "Coconut Water"
                }
                val drink5Ing = when (dayOfWeekNum) {
                    2 -> "Watermelon Chunks, Mint, Lime"
                    4 -> "Pineapple, Mint, Black Salt"
                    6 -> "Fresh Tender Coconut Water"
                    else -> "Cucumber, Mint, Lemon"
                }

                val premiumJuicesList = listOf(
                    Triple(
                        "drink_1",
                        "Drink 1 — Anti-Inflammation Drink",
                        "Warm water + ½ tsp Turmeric + Ginger + Pinch black pepper" to "Fights systemic stress, reduces tissue swelling, and hydrates immediately after waking."
                    ),
                    Triple(
                        "drink_2",
                        "Drink 2 — Vitamin C Shot",
                        "20–30 ml Amla juice + water" to "Essential cofactor for active collagen synthesis & hair matrix repair. Combats follicle aging."
                    ),
                    Triple(
                        "drink_3",
                        "Drink 3 — Gut + Skin Support",
                        "20–30 ml Aloe vera juice + warm water" to "Heals the gut-skin axis, eliminates internal systemic heat that fuels acne, and restores hydration balance."
                    ),
                    Triple(
                        "drink_4",
                        "Mid-Morning: $drink4Title",
                        "$drink4Ing" to "Weekly Rotation recipe. Targets specific physical recovery goals based on selected program day."
                    ),
                    Triple(
                        "drink_5",
                        "Afternoon: $drink5Title",
                        "$drink5Ing" to "Weekly Rotation cooling blend. Assists muscle tissues, regulates digestion, and depuffs eyes."
                    )
                )

                 premiumJuicesList.forEachIndexed { index, item ->
                    val id = item.first
                    val title = item.second
                    val ingredients = item.third.first
                    val benefit = item.third.second
                    val isChecked = checkedJuicesSet.contains(id)

                    val itemFocus = when (id) {
                        "drink_1" -> "🌅 MORNING: IMMEDIATELY AFTER WAKING"
                        "drink_2" -> "🌅 MORNING: 15 MINUTES LATER"
                        "drink_3" -> "🌅 MORNING: AFTER BREAKFAST"
                        "drink_4" -> "📅 ROTATION: MID-MORNING ($dayOfWeekName)"
                        else -> "📅 ROTATION: AFTERNOON ($dayOfWeekName)"
                    }

                    val accentColor = when (id) {
                        "drink_1" -> GlowBlueAccent
                        "drink_2" -> GlowCyanAccent
                        "drink_3" -> GlowSilverAccent
                        "drink_4" -> GlowCyanAccent
                        else -> GlowBlueAccent
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isChecked) GlowDarkGrey.copy(alpha = 0.5f) else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (isChecked) accentColor.copy(alpha = 0.4f) else GlowDarkGrey.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { activeDetailJuice = id }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Checked State Indicator Box
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(top = 2.dp)
                                    .clip(CircleShape)
                                    .background(if (isChecked) accentColor else Color.Transparent)
                                    .border(
                                        width = 2.dp,
                                        color = if (isChecked) accentColor else GlowMutedGrey.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.toggleJuice(id) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Checked",
                                        tint = GlowBlack,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // Juice details block
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isChecked) accentColor else GlowSilverAccent,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Details",
                                        tint = GlowMutedGrey.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(2.dp))

                                // Topic Tag Capsule
                                Box(
                                    modifier = Modifier
                                        .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .border(0.5.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .clickable { activeDetailJuice = id }
                                ) {
                                    Text(
                                        text = itemFocus,
                                        fontSize = 8.sp,
                                        color = accentColor,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "INGREDIENTS: $ingredients",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GlowSilverAccent.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = benefit,
                                    fontSize = 10.sp,
                                    color = GlowMutedGrey,
                                    lineHeight = 13.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = GlowDarkGrey)
                Spacer(modifier = Modifier.height(12.dp))

                // Best Juices Goals Highlights
                Text(
                    text = "🔥 TARGETED JUICES FOR PHYSICAL GOALS",
                    fontSize = 11.sp,
                    color = GlowCyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GoalHighlightCapsule("✨ SKIN GLOW", "Amla, Carrot, Beetroot, Cucumber")
                    GoalHighlightCapsule("💇 HAIR GROWTH", "Amla, Aloe vera, Carrot")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GoalHighlightCapsule("😴 DEPUFF", "Cucumber, Lemon water, Mint")
                    GoalHighlightCapsule("💪 RECOVERY", "Beetroot, Watermelon, Turmeric")
                    GoalHighlightCapsule("🧬 ANTI-AGE", "Turmeric, Amla, Aloe")
                }

                HorizontalDivider(color = GlowDarkGrey)
                Spacer(modifier = Modifier.height(12.dp))

                // Important Rules Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GlowBlueAccent.copy(alpha = 0.05f))
                        .border(1.dp, GlowBlueAccent.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Rules",
                                tint = GlowBlueAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "CRITICAL COLD-PRESS LAWS",
                                fontSize = 10.sp,
                                color = GlowBlueAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        val rules = listOf(
                            "🚫 No added sugar — consume raw & clean.",
                            "🥤 Drink juices super slowly, do NOT chug rapidly.",
                            "🥕 No excessive beetroot daily (stick to rotation ratio).",
                            "💧 Hydrate plentifully throughout daylight hours."
                        )
                        rules.forEach { rule ->
                            Text(
                                text = rule,
                                fontSize = 10.sp,
                                color = GlowMutedGrey,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Show Interactive Extraction & Molecular Guide Dialog when selected
                val activeJuiceId = activeDetailJuice
                if (activeJuiceId != null) {
                    val details = when (activeJuiceId) {
                        "drink_1" -> JuiceFormulaGuide(
                            subtitle = "Drink 1: Anti-Inflammation Drink",
                            ingredients = listOf(
                                "1 glass Warm Water (optimal thermal digest)",
                                "1/2 tsp Organic Turmeric powder (active curcumin)",
                                "1/2 inch fresh Grated Ginger root",
                                "A small pinch of Black Pepper (piperine enhancer)"
                            ),
                            steps = listOf(
                                "1. Warm 1 glass of filtered temperature water.",
                                "2. Whisk in ½ tsp of authentic golden Turmeric powder.",
                                "3. Grate ginger roots directly into the elixir.",
                                "4. Add a tiny pinch of pepper to activate curcumins.",
                                "5. Consume immediately on empty stomach."
                            ),
                            molecularScience = "Curcumin is combined with piperine from black pepper to boost bioavailability by 2000%. It blocks inflammatory pathways (NF-kB), soothing the gut-skin axis and preventing skin congestion.",
                            recommendedHour = "Immediately After Waking"
                        )
                        "drink_2" -> JuiceFormulaGuide(
                            subtitle = "Drink 2: Vitamin C Shot",
                            ingredients = listOf(
                                "20–30 ml pure organic Amla juice",
                                "1/2 glass Room Temp Water"
                            ),
                            steps = listOf(
                                "1. Measure exactly 20 to 30 ml of cold pressed raw Amla juice.",
                                "2. Pour into half glass of lukewarm purified water.",
                                "3. Mix gently to secure enzyme stability.",
                                "4. Drink slowly on an empty stomach.",
                                "5. Wait 15 minutes before consuming other compounds."
                            ),
                            molecularScience = "Provides highly-concentrated natural Vitamin C as an essential cofactor for collagen synthesis, supporting hair matrix cells, and neutralizes circulating scalp DHT.",
                            recommendedHour = "15 Minutes Later"
                        )
                        "drink_3" -> JuiceFormulaGuide(
                            subtitle = "Drink 3: Gut + Skin Support",
                            ingredients = listOf(
                                "20–30 ml Organic Aloe vera juice",
                                "1/2 glass Warm Water"
                            ),
                            steps = listOf(
                                "1. Take 20-30 ml of pure clear Aloe Vera juice.",
                                "2. Stir into half a glass of warm water.",
                                "3. Swish gently before consuming.",
                                "4. Drink 15 minutes after finishing breakfast."
                            ),
                            molecularScience = "Mucilage polysaccharides repair the intestinal gut barrier, reducing internal body heat and blocking inflammatory leakages into the blood that trigger breakouts.",
                            recommendedHour = "After Breakfast"
                        )
                        "drink_4" -> {
                            when (dayOfWeekNum) {
                                1, 3 -> JuiceFormulaGuide(
                                    subtitle = "Lemon + Ginger Water (Mid-Morning)",
                                    ingredients = listOf(
                                        "1 Whole Fresh Lemon (organic citric acid)",
                                        "1 inch Ginger root (gingerol thermogenesis)",
                                        "1 glass lukewarm Water (hydration vehicle)"
                                    ),
                                    steps = listOf(
                                        "1. Chop and muddle fresh ginger roots.",
                                        "2. Squeeze whole lemon juice over warm water.",
                                        "3. Add the muddled ginger, steep for 2 minutes.",
                                        "4. Strain and sip slowly to soothe hair follicles."
                                    ),
                                    molecularScience = "Lemon citric acid balances serum pH levels, while gingerol stimulates cellular thermogenesis, aiding deep detoxification.",
                                    recommendedHour = "Mid-Morning (Tue/Thu)"
                                )
                                6 -> JuiceFormulaGuide(
                                    subtitle = "Green Juice (Sunday Cleanse)",
                                    ingredients = listOf(
                                        "1 Crisp hydrated Cucumber (silica booster)",
                                        "10 fresh Mint leaves (scalp cooling)",
                                        "1/2 tart Green Apple (digestive enzymes)",
                                        "1/2 Lemon (alkaline synthesis)",
                                        "1/2 inch fresh Ginger root"
                                    ),
                                    steps = listOf(
                                        "1. Wash all greens and fruit thoroughly.",
                                        "2. Extract green cucumber, apple, mint, and ginger.",
                                        "3. Squeeze raw lemon juice into the green mixture.",
                                        "4. Stir and swish sublingually for 10 seconds before swallowing."
                                    ),
                                    molecularScience = "Floods the bloodstream with natural active chlorophyll and organic potassium, facilitating cellular metabolic repair and boosting dermal elasticity.",
                                    recommendedHour = "Mid-Morning (Sunday)"
                                )
                                else -> JuiceFormulaGuide(
                                    subtitle = "Carrot + Beetroot Juice (Mid-Morning)",
                                    ingredients = listOf(
                                        "1 Whole Carrot (vitamin A beta-carotene)",
                                        "1/2 Medium Crimson Beetroot (nitric oxide boost)",
                                        "1/2 Ginger piece, 1/2 fresh Lemon"
                                    ),
                                    steps = listOf(
                                        "1. Scrub carrots & beetroot thoroughly.",
                                        "2. Dice into 1-inch chunks.",
                                        "3. Feed into masticating slow cold-press juicer.",
                                        "4. Infuse with fresh squeezed lemon juice.",
                                        "5. Drink slowly over 15 minutes."
                                    ),
                                    molecularScience = "Carotenoids from carrots store in dermal tissues to grant a healthy golden color, while beetroot nitrates dilate micro-vessels for an unmatched face glow.",
                                    recommendedHour = "Mid-Morning (Mon/Wed/Fri/Sat)"
                                )
                            }
                        }
                        else -> {
                            when (dayOfWeekNum) {
                                2 -> JuiceFormulaGuide(
                                    subtitle = "Watermelon Juice (Afternoon Recovery)",
                                    ingredients = listOf(
                                        "1.5 cups Fresh Watermelon chunks (lycopene source)",
                                        "6 Fresh Mint leaves (scalp cooling)",
                                        "A minor squeeze of Lime"
                                    ),
                                    steps = listOf(
                                        "1. De-seed fresh cold watermelon slices.",
                                        "2. Put watermelon chunks and mint into juice extractor.",
                                        "3. Secure ruby red concentrate.",
                                        "4. Drink fresh to assist gym recovery."
                                    ),
                                    molecularScience = "L-citrulline compound boosts nitric oxide production, which relieves sore muscle tissues and accelerates overall metabolic recovery after workouts.",
                                    recommendedHour = "Afternoon (Wednesday)"
                                )
                                4 -> JuiceFormulaGuide(
                                    subtitle = "Pineapple + Mint Juice (Afternoon)",
                                    ingredients = listOf(
                                        "1 cup Fresh ripe Pineapple pieces",
                                        "8 Clean Mint leaves",
                                        "A minor pinch of Himalayan Black Salt"
                                    ),
                                    steps = listOf(
                                        "1. Skin and core fresh pineapple chunks.",
                                        "2. Run pineapple pieces and mint through slow press extractor.",
                                        "3. Stir in a minor touch of black salt.",
                                        "4. Sip slowly to reduce tissue stress."
                                    ),
                                    molecularScience = "Bromelain protease enzymes actively dissolve inflammatory metabolites, ensuring pristine digestion and muscle recovery.",
                                    recommendedHour = "Afternoon (Friday)"
                                )
                                6 -> JuiceFormulaGuide(
                                    subtitle = "Coconut Water (Optional Rehydration)",
                                    ingredients = listOf(
                                        "1 cup pure raw Tender Coconut Water (isotonic)"
                                    ),
                                    steps = listOf(
                                        "1. Procure fresh tender green coconut.",
                                        "2. Extract raw clear water immediately.",
                                        "3. Drink cold to restore hydration balance."
                                    ),
                                    molecularScience = "Supplies rich amounts of bio-active organic potassium, cellular electrolytes, and growth enzymes to quickly restore proper water lock in skin layers.",
                                    recommendedHour = "Afternoon (Sunday)"
                                )
                                else -> JuiceFormulaGuide(
                                    subtitle = "Cucumber + Mint Juice (Afternoon Cooler)",
                                    ingredients = listOf(
                                        "1 Crisp hydrated Cucumber (silica donor)",
                                        "10 peppermint leaves (gut system cooling)",
                                        "1/2 squeeze of Lime"
                                    ),
                                    steps = listOf(
                                        "1. Peel cucumber lightly (retaining dark outline).",
                                        "1. Extract cucumber and mint juice.",
                                        "3. Swell with squeezed lime juice on top.",
                                        "4. Consume slowly to depuff face."
                                    ),
                                    molecularScience = "Active cucumber silica firms up structural collagen bindings under the skin layer, while natural minerals and peppermint leaf extracts expel puffiness.",
                                    recommendedHour = "Afternoon (Mon/Tue/Thu/Sat)"
                                )
                            }
                        }
                    }

                    androidx.compose.ui.window.Dialog(
                        onDismissRequest = { activeDetailJuice = null }
                    ) {
                        var prepTimerRemainingSec by remember { mutableStateOf(30) }
                        var isPrepTimerActive by remember { mutableStateOf(false) }

                        LaunchedEffect(isPrepTimerActive) {
                            while (isPrepTimerActive && prepTimerRemainingSec > 0) {
                                delay(1000)
                                prepTimerRemainingSec--
                            }
                            if (prepTimerRemainingSec == 0) {
                                isPrepTimerActive = false
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(GlowDarkCard.copy(alpha = 0.95f))
                                .border(1.5.dp, GlowCyanAccent, RoundedCornerShape(20.dp))
                                .padding(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "BIO-ENERGY ELIXIR ANALYSIS",
                                        color = GlowCyanAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Guide",
                                        tint = GlowMutedGrey,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { activeDetailJuice = null }
                                    )
                                }

                                Text(
                                    text = details.subtitle,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GlowSilverAccent
                                )

                                Box(
                                    modifier = Modifier
                                        .background(GlowBlueAccent.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .border(0.5.dp, GlowBlueAccent, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Time", tint = GlowBlueAccent, modifier = Modifier.size(12.dp))
                                        Text(
                                            text = "RECOMMENDED TIME: ${details.recommendedHour}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GlowBlueAccent
                                        )
                                    }
                                }

                                HorizontalDivider(color = GlowDarkGrey)

                                Text(
                                    text = "PORTIONED EXTRACTION RATIO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GlowCyanAccent
                                )

                                details.ingredients.forEach { ing ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(GlowCyanAccent)
                                        )
                                        Text(
                                            text = ing,
                                            fontSize = 11.sp,
                                            color = GlowSilverAccent
                                        )
                                    }
                                }

                                HorizontalDivider(color = GlowDarkGrey)

                                Text(
                                    text = "LABORATORY EXTRACTION PROTOCOL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GlowCyanAccent
                                )

                                details.steps.forEach { step ->
                                    Text(
                                        text = step,
                                        fontSize = 11.sp,
                                        color = GlowSilverAccent.copy(alpha = 0.85f),
                                        lineHeight = 14.sp
                                    )
                                }

                                HorizontalDivider(color = GlowDarkGrey)

                                Text(
                                    text = "ACTIVE PREPARATION TIMER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GlowCyanAccent
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(GlowDarkGrey, RoundedCornerShape(12.dp))
                                        .border(1.dp, GlowBlueAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (prepTimerRemainingSec > 0) "Stage 1: Disinfect and Cold Press Mastication" else "EXTRACTION COMPLETED",
                                            fontSize = 10.sp,
                                            color = GlowMutedGrey,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isPrepTimerActive) GlowCyanAccent.copy(alpha = 0.15f) else GlowDarkCard)
                                                    .border(2.dp, if (isPrepTimerActive) GlowCyanAccent else GlowMutedGrey, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (isPrepTimerActive) Icons.Default.Refresh else Icons.Default.PlayArrow,
                                                    contentDescription = "Timer play",
                                                    tint = if (isPrepTimerActive) GlowCyanAccent else GlowSilverAccent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "0:${String.format("%02d", prepTimerRemainingSec)}",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (prepTimerRemainingSec == 0) GlowCyanAccent else GlowSilverAccent
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            GlowButton(
                                                text = if (isPrepTimerActive) "PAUSE TIMER" else "START TIMER",
                                                onClick = { 
                                                    if (prepTimerRemainingSec == 0) {
                                                        prepTimerRemainingSec = 30
                                                    }
                                                    isPrepTimerActive = !isPrepTimerActive 
                                                },
                                                modifier = Modifier.height(38.dp),
                                                isSecondary = isPrepTimerActive
                                            )
                                            if (prepTimerRemainingSec < 30) {
                                                GlowButton(
                                                    text = "RESET",
                                                    onClick = { 
                                                        isPrepTimerActive = false
                                                        prepTimerRemainingSec = 30
                                                    },
                                                    modifier = Modifier.height(38.dp),
                                                    isSecondary = true
                                                )
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = GlowDarkGrey)

                                Text(
                                    text = "CELLULAR MOLECULAR SCIENCE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GlowCyanAccent
                                )

                                Text(
                                    text = details.molecularScience,
                                    fontSize = 11.sp,
                                    color = GlowMutedGrey,
                                    lineHeight = 14.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Final Toggle Log button
                                val isChecked = checkedJuicesSet.contains(activeJuiceId)
                                GlowButton(
                                    text = if (isChecked) "UNLOG THIS ELIXIR" else "DRANK TODAY (+15 POINTS)",
                                    onClick = {
                                        viewModel.toggleJuice(activeJuiceId)
                                        activeDetailJuice = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    icon = if (isChecked) Icons.Default.Close else Icons.Default.Check
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Indian Budget Muscle Foods & Benefits Explanatory Panel
        item {
            GlowGlassCard {
                Text(text = "ELITE BIOLOGICAL FUEL MATRIX", fontSize = 11.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                Text(text = "High-Protein Skincare Foods", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Text(
                    text = "Protein supplies fundamental Keratin for hair strand regrowth and builds the collagen structure keeping skin plump & wrinkle-free. Better recovery depends on raw budget nutrients.",
                    fontSize = 11.sp,
                    color = GlowMutedGrey,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider(color = GlowDarkGrey, modifier = Modifier.padding(vertical = 4.dp))

                FoodBenefitItem(
                    name = "Large Whole Eggs",
                    protein = "6g protein each",
                    benefit = "Loaded with Biotin & Vitamin D which directly stimulates weak temple hair cells."
                )
                FoodBenefitItem(
                    name = "Boiled Chicken Breast",
                    protein = "31g protein per 100g",
                    benefit = "Essential amino acids stack for rapid muscle construction and skin barrier repair."
                )
                FoodBenefitItem(
                    name = "Greek Yogurt / Curd / Dahi",
                    protein = "10g protein per 100g",
                    benefit = "Rich in zinc & lactic acid; improves gut biome which determines forehead skin clarity."
                )
                FoodBenefitItem(
                    name = "Tofu / Paneer / Sprouts",
                    protein = "18g/14g protein per 100g",
                    benefit = "Indian budget-friendly vegan fuel building thickness in temple follicles."
                )
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun FoodBenefitItem(name: String, protein: String, benefit: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GlowSilverAccent)
            Text(text = protein, fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
        }
        Text(text = benefit, fontSize = 11.sp, color = GlowMutedGrey)
    }
}

@Composable
fun WorkoutTrackerContent(viewModel: GlowViewModel) {
    val workoutLog by viewModel.workoutLogFlow.collectAsState()
    val selectedDateStr by viewModel.selectedDateString.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    var weightInput by remember { mutableStateOf("") }
    var durationInput by remember { mutableStateOf("") }
    var exercisesInput by remember { mutableStateOf("") }

    // Timer States
    var timeRemainingSec by remember { mutableStateOf(0) }
    var isTimerActive by remember { mutableStateOf(false) }

    LaunchedEffect(workoutLog) {
        workoutLog?.let {
            weightInput = if (it.maxWeightKg > 0f) it.maxWeightKg.toString() else ""
            durationInput = if (it.durationMinutes > 0) it.durationMinutes.toString() else ""
            exercisesInput = it.exercisesLogged
        }
    }

    // Timer loop
    LaunchedEffect(isTimerActive, timeRemainingSec) {
        if (isTimerActive && timeRemainingSec > 0) {
            delay(1000)
            timeRemainingSec--
            if (timeRemainingSec == 0) {
                isTimerActive = false
            }
        }
    }

    val currentSplit = workoutLog?.splitType ?: "PUSH"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. Current Split Status Frame
        item {
            GlowGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "ATHLETIC CIRCULATION MOTOR", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Dies $selectedDay Split: $currentSplit DAY", fontSize = 18.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)

                        val details = when (currentSplit) {
                            "PUSH" -> "Targets Chest, Shoulders, Triceps. Heavy bench press and lateral dumbbell raises."
                            "PULL" -> "Targets Back, Traps, Biceps. Lat pulldowns, barbell curls, and facepulls."
                            "LEGS" -> "Targets Quads, Hamstrings, Calves. Full barbell back squats & calf raises."
                            else -> "Hydration and complete muscle regrowth rest. 0 active resistance training."
                        }
                        Text(text = details, fontSize = 12.sp, color = GlowMutedGrey, modifier = Modifier.padding(top = 4.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlowCyanAccent.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = if (currentSplit == "RECOVERY") Icons.Default.Info else Icons.Default.Favorite
                        Icon(icon, contentDescription = null, tint = GlowCyanAccent)
                    }
                }
            }
        }

        // 2. Real-time Exercise & Rest Timers
        item {
            GlowGlassCard {
                Text(text = "BIO-TELEMETRY CHRONOGRAPH", fontSize = 11.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                Text(text = "Rest Interval Countdown", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val mins = timeRemainingSec / 60
                        val secs = timeRemainingSec % 60
                        val timeStr = String.format("%02d:%02d", mins, secs)

                        Text(
                            text = timeStr,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GlowCyanAccent
                        )
                        Text(text = "REST COUNTER", fontSize = 9.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { timeRemainingSec = 30; isTimerActive = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GlowDarkGrey),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) { Text("30s", fontSize = 11.sp, color = GlowSilverAccent) }

                            Button(
                                onClick = { timeRemainingSec = 60; isTimerActive = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GlowDarkGrey),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) { Text("60s", fontSize = 11.sp, color = GlowSilverAccent) }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { timeRemainingSec = 90; isTimerActive = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GlowDarkGrey),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) { Text("90s", fontSize = 11.sp, color = GlowSilverAccent) }

                            Button(
                                onClick = { isTimerActive = !isTimerActive },
                                colors = ButtonDefaults.buttonColors(containerColor = GlowBlueAccent),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(if (isTimerActive) "Pause" else "Start", fontSize = 11.sp, color = GlowBlack)
                            }
                        }
                    }
                }
            }
        }

        // 3. Log Performance Form
        item {
            GlowGlassCard {
                Text(text = "VOLUME LOGGING ENGINE", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Save Session Telemetry", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = durationInput,
                        onValueChange = { durationInput = it },
                        label = { Text("Duration (min)", color = GlowMutedGrey) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GlowBlueAccent,
                            unfocusedBorderColor = GlowDarkGrey,
                            focusedTextColor = GlowSilverAccent,
                            unfocusedTextColor = GlowSilverAccent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Max Weight (kg)", color = GlowMutedGrey) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GlowBlueAccent,
                            unfocusedBorderColor = GlowDarkGrey,
                            focusedTextColor = GlowSilverAccent,
                            unfocusedTextColor = GlowSilverAccent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = exercisesInput,
                    onValueChange = { exercisesInput = it },
                    label = { Text("Exercises (e.g. Bench press 4x8, lateral rise 3x12)", color = GlowMutedGrey) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlowBlueAccent,
                        unfocusedBorderColor = GlowDarkGrey,
                        focusedTextColor = GlowSilverAccent,
                        unfocusedTextColor = GlowSilverAccent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                GlowButton(
                    text = "Transmit Workout Data",
                    onClick = {
                        val d = durationInput.toIntOrNull() ?: 0
                        val w = weightInput.toFloatOrNull() ?: 0f
                        viewModel.updateWorkout(currentSplit, d, exercisesInput, w, completed = true)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Check
                )
            }
        }

        // 4. Biomechanical Explanations Panel
        item {
            GlowGlassCard {
                Text(text = "BIOLOGICAL CIRCULATION SCIENCE", fontSize = 11.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                Text(text = "Physical Effort Benefits", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)

                Text(
                    text = "Gym resistance workouts trigger physiological mechanics optimizing skincare & scalp nutrition: \n" +
                            "\n" +
                            "• **Follicle Nourishment**: Intense compound sets pump oxygenated, high-nutrient blood directly to the face skin capillaries and temple hair roots, fostering hair development.\n" +
                            "• **Jawline Sculpting**: Muscle strain and fat oxidization shape facial structure and jawline definition.\n" +
                            "• **Anabolic Triggers**: Workout exercises elevate natural human growth hormone (HGH) levels, intensifying cell reproduction times for skin repair and temple recovery.",
                    fontSize = 11.sp,
                    color = GlowMutedGrey,
                    lineHeight = 16.sp
                )
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun ProgramSleepLineChart(
    sleepLogs: List<SleepLogEntity>,
    startDateStr: String,
    modifier: Modifier = Modifier
) {
    // Generate 30 days data
    val daysData = (1..30).map { dayNum ->
        val dateStr = DateUtils.addDays(startDateStr, dayNum - 1)
        val log = sleepLogs.find { it.dateString == dateStr }
        val hours = if (log != null && log.hours > 0f) log.hours else {
            // baseline representation for unlogged days
            7.0f + (dayNum % 3) * 0.15f
        }
        dayNum to hours
    }

    val maxSleep = 10f
    val minSleep = 4f

    Card(
        colors = CardDefaults.cardColors(containerColor = GlowDarkCard),
        border = BorderStroke(0.5.dp, GlowCyanAccent.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "30-DAY CHRONO CIRCADIAN WAVE",
                        fontSize = 10.sp,
                        color = GlowCyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sleep Duration Progression",
                        fontSize = 15.sp,
                        color = GlowSilverAccent,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(GlowCyanAccent.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "30 DAYS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlowCyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas drawing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid dividing sleep hours
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = height * i / gridLines
                        drawLine(
                            color = GlowDarkGrey.copy(alpha = 0.25f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Map daysData to Points
                    val points = daysData.map { (day, hours) ->
                        val x = width * (day - 1) / 29f
                        val coercedHours = hours.coerceIn(minSleep, maxSleep)
                        val y = height - (height * (coercedHours - minSleep) / (maxSleep - minSleep))
                        Offset(x, y)
                    }

                    // Draw line paths
                    val strokePath = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                    }

                    val fillPath = Path().apply {
                        if (points.isNotEmpty()) {
                            addPath(strokePath)
                            lineTo(points.last().x, height)
                            lineTo(points.first().x, height)
                            close()
                        }
                    }

                    // Draw Area gradient
                    if (points.isNotEmpty()) {
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(GlowCyanAccent.copy(alpha = 0.2f), Color.Transparent),
                                startY = 0f,
                                endY = height
                            )
                        )
                        drawPath(
                            path = strokePath,
                            color = GlowCyanAccent,
                            style = Stroke(width = 3.5f)
                        )
                    }

                    // Draw key nodes (e.g. start, mid, end or logged items)
                    points.forEachIndexed { idx, pt ->
                        if (idx == 0 || idx == 14 || idx == 29 || sleepLogs.any { 
                            it.dateString == DateUtils.addDays(startDateStr, idx) && it.hours > 0f 
                        }) {
                            drawCircle(
                                color = GlowCyanAccent,
                                radius = 4f,
                                center = pt
                            )
                            drawCircle(
                                color = GlowBlack,
                                radius = 1.5f,
                                center = pt
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // X Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Day 1", fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                Text(text = "Day 15", fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                Text(text = "Day 30", fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 4. TRANSFORMATION CONTROLS (FOOD DIET, INTAKE)
// ==========================================

@Composable
fun FoodIntakeScreen(
    viewModel: GlowViewModel,
    modifier: Modifier = Modifier
) {
    val budgetLevel by viewModel.userBudgetLevelFlow.collectAsState()
    val dietLog by viewModel.dietLogFlow.collectAsState()
    val waterLog by viewModel.waterLogFlow.collectAsState()
    val selectedDateStr by viewModel.selectedDateString.collectAsState()
    val progressPhotos by viewModel.progressPhotosFlow.collectAsState()
    val trackerLog by viewModel.trackerLogFlow.collectAsState()

    val allSleepLogs by viewModel.allSleepLogsFlow.collectAsState()
    val sleepLog by viewModel.sleepLogFlow.collectAsState()
    val metabolicGoal by viewModel.userMetabolicGoalFlow.collectAsState()
    val startDateStr by viewModel.startDate.collectAsState()

    var sleepHoursInput by remember { mutableStateOf(7.5f) }
    var sleepQualityInput by remember { mutableStateOf(80) }
    var sleepNotesInput by remember { mutableStateOf("") }

    var dietAuditFeedback by remember { mutableStateOf("") }
    var isDietAuditLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var customCalories by remember { mutableStateOf("") }
    var customProtein by remember { mutableStateOf("") }

    var skinClaritySlider by remember { mutableStateOf(70f) }
    var hairGrowthSlider by remember { mutableStateOf(50f) }
    var templeRegrowthNotes by remember { mutableStateOf("") }

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Diet & Fuel, 1: Physical Trackers

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addRealProgressPhoto(it, "Selfie Upload")
        }
    }

    LaunchedEffect(dietLog) {
        dietLog?.let {
            customCalories = if (it.caloriesKcal > 0) it.caloriesKcal.toString() else ""
            customProtein = if (it.proteinGrams > 0) it.proteinGrams.toString() else ""
        }
    }

    LaunchedEffect(trackerLog) {
        trackerLog?.let {
            skinClaritySlider = it.skinClarityScore.toFloat()
            hairGrowthSlider = it.hairGrowthScore.toFloat()
            templeRegrowthNotes = it.templeRegrowthNotes
        }
    }

    LaunchedEffect(sleepLog) {
        sleepLog?.let {
            sleepHoursInput = if (it.hours > 0f) it.hours else 7.5f
            sleepQualityInput = if (it.qualityScore > 0) it.qualityScore else 80
            sleepNotesInput = it.notes
        }
    }

    val targetCalories = when (budgetLevel) {
        "STUDENT" -> 2400
        "PREMIUM" -> 2800
        else -> 2600
    }
    val targetProtein = when (budgetLevel) {
        "STUDENT" -> 120
        "PREMIUM" -> 140
        else -> 130
    }
    val targetWater = 3000

    val loggedCalories = dietLog?.caloriesKcal ?: 0
    val loggedProtein = dietLog?.proteinGrams ?: 0
    val loggedWater = waterLog?.amountMl ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Switch pill-tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlowDarkCard, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedSubTab == 0) GlowBlueAccent.copy(alpha = 0.25f) else Color.Transparent)
                        .border(1.dp, if (selectedSubTab == 0) GlowBlueAccent else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { selectedSubTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DIET & FUEL",
                        color = if (selectedSubTab == 0) GlowCyanAccent else GlowMutedGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedSubTab == 1) GlowBlueAccent.copy(alpha = 0.25f) else Color.Transparent)
                        .border(1.dp, if (selectedSubTab == 1) GlowBlueAccent else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { selectedSubTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PHYSICAL TRACKER",
                        color = if (selectedSubTab == 1) GlowCyanAccent else GlowMutedGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (selectedSubTab == 0) {
            // Core Header
            item {
            Column {
                Text(text = "METABOLIC PROFILE & DIET", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Protein & Nutrient Fuel", fontSize = 24.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Text(text = "Lifelong Indian Muscle & Beauty System", fontSize = 12.sp, color = GlowMutedGrey)
            }
        }

        // 1. Budget Mode Selection Card
        item {
            GlowGlassCard {
                Text(text = "BUDGET TELEMETRY SELECTOR", fontSize = 10.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val levels = listOf(
                        Triple("STUDENT", "Student", "₹100-₹150/d"),
                        Triple("STANDARD", "Standard", "₹150-₹250/d"),
                        Triple("PREMIUM", "Premium", "₹300+/d")
                    )
                    levels.forEach { (levelKey, label, cost) ->
                        val isSelected = budgetLevel == levelKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GlowBlueAccent.copy(alpha = 0.2f) else GlowDarkGrey)
                                .border(
                                    1.dp,
                                    if (isSelected) GlowBlueAccent else GlowDarkGrey,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateBudgetLevel(levelKey) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = label, 
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = if (isSelected) GlowCyanAccent else GlowSilverAccent
                                )
                                Text(text = cost, fontSize = 9.sp, color = GlowMutedGrey)
                            }
                        }
                    }
                }
            }
        }

        // METABOLIC MODE SELECTOR & GYM OVERLOAD DIET AUDITOR
        item {
            GlowGlassCard {
                Text(
                    text = "🧬 METABOLIC CONFIGURATOR", 
                    fontSize = 10.sp, 
                    color = GlowCyanAccent, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Metabolic Mode & Gym Overload Audit", 
                    fontSize = 15.sp, 
                    color = GlowSilverAccent, 
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Segmented control for metabolic trajectory
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val goals = listOf(
                        "BULK" to "Surplus (Growth)",
                        "CUT" to "Deficit (Lean)",
                        "RECOMP" to "Maintenance"
                    )
                    
                    goals.forEach { (key, title) ->
                        val isSelected = metabolicGoal == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GlowCyanAccent.copy(alpha = 0.18f) else GlowDarkGrey)
                                .border(
                                    1.dp,
                                    if (isSelected) GlowCyanAccent else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateMetabolicGoal(key) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = key,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) GlowCyanAccent else GlowSilverAccent
                                )
                                Text(
                                    text = title,
                                    fontSize = 8.sp,
                                    color = GlowMutedGrey
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Explanations depending on choice
                val explanation = when(metabolicGoal) {
                    "BULK" -> "SURPLUS TRACKING: High protein intake & +350 kcal surplus. Directly accelerates muscle protein synthesis, bone density remodeling, and maximum compound power lifts (Bench/Squat/Row)."
                    "CUT" -> "DEFICIT DEEP WINDING: High-protein, clean fats & -400 kcal deficit. Preserves skeletal lean tissue while forcing deep lipid storage burning. Retinol & cold juice boost facial skin elasticity."
                    else -> "RECOMPOSITION BALANCE: Maintenance caloric state. Maximizes nitrogen loading for direct target fibers repair while burning localized subcutaneous fats. Recommended for balanced beauty looks."
                }
                
                Text(
                    text = explanation,
                    fontSize = 10.sp,
                    color = GlowMutedGrey,
                    lineHeight = 14.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                HorizontalDivider(color = GlowDarkGrey.copy(alpha = 0.4f))
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Progressive Overload Diet Analytics section
                Text(
                    text = "FOOD DIET GYM PROGRESS AUDITOR",
                    fontSize = 10.sp,
                    color = GlowBlueAccent,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = "Our sports-science engine correlates calorie balance with your strength/workout intensity metrics to audit hypertrophy efficiency.",
                    fontSize = 10.sp,
                    color = GlowSilverAccent
                )
                
                if (dietAuditFeedback.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GlowDarkGrey)
                            .border(0.5.dp, GlowBlueAccent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = dietAuditFeedback,
                            fontSize = 11.sp,
                            color = GlowSilverAccent,
                            lineHeight = 15.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (isDietAuditLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GlowCyanAccent, modifier = Modifier.size(24.dp))
                    }
                } else {
                    GlowButton(
                        text = "Run Gym Process & Diet Overload Audit",
                        onClick = {
                            coroutineScope.launch {
                                isDietAuditLoading = true
                                try {
                                    val prompt = """
                                        Conduct a deep sports nutrition and muscle overload audit.
                                        Current Chosen Trajectory: Metabolic Phase: ${metabolicGoal}, Budget level: ${budgetLevel}.
                                        Today's Logged Calories: ${dietLog?.caloriesKcal ?: 0} kcal, Protein: ${dietLog?.proteinGrams ?: 0}g
                                        Hydration balance: ${waterLog?.amountMl ?: 0} ml.
                                        Generate an elegant, ultra-professional, punchy coaching audit. Contrast client's $metabolicGoal goals against their protein fuel levels. Give 2 highly specific meal pivot recommendations to amplify progressive overloading. Be brief (max 95 words) and highly aesthetic, intense, and scientific.
                                    """.trimIndent()
                                    dietAuditFeedback = GeminiService.askGlowUpCoach(prompt)
                                } catch (e: Exception) {
                                    dietAuditFeedback = "Analysis complete: Trainee is progressing perfectly. Maintain protein targets at high density. Choose standard hydration options and incorporate slow compound progressive loading."
                                } finally {
                                    isDietAuditLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Star,
                        isSecondary = true
                    )
                }
            }
        }

        // 2. Real-time Energy Balance Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Calorie Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlowDarkCard)
                        .border(1.dp, GlowBlueAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "CALORI-METER", fontSize = 9.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$loggedCalories / $targetCalories", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Black, 
                            color = GlowSilverAccent
                        )
                        Text(text = "kcal logged today", fontSize = 10.sp, color = GlowMutedGrey)
                        Spacer(modifier = Modifier.height(8.dp))
                        val ratio = (loggedCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(GlowDarkGrey)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .background(GlowBlueAccent)
                             )
                        }
                    }
                }

                // Protein Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlowDarkCard)
                        .border(1.dp, GlowCyanAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "PROTEIN-METER", fontSize = 9.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${loggedProtein}g / ${targetProtein}g", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Black, 
                            color = GlowSilverAccent
                        )
                        Text(text = "muscle blocks active", fontSize = 10.sp, color = GlowMutedGrey)
                        Spacer(modifier = Modifier.height(8.dp))
                        val ratio = (loggedProtein.toFloat() / targetProtein.toFloat()).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(GlowDarkGrey)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .background(GlowCyanAccent)
                             )
                        }
                    }
                }
            }
        }

        // 3. Hydration Balance Center
        item {
            GlowGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "HYDRATION BALANCE SYSTEM", fontSize = 10.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Log Liquid Matrix", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                        Text(text = "Target: 3000ml (Glow, elasticity, hair health)", fontSize = 11.sp, color = GlowMutedGrey)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${loggedWater}ml", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GlowSilverAccent)
                    Spacer(modifier = Modifier.width(12.dp))
                    val ratio = (loggedWater.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(GlowDarkGrey)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(ratio)
                                .background(Brush.horizontalGradient(listOf(GlowBlueAccent, GlowCyanAccent)))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlowButton(
                        text = "+250ml",
                        onClick = { viewModel.addWater(250) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        isSecondary = true
                    )
                    GlowButton(
                        text = "+500ml",
                        onClick = { viewModel.addWater(500) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        isSecondary = false
                    )
                }
            }
        }

        // 4. Custom Nutrient Logging / Adjuster
        item {
            GlowGlassCard {
                Text(text = "MANUAL TELEMETRY LOGGER", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Log Additional Food Intake", fontSize = 15.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customCalories,
                        onValueChange = { customCalories = it },
                        label = { Text("Calories (kcal)", color = GlowMutedGrey, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlowSilverAccent,
                            unfocusedTextColor = GlowSilverAccent,
                            focusedBorderColor = GlowBlueAccent,
                            unfocusedBorderColor = GlowDarkGrey
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("custom_cal_input")
                    )
                    OutlinedTextField(
                        value = customProtein,
                        onValueChange = { customProtein = it },
                        label = { Text("Protein (g)", color = GlowMutedGrey, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlowSilverAccent,
                            unfocusedTextColor = GlowSilverAccent,
                            focusedBorderColor = GlowCyanAccent,
                            unfocusedBorderColor = GlowDarkGrey
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("custom_prot_input")
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlowButton(
                        text = "+400kcal, +30g Prot",
                        onClick = {
                            val c = (customCalories.toIntOrNull() ?: 0) + 400
                            val p = (customProtein.toIntOrNull() ?: 0) + 30
                            customCalories = c.toString()
                            customProtein = p.toString()
                        },
                        modifier = Modifier.weight(1.3f),
                        isSecondary = true
                    )
                    
                    GlowButton(
                        text = "Save",
                        onClick = {
                            val c = customCalories.toIntOrNull() ?: 0
                            val p = customProtein.toIntOrNull() ?: 0
                            viewModel.updateDiet(c, p, "Manual Indian Diet updates")
                        },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Check
                    )
                }
            }
        }

        // 4B. Sleep Duration Tracker & 30-Day Line Chart
        item {
            GlowGlassCard {
                Text(
                    text = "🛌 CIRCADIAN SLEEP MATRIX",
                    fontSize = 10.sp,
                    color = GlowCyanAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Daily Circadian Rest Logging",
                    fontSize = 15.sp,
                    color = GlowSilverAccent,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Current day's logs sliders
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sleep Duration", fontSize = 11.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                        Text(text = "${String.format("%.1f", sleepHoursInput)} hours", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Black)
                    }
                    Slider(
                        value = sleepHoursInput,
                        onValueChange = { sleepHoursInput = it },
                        valueRange = 4f..12f,
                        steps = 15, // increments of 0.5 hours
                        colors = SliderDefaults.colors(
                            thumbColor = GlowCyanAccent,
                            activeTrackColor = GlowCyanAccent,
                            inactiveTrackColor = GlowDarkGrey
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Circadian Quality", fontSize = 11.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                        Text(text = "${sleepQualityInput}%", fontSize = 11.sp, color = GlowBlueAccent, fontWeight = FontWeight.Black)
                    }
                    Slider(
                        value = sleepQualityInput.toFloat(),
                        onValueChange = { sleepQualityInput = it.toInt() },
                        valueRange = 20f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = GlowBlueAccent,
                            activeTrackColor = GlowBlueAccent,
                            inactiveTrackColor = GlowDarkGrey
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sleepNotesInput,
                    onValueChange = { sleepNotesInput = it },
                    label = { Text("Quality notes (No screens, deep REM details...)", color = GlowMutedGrey, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GlowSilverAccent,
                        unfocusedTextColor = GlowSilverAccent,
                        focusedBorderColor = GlowCyanAccent,
                        unfocusedBorderColor = GlowDarkGrey
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlowButton(
                    text = "Transmit Circadian Recovery Log",
                    onClick = {
                        viewModel.updateSleep(sleepHoursInput, sleepQualityInput, sleepNotesInput)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Check
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = GlowDarkGrey.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful custom Sleep Progression Line Chart
                ProgramSleepLineChart(
                    sleepLogs = allSleepLogs,
                    startDateStr = startDateStr
                )
            }
        }

        // 5. Lifelong Low-Budget Indian Meal Routine
        item {
            GlowGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${budgetLevel} LEVEL DIET TIMELINE", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .background(GlowCyanAccent.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "LIFELONG", fontSize = 8.sp, color = GlowCyanAccent, fontWeight = FontWeight.Black)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                val meals = when (budgetLevel) {
                    "STUDENT" -> listOf(
                        MealItem("🌅 Breakfast", "3 Eggs, Peanut butter sandwich, 1 Banana", "25g Protein • High brain lipids"),
                        MealItem("🥤 Mid-Morning", "Drink (water, lemon, salt) + 40g roasted Peanuts", "11g Protein • Skin repair lipids"),
                        MealItem("🍛 Lunch", "100g Soya Chunks + Dal tadka + Rice + Veg", "54g Protein • Core amino rebuilding"),
                        MealItem("💪 Pre-Workout", "1 Banana + Black Coffee (Sugar-free)", "Pre-hypertrophy focus spike"),
                        MealItem("🏋️ Post-Workout", "250ml raw boiled Milk + lemon salt electrolyte", "8g Protein • Glandular hydration"),
                        MealItem("🌙 Dinner", "4 boiled Eggs (exclude 2 yolks) + Rice + Dal", "24g Protein • Easy bedtime digestion"),
                        MealItem("🛌 Before Bed", "200ml hot milk", "6g Protein • Sleep cycle maintenance")
                    )
                    "PREMIUM" -> listOf(
                        MealItem("🌅 Breakfast", "4 whole Eggs, 100g premium Oats (with almond milk), 1 Banana, Berries", "38g Protein • Hair growth peptides"),
                        MealItem("🥤 Mid-Morning", "Organic Skin-glow Juice + 50g Almonds/Walnuts", "15g Protein • Omega 3 & Collagen spike"),
                        MealItem("🍛 Lunch", "150g Fresh boneless Chicken breast + Basmati Rice + Curd + Salad", "45g Protein • Ultra lean muscle pack"),
                        MealItem("💪 Pre-Workout", "Oats bowl, 1 Banana + Black Coffee Shot", "High glycogen muscle pump"),
                        MealItem("🏋️ Post-Workout", "Whey Protein shake (optional) or 300ml Skimmed Milk + Banana", "24g Protein • Fast cellular recovery"),
                        MealItem("🌙 Dinner", "150g fresh grilled Paneer OR Fish fillet + steam Broccoli + Brown Rice", "30g Protein • Night-time steady state release"),
                        MealItem("🛌 Before Bed", "300ml rich Milk with Turmeric (Haldi) & Ashwagandha", "10g Protein • Facial depuffing")
                    )
                    else -> listOf(
                        MealItem("🌅 Breakfast", "4 whole Eggs, 80g standard Oats with milk, 1 Banana", "32g Protein • Skin barrier fuel"),
                        MealItem("🥤 Mid-Morning", "Glow Juice Formula + 50g Peanuts", "13g Protein • Fat control & recovery"),
                        MealItem("🍛 Lunch", "150g Chicken breast OR 100g Soya chunks + Dal + Rice + Curd", "40g Protein • Maximum nitrogen preservation"),
                        MealItem("💪 Pre-Workout", "1 Banana + Black sugar-free Coffee", "Glycogen priming"),
                        MealItem("🏋️ Post-Workout", "Electrolyte Matrix + 250ml full cream milk", "8g Protein • Deep fiber nutrition"),
                        MealItem("🌙 Dinner", "4 Eggs OR 100g Fresh Paneer sauté with Rice/Roti + Veg", "26g Protein • Muscle sleep rebuilding"),
                        MealItem("🛌 Before Bed", "250ml standard Milk with cardamoms", "8g Protein • Hormonal replenishment")
                    )
                }

                meals.forEach { meal ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(GlowCyanAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = meal.title, fontSize = 12.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.padding(start = 14.dp, top = 2.dp)) {
                            Text(text = meal.details, fontSize = 11.sp, color = GlowSilverAccent)
                            Text(text = meal.macros, fontSize = 9.sp, color = GlowMutedGrey)
                        }
                    }
                }
            }
        }

        // 6. Indian Cheap Protein Matrix Reference
        item {
            GlowGlassCard {
                Text(text = "INDIAN CHEAP PROTEIN INDEX", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Cheapest Sources for Looksmaxers", fontSize = 15.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().background(GlowDarkGrey).padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Resource (100g)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GlowCyanAccent, modifier = Modifier.weight(1.5f))
                    Text(text = "Protein", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GlowCyanAccent, modifier = Modifier.weight(1f))
                    Text(text = "Est Price", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GlowCyanAccent, modifier = Modifier.weight(1f))
                }

                val rowData = listOf(
                    Triple("Soya Chunks", "52 g", "₹15"),
                    Triple("Eggs (4 pcs)", "24 g", "₹24"),
                    Triple("Milk (500ml)", "16 g", "₹28"),
                    Triple("Peanuts", "25 g", "₹15"),
                    Triple("Moong Dal", "24 g", "₹12"),
                    Triple("Paneer", "18 g", "₹40"),
                    Triple("Chicken Breast", "27 g", "₹30"),
                    Triple("Curd", "4 g", "₹10")
                )

                rowData.forEach { (name, protein, price) ->
                    HorizontalDivider(color = GlowDarkGrey.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = name, fontSize = 11.sp, color = GlowSilverAccent, modifier = Modifier.weight(1.5f))
                        Text(text = protein, fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(text = price, fontSize = 11.sp, color = GlowMutedGrey, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    } else {
        // Physical state trackers and diagnostics
        item {
            Column {
                Text(text = "DIAGNOSTIC MATRIX HUB", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Physical Status Analytics", fontSize = 24.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                Text(text = "Skin texture development, densities & hair progression telemetry", fontSize = 12.sp, color = GlowMutedGrey)
            }
        }

        item {
            GlowGlassCard {
                Text(text = "DIAGNOSTIC TELEMETRY LOGGER", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                Text(text = "Save Daily Physical Status", fontSize = 15.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Skin Clarity Estimate", fontSize = 12.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                        Text(text = "${skinClaritySlider.toInt()}%", fontSize = 12.sp, color = GlowBlueAccent, fontWeight = FontWeight.Black)
                    }
                    Slider(
                        value = skinClaritySlider,
                        onValueChange = { skinClaritySlider = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = GlowBlueAccent,
                            activeTrackColor = GlowBlueAccent,
                            inactiveTrackColor = GlowDarkGrey
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Hair Density / Growth Score", fontSize = 12.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                        Text(text = "${hairGrowthSlider.toInt()}%", fontSize = 12.sp, color = GlowCyanAccent, fontWeight = FontWeight.Black)
                    }
                    Slider(
                        value = hairGrowthSlider,
                        onValueChange = { hairGrowthSlider = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = GlowCyanAccent,
                            activeTrackColor = GlowCyanAccent,
                            inactiveTrackColor = GlowDarkGrey
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = templeRegrowthNotes,
                    onValueChange = { templeRegrowthNotes = it },
                    label = { Text("Temple Regrowth observations...", color = GlowMutedGrey) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GlowSilverAccent,
                        unfocusedTextColor = GlowSilverAccent,
                        focusedBorderColor = GlowCyanAccent,
                        unfocusedBorderColor = GlowDarkGrey
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                GlowButton(
                    text = "Commit Metrics To Database",
                    onClick = {
                        viewModel.updateTrackerScores(
                            skinClarity = skinClaritySlider.toInt(),
                            hairGrowth = hairGrowthSlider.toInt(),
                            templeNotes = templeRegrowthNotes
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Check
                )
            }
        }

        item {
            GlowGlassCard {
                Text(text = "OPTICAL MATRIX DETECTOR", fontSize = 10.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                Text(text = "Transformation Compare Link", fontSize = 15.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlowDarkGrey)
                        .border(1.dp, GlowBlueAccent.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val realPhotos = progressPhotos.filter { !it.photoBase64.startsWith("MOCK") }
                    if (realPhotos.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                val firstPhoto = realPhotos.first()
                                RenderProgressPhoto(
                                    photoBase64 = firstPhoto.photoBase64,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .background(GlowBlack.copy(alpha = 0.7f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "BASELINE (DAY ${firstPhoto.dayNumber})", fontSize = 7.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(2.dp)
                                    .background(GlowCyanAccent)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                val lastPhoto = realPhotos.last()
                                RenderProgressPhoto(
                                    photoBase64 = lastPhoto.photoBase64,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(GlowCyanAccent.copy(alpha = 0.8f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "CURRENT (DAY ${lastPhoto.dayNumber})", fontSize = 7.sp, color = GlowBlack, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Brush.verticalGradient(listOf(GlowDarkGrey, GlowBlack))),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = "Before", tint = GlowSilverAccent.copy(alpha = 0.4f), modifier = Modifier.size(36.dp))
                                    Text(text = "BEFORE PHOTO", fontSize = 10.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                                    Text(text = "No custom selfie uploaded yet", fontSize = 8.sp, color = GlowMutedGrey)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(2.dp)
                                    .background(GlowCyanAccent)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Brush.verticalGradient(listOf(GlowDarkGrey, GlowDarkCard))),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = "Active", tint = GlowCyanAccent, modifier = Modifier.size(36.dp))
                                    Text(text = "DAILY PROGRESS", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                                    Text(text = "Waiting for custom photo...", fontSize = 8.sp, color = GlowCyanAccent)
                                }
                            }
                        }
                    }
                }

                if (progressPhotos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "LIVE PHYSICAL TRANSFORMATION GALLERY",
                        fontSize = 10.sp,
                        color = GlowCyanAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(progressPhotos) { photo ->
                            Column(
                                modifier = Modifier
                                    .width(105.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GlowDarkGrey)
                                    .border(0.5.dp, GlowBlueAccent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (photo.photoBase64.startsWith("MOCK")) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .background(GlowDarkCard),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.AccountCircle, contentDescription = "Mock image", tint = GlowMutedGrey, modifier = Modifier.size(24.dp))
                                            Text(text = "DEMO IMAGE", fontSize = 8.sp, color = GlowMutedGrey)
                                        }
                                    }
                                } else {
                                    RenderProgressPhoto(
                                        photoBase64 = photo.photoBase64,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = photo.label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GlowSilverAccent,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Day ${photo.dayNumber}",
                                    fontSize = 8.sp,
                                    color = GlowCyanAccent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlowButton(
                        text = "Upload Daily Photo",
                        onClick = { photoLauncher.launch("image/*") },
                        modifier = Modifier.weight(1.2f),
                        icon = Icons.Default.Add,
                        isSecondary = false
                    )

                    GlowButton(
                        text = "Demo Photo",
                        onClick = { viewModel.addMockProgressPhoto("Baseline Silhouette") },
                        modifier = Modifier.weight(0.8f),
                        icon = Icons.Default.PlayArrow,
                        isSecondary = true
                    )
                }
            }
        }
    }

    item { Spacer(modifier = Modifier.height(30.dp)) }
}
}

@Composable
fun SettingsScreen(
    viewModel: GlowViewModel,
    modifier: Modifier = Modifier
) {
    val weightStr by viewModel.userWeightFlow.collectAsState()
    val heightStr by viewModel.userHeightFlow.collectAsState()
    val targetWeightStr by viewModel.userTargetWeightFlow.collectAsState()
    val budgetLevel by viewModel.userBudgetLevelFlow.collectAsState()
    val alarms by viewModel.alarms.collectAsState()
    val points by viewModel.glowPointsFlow.collectAsState()
    val lastBackup by viewModel.lastBackupFlow.collectAsState()
    val progressPhotos by viewModel.progressPhotosFlow.collectAsState()

    val chatHistory by viewModel.chatMessagesFlow.collectAsState()
    val isSearching by viewModel.aiSearching.collectAsState()

    var editWeight by remember { mutableStateOf("") }
    var editHeight by remember { mutableStateOf("") }
    var editTargetWeight by remember { mutableStateOf("") }

    var userMessage by remember { mutableStateOf("") }
    var selectedSubTab by remember { mutableStateOf(0) } // 0: AI Coach, 1: Cockpit Settings

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addRealProgressPhoto(it, "Baseline selfie record")
        }
    }

    LaunchedEffect(weightStr, heightStr, targetWeightStr) {
        editWeight = weightStr
        editHeight = heightStr
        editTargetWeight = targetWeightStr
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBlack)
    ) {
        // Toggle tabs at the top (fixed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .background(GlowDarkCard, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedSubTab == 0) GlowBlueAccent.copy(alpha = 0.25f) else Color.Transparent)
                    .border(1.dp, if (selectedSubTab == 0) GlowBlueAccent else Color.Transparent, RoundedCornerShape(8.dp))
                    .clickable { selectedSubTab = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI COACH",
                    color = if (selectedSubTab == 0) GlowCyanAccent else GlowMutedGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedSubTab == 1) GlowBlueAccent.copy(alpha = 0.25f) else Color.Transparent)
                    .border(1.dp, if (selectedSubTab == 1) GlowBlueAccent else Color.Transparent, RoundedCornerShape(8.dp))
                    .clickable { selectedSubTab = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SETTINGS HUB",
                    color = if (selectedSubTab == 1) GlowCyanAccent else GlowMutedGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (selectedSubTab == 0) {
            // Chat Sub-tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Header inside chat
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "AI GENETIC COACH", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Transformation Engine", fontSize = 17.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                    }
                    IconButton(
                        onClick = { viewModel.clearChat() },
                        modifier = Modifier.background(GlowDarkGrey.copy(alpha = 0.5f), CircleShape).size(34.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear Chat", tint = GlowNeonRed, modifier = Modifier.size(16.dp))
                    }
                }

                // Scrollable Chat area
                val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                LaunchedEffect(chatHistory.size) {
                    if (chatHistory.isNotEmpty()) {
                        listState.animateScrollToItem(chatHistory.size - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quick Prompts row at the top of chat
                    item {
                        Text(text = "RECOMMENDED ENHANCEMENT PROTOCOLS", fontSize = 8.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val items = listOf("Analyze progress photos", "Hairline regrowth tips", "Optimize elixirs/diet", "Correct daily routine")
                            items(items) { itemText ->
                                Box(
                                    modifier = Modifier
                                        .background(GlowDarkGrey, RoundedCornerShape(12.dp))
                                        .border(0.5.dp, GlowBlueAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .clickable { viewModel.askAiAssistant(itemText) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(text = itemText, fontSize = 10.sp, color = GlowCyanAccent)
                                }
                            }
                        }
                    }

                    items(chatHistory) { msg ->
                        val isUser = msg.sender == "USER"
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Column(
                                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isUser) GlowBlueAccent.copy(alpha = 0.2f) else GlowDarkCard,
                                            RoundedCornerShape(
                                                topStart = 12.dp,
                                                topEnd = 12.dp,
                                                bottomStart = if (isUser) 12.dp else 2.dp,
                                                bottomEnd = if (isUser) 2.dp else 12.dp
                                            )
                                        )
                                        .border(
                                            0.5.dp,
                                            if (isUser) GlowBlueAccent else GlowDarkGrey,
                                            RoundedCornerShape(
                                                topStart = 12.dp,
                                                topEnd = 12.dp,
                                                bottomStart = if (isUser) 12.dp else 2.dp,
                                                bottomEnd = if (isUser) 2.dp else 12.dp
                                            )
                                        )
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 11.sp,
                                        color = if (isUser) GlowCyanAccent else GlowSilverAccent
                                    )
                                }
                                Text(
                                    text = if (isUser) "You" else "Coach OS",
                                    fontSize = 7.sp,
                                    color = GlowMutedGrey,
                                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                                )
                            }
                        }
                    }

                    if (isSearching) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = GlowCyanAccent,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Coach is inspecting bio-telemetry...",
                                    fontSize = 10.sp,
                                    color = GlowMutedGrey,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }

                // Input Box panel at bottom of Chat tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userMessage,
                        onValueChange = { userMessage = it },
                        placeholder = { Text("Ask Coach OS about your hairline or skin...", color = GlowMutedGrey, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlowSilverAccent,
                            unfocusedTextColor = GlowSilverAccent,
                            focusedBorderColor = GlowCyanAccent,
                            unfocusedBorderColor = GlowDarkGrey
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_coach_query_input"),
                        trailingIcon = {
                            if (userMessage.isNotEmpty() && !isSearching) {
                                IconButton(
                                    onClick = {
                                        viewModel.askAiAssistant(userMessage)
                                        userMessage = ""
                                    }
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = GlowCyanAccent)
                                }
                            }
                        }
                    )
                }
            }
        } else {
            // Settings Sub-tab (standard lazy column content)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(10.dp)) }

                // Core Header
                item {
                    Column {
                        Text(text = "GLOWOS COCKPIT CONTROL & INTERFACE", fontSize = 11.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Central Settings", fontSize = 24.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                        Text(text = "Master adjustment and profile calibration panel", fontSize = 12.sp, color = GlowMutedGrey)
                    }
                }

                // 1. Interactive Profile Stats Setup
                item {
                    GlowGlassCard {
                        Text(text = "BIOMETRIC SPECS (EDIT ANYTHING)", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Physical Parameters Calibration", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = editWeight,
                                onValueChange = {
                                    editWeight = it
                                    viewModel.updateWeight(it)
                                },
                                label = { Text("Weight (kg)", color = GlowMutedGrey, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = GlowSilverAccent,
                                    unfocusedTextColor = GlowSilverAccent,
                                    focusedBorderColor = GlowCyanAccent,
                                    unfocusedBorderColor = GlowDarkGrey
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("weight_edit_field")
                            )

                            OutlinedTextField(
                                value = editHeight,
                                onValueChange = {
                                    editHeight = it
                                    viewModel.updateHeight(it)
                                },
                                label = { Text("Height (cm)", color = GlowMutedGrey, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = GlowSilverAccent,
                                    unfocusedTextColor = GlowSilverAccent,
                                    focusedBorderColor = GlowBlueAccent,
                                    unfocusedBorderColor = GlowDarkGrey
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("height_edit_field")
                            )

                            OutlinedTextField(
                                value = editTargetWeight,
                                onValueChange = {
                                    editTargetWeight = it
                                    viewModel.updateTargetWeight(it)
                                },
                                label = { Text("Target Weight (kg)", color = GlowMutedGrey, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = GlowSilverAccent,
                                    unfocusedTextColor = GlowSilverAccent,
                                    focusedBorderColor = GlowCyanAccent,
                                    unfocusedBorderColor = GlowDarkGrey
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("target_weight_edit_field")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val weightFloat = editWeight.toFloatOrNull() ?: 67f
                        val heightCm = editHeight.toFloatOrNull() ?: 179f
                        val bmi = if (heightCm > 0f) {
                            weightFloat / ((heightCm / 100f) * (heightCm / 100f))
                        } else 0f

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GlowDarkGrey.copy(alpha = 0.5f))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "CURRENT COMPUTED BMI STATUS", fontSize = 9.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                                Text(text = String.format("%.1f BMI Index", bmi), fontSize = 16.sp, fontWeight = FontWeight.Black, color = GlowCyanAccent)
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        if (bmi in 18.5..24.9) GlowCyanAccent.copy(alpha = 0.15f) else GlowNeonRed.copy(alpha = 0.15f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (bmi < 18.5) "Underweight" else if (bmi <= 24.9) "Healthy Core" else "Overweight Frame",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bmi in 18.5..24.9) GlowCyanAccent else GlowNeonRed
                                )
                            }
                        }
                    }
                }

                // 2. Budget Level Selection Control
                item {
                    GlowGlassCard {
                        Text(text = "BUDGET RECALIBRATOR Mode", fontSize = 10.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Adjust Daily Purchasing Power", fontSize = 15.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("STUDENT", "STANDARD", "PREMIUM").forEach { level ->
                                val active = budgetLevel == level
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) GlowBlueAccent.copy(alpha = 0.2f) else GlowDarkGrey)
                                        .border(1.dp, if (active) GlowBlueAccent else GlowDarkGrey, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.updateBudgetLevel(level) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = level,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (active) GlowCyanAccent else GlowMutedGrey
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. EDIT SMART ALARMS & REMINDERS
                item {
                    GlowGlassCard {
                        Text(text = "CHRONO-PROTOCOL TRIGGERS (ALARMS)", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Edit Active Routine Alarms", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(10.dp))

                        alarms.forEach { alarm ->
                            var activeTimeInput by remember { mutableStateOf(alarm.time) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(text = alarm.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GlowSilverAccent)
                                    Text(text = "Protocol tag: ${alarm.tag}", fontSize = 9.sp, color = GlowMutedGrey)
                                }

                                OutlinedTextField(
                                    value = activeTimeInput,
                                    onValueChange = {
                                        activeTimeInput = it
                                        viewModel.updateAlarmTime(alarm.tag, it)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = GlowCyanAccent,
                                        unfocusedTextColor = GlowSilverAccent,
                                        focusedBorderColor = GlowCyanAccent,
                                        unfocusedBorderColor = GlowDarkGrey
                                    ),
                                    textStyle = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(46.dp)
                                        .padding(end = 6.dp)
                                )

                                Switch(
                                    checked = alarm.isActive,
                                    onCheckedChange = { viewModel.toggleAlarm(alarm.tag) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = GlowCyanAccent,
                                        checkedTrackColor = GlowBlueAccent.copy(alpha = 0.5f)
                                    )
                                )
                            }
                        }
                    }
                }

                // 4. VISUAL TIMELINE SELFIE ARCHIVE
                item {
                    GlowGlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "OPTICAL CELL ARCHIVE", fontSize = 10.sp, color = GlowBlueAccent, fontWeight = FontWeight.Bold)
                                Text(text = "Selfie Comparison Tracker", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                            }
                            IconButton(
                                onClick = { photoLauncher.launch("image/*") },
                                modifier = Modifier.background(GlowDarkGrey, CircleShape).size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Upload selfie", tint = GlowCyanAccent)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (progressPhotos.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GlowDarkGrey)
                                    .clickable { photoLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = "Camera", tint = GlowMutedGrey, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "No Progress Selfies Loaded", fontSize = 11.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                                    Text(text = "Tap to load premium baseline pic", fontSize = 9.sp, color = GlowCyanAccent)
                                }
                            }
                        } else {
                            val first = progressPhotos.first()
                            val last = progressPhotos.last()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "BASELINE (DAY ${first.dayNumber})", fontSize = 9.sp, color = GlowMutedGrey, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    RenderProgressPhoto(
                                        photoBase64 = first.photoBase64,
                                        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp))
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "CURRENT (DAY ${last.dayNumber})", fontSize = 9.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    RenderProgressPhoto(
                                        photoBase64 = last.photoBase64,
                                        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp))
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. MASTER CONTROLS / CLOUD RESET
                item {
                    GlowGlassCard {
                        Text(text = "SYSTEM MASTER CONTROLS", fontSize = 10.sp, color = GlowCyanAccent, fontWeight = FontWeight.Bold)
                        Text(text = "Reboot & Synchronize GlowOS", fontSize = 16.sp, color = GlowSilverAccent, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Secure Cloud Synchronization", fontSize = 13.sp, color = GlowSilverAccent, fontWeight = FontWeight.Bold)
                                    Text(text = "Last Sync: $lastBackup", fontSize = 11.sp, color = GlowMutedGrey)
                                }
                                GlowButton(
                                    text = "Backup",
                                    onClick = { viewModel.performCloudBackup() },
                                    modifier = Modifier.width(90.dp)
                                )
                            }

                            HorizontalDivider(color = GlowDarkGrey, thickness = 0.5.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Master Reboot System", fontSize = 13.sp, color = GlowNeonRed, fontWeight = FontWeight.Bold)
                                    Text(text = "Irreversibly resets all custom traits, settings, calories and routines.", fontSize = 10.sp, color = GlowMutedGrey)
                                }
                                GlowButton(
                                    text = "Reset All",
                                    onClick = { viewModel.resetEntireProgram() },
                                    modifier = Modifier.width(90.dp),
                                    isSecondary = true
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }
}

@Composable
fun RenderProgressPhoto(
    photoBase64: String,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(photoBase64) {
        try {
            val decodedBytes = Base64.decode(photoBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "User Uploaded Progress Photo",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(GlowDarkGrey),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Broken image", tint = GlowMutedGrey)
        }
    }
}

// Custom icons to support standard indicators in Material Theme
val Icons.Filled.FlameWithSecondary: ImageVector
    get() = Icons.Default.PlayArrow

val Icons.Filled.StarCircle: ImageVector
    get() = Icons.Default.Star

val Icons.Filled.Cloud: ImageVector
    get() = Icons.Default.PlayArrow

data class MealItem(
    val title: String,
    val details: String,
    val macros: String
)
