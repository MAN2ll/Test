package com.securevault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.securevault.ui.screens.*
import com.securevault.ui.theme.SvColors

@Composable
fun SecureVaultNavHost() {
    val root = rememberNavController()
    NavHost(root, startDestination = "splash") {

        // ── Splash ────────────────────────────────────────────────────
        composable("splash") {
            SplashScreen(
                onFinished = {
                    root.navigate("lock") { popUpTo("splash") { inclusive = true } }
                }
            )
        }

        // ── Lock ──────────────────────────────────────────────────────
        composable("lock") {
            LockScreen(
                onUnlocked        = { root.navigate("main") { popUpTo("lock") { inclusive = true } } },
                onBiometricRequest = {}
            )
        }

        // ── Main shell with bottom nav ────────────────────────────────
        composable("main") {
            MainShell(onLock = { root.navigate("lock") { popUpTo("main") { inclusive = true } } })
        }
    }
}

// ── Nav tab descriptor ────────────────────────────────────────────────────────
private data class NavTab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun MainShell(onLock: () -> Unit) {
    val tabs = listOf(
        NavTab("passwords", "Пароли",    Icons.Default.VpnKey),
        NavTab("favorites", "Избранное", Icons.Default.Star),
        NavTab("settings",  "Настройки", Icons.Default.Settings)
    )
    val nav = rememberNavController()
    val back by nav.currentBackStackEntryAsState()
    val cur = back?.destination?.route?.substringBefore("?")

    Scaffold(
        containerColor = SvColors.BgDeep,
        bottomBar = {
            // Floating pill bottom nav
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, SvColors.BgDeep.copy(alpha = 0.95f))
                        )
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(SvColors.BgElevated)
                        .border(1.dp, SvColors.Border, RoundedCornerShape(26.dp))
                        .padding(vertical = 6.dp, horizontal = 6.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        tabs.forEach { tab ->
                            val selected = cur == tab.route || (tab.route == "passwords" && cur == null)
                            NavItem(tab, selected) {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { pad ->
        NavHost(nav, "passwords", Modifier.padding(pad)) {
            composable("passwords") {
                VaultListScreen(
                    onAdd  = { nav.navigate("entry") },
                    onEdit = { nav.navigate("entry?id=$it") },
                    onLock = onLock
                )
            }
            composable("favorites") {
                VaultListScreen(
                    onAdd  = { nav.navigate("entry") },
                    onEdit = { nav.navigate("entry?id=$it") },
                    onLock = onLock,
                    favOnly = true
                )
            }
            composable("settings") { SettingsScreen(onResetDone = onLock) }
            composable(
                "entry?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
            ) { back ->
                val rawId = back.arguments?.getLong("id") ?: -1L
                EntryEditScreen(
                    entryId = if (rawId == -1L) null else rawId,
                    onBack  = { nav.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun NavItem(tab: NavTab, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (selected) Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                SvColors.Blue.copy(alpha = 0.22f),
                                SvColors.Teal.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(1.dp, SvColors.Blue.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                else Modifier
            )
    ) {
        NavigationBarItem(
            selected  = selected,
            onClick   = onClick,
            icon = {
                Icon(
                    tab.icon, tab.label,
                    Modifier.size(21.dp),
                    tint = if (selected) SvColors.Blue else SvColors.TextMuted
                )
            },
            label = {
                Text(
                    tab.label,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) SvColors.Blue else SvColors.TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = SvColors.Blue,
                unselectedIconColor = SvColors.TextMuted,
                indicatorColor      = Color.Transparent
            )
        )
    }
}
