package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.GlowViewModel
import com.example.ui.theme.*
import com.example.ui.*

class MainActivity : ComponentActivity() {
    private val viewModel: GlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainLayoutContainer(viewModel)
            }
        }
    }
}

@Composable
fun MainLayoutContainer(viewModel: GlowViewModel) {
    var activeTab by remember { mutableStateOf(0) } // 0: Dashboard, 1: Checklist, 2: Trackers, 3: Fuel/Fitness, 4: AI Coach
    val activeNotification by viewModel.activeNotification.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GlowBlack,
        bottomBar = {
            NavigationBar(
                containerColor = GlowDarkCard,
                tonalElevation = 8.dp,
                modifier = Modifier.border(0.5.dp, GlowBlueAccent.copy(alpha = 0.15f))
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("DASHBOARD", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GlowCyanAccent,
                        selectedTextColor = GlowCyanAccent,
                        unselectedIconColor = GlowMutedGrey,
                        unselectedTextColor = GlowMutedGrey,
                        indicatorColor = GlowBlueAccent.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Checklist") },
                    label = { Text("CHECKLIST", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GlowCyanAccent,
                        selectedTextColor = GlowCyanAccent,
                        unselectedIconColor = GlowMutedGrey,
                        unselectedTextColor = GlowMutedGrey,
                        indicatorColor = GlowBlueAccent.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_checklist")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Food Diet") },
                    label = { Text("FOOD DIET", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GlowCyanAccent,
                        selectedTextColor = GlowCyanAccent,
                        unselectedIconColor = GlowMutedGrey,
                        unselectedTextColor = GlowMutedGrey,
                        indicatorColor = GlowBlueAccent.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_food_diet")
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Gym PPL") },
                    label = { Text("GYM PPL", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GlowCyanAccent,
                        selectedTextColor = GlowCyanAccent,
                        unselectedIconColor = GlowMutedGrey,
                        unselectedTextColor = GlowMutedGrey,
                        indicatorColor = GlowBlueAccent.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_gym_ppl")
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("SETTINGS", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GlowCyanAccent,
                        selectedTextColor = GlowCyanAccent,
                        unselectedIconColor = GlowMutedGrey,
                        unselectedTextColor = GlowMutedGrey,
                        indicatorColor = GlowBlueAccent.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GlowBlack)
                .padding(innerPadding)
        ) {
            // Core screen content switcher
            when (activeTab) {
                0 -> DashboardScreen(viewModel = viewModel, onMoveToTab = { activeTab = it })
                1 -> ChecklistScreen(viewModel = viewModel)
                2 -> FoodIntakeScreen(viewModel = viewModel)
                3 -> GymPPLScreen(viewModel = viewModel)
                4 -> SettingsScreen(viewModel = viewModel)
            }

            // Top overlay alert layer for smart alarms and notifications
            AnimatedVisibility(
                visible = activeNotification != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                activeNotification?.let { message ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GlowDarkCard, GlowDarkGrey)
                                )
                            )
                            .border(1.dp, GlowCyanAccent, RoundedCornerShape(12.dp))
                            .clickable { viewModel.clearOverlayNotification() }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GlowCyanAccent)
                            )
                            Text(
                                text = message,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlowSilverAccent,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = GlowMutedGrey,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { viewModel.clearOverlayNotification() }
                            )
                        }
                    }
                }
            }
        }
    }
}


