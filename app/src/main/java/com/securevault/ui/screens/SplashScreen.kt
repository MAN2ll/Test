package com.securevault.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.securevault.ui.theme.SvColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // ── Animation states ─────────────────────────────────────────────────────
    var logoVisible    by remember { mutableStateOf(false) }
    var titleVisible   by remember { mutableStateOf(false) }
    var badgesVisible  by remember { mutableStateOf(false) }
    var barVisible     by remember { mutableStateOf(false) }

    // Logo: spring scale
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        label = "logoScale"
    )

    // Logo glow pulse (infinite after logo appears)
    val pulse = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.4f, targetValue = 0.85f, label = "glow",
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse)
    )

    // Title fade + slide
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing), label = "titleAlpha"
    )
    val titleOffset by animateFloatAsState(
        targetValue = if (titleVisible) 0f else 30f,
        animationSpec = tween(700, easing = FastOutSlowInEasing), label = "titleOffset"
    )

    // Badges staggered
    val badgeAlpha by animateFloatAsState(
        targetValue = if (badgesVisible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 100), label = "badgeAlpha"
    )

    // Progress bar
    val barProgress by animateFloatAsState(
        targetValue = if (barVisible) 1f else 0f,
        animationSpec = tween(1800, easing = FastOutSlowInEasing), label = "barProgress"
    )

    // Floating orbs
    val orb1 = rememberInfiniteTransition(label = "orb1")
    val orb1Y by orb1.animateFloat(
        initialValue = -20f, targetValue = 20f, label = "o1y",
        animationSpec = infiniteRepeatable(tween(3400, easing = EaseInOutSine), RepeatMode.Reverse)
    )
    val orb2 = rememberInfiniteTransition(label = "orb2")
    val orb2Y by orb2.animateFloat(
        initialValue = 15f, targetValue = -15f, label = "o2y",
        animationSpec = infiniteRepeatable(tween(4100, easing = EaseInOutSine), RepeatMode.Reverse)
    )

    // ── Sequencing ───────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        delay(200);  logoVisible  = true
        delay(600);  titleVisible = true
        delay(400);  badgesVisible = true
        delay(200);  barVisible   = true
        delay(2000); onFinished()
    }

    // ── UI ───────────────────────────────────────────────────────────────────
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020610), Color(0xFF050E24), Color(0xFF020610))
                )
            )
    ) {

        // Background orbs (decorative blurred glows)
        Box(
            Modifier
                .size(320.dp)
                .offset(x = (-80).dp, y = orb1Y.dp)
                .align(Alignment.TopStart)
                .blur(60.dp)
                .background(
                    Brush.radialGradient(
                        listOf(SvColors.Blue.copy(alpha = 0.18f), Color.Transparent)
                    ), CircleShape
                )
        )
        Box(
            Modifier
                .size(280.dp)
                .offset(x = 60.dp, y = orb2Y.dp)
                .align(Alignment.BottomEnd)
                .blur(50.dp)
                .background(
                    Brush.radialGradient(
                        listOf(SvColors.Teal.copy(alpha = 0.12f), Color.Transparent)
                    ), CircleShape
                )
        )
        Box(
            Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .offset(y = (-30).dp)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(SvColors.Blue.copy(alpha = glowAlpha * 0.2f), Color.Transparent)
                    ), CircleShape
                )
        )

        // Main content
        Column(
            Modifier.fillMaxSize().padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Animated logo ──────────────────────────────────────────
            Box(
                Modifier
                    .scale(logoScale)
                    .size(110.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(
                                    SvColors.Blue.copy(alpha = glowAlpha * 0.6f),
                                    SvColors.Teal.copy(alpha = glowAlpha * 0.2f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.minDimension * 0.9f
                        )
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0D2060), Color(0xFF051540)),
                            start = Offset(0f, 0f),
                            end   = Offset(Float.MAX_VALUE, Float.MAX_VALUE)
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(SvColors.Blue.copy(alpha = 0.8f), SvColors.Teal.copy(alpha = 0.4f))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Shield, null,
                    Modifier.size(52.dp),
                    tint = SvColors.Blue
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── App name ───────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .offset(y = titleOffset.dp)
            ) {
                Text(
                    "Secure",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SvColors.TextPrimary,
                    letterSpacing = (-1).sp
                )
                Text(
                    "Vault",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp,
                    color = Color.Transparent,
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(SvColors.Blue, SvColors.Teal)
                            )
                        )
                        // Clip gradient to text via drawWithContent trick
                )
                // Gradient "Vault" (workaround — text over gradient box)
                Box(Modifier.offset(y = (-42).dp)) {
                    Text(
                        "Vault",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp,
                        color = SvColors.Blue
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Ваш персональный менеджер паролей",
                    fontSize = 14.sp,
                    color = SvColors.TextSecond,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(48.dp))

            // ── Security badges ────────────────────────────────────────
            Row(
                Modifier.alpha(badgeAlpha),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "AES-256"    to SvColors.Blue,
                    "Keystore"   to SvColors.Teal,
                    "PBKDF2"     to SvColors.Purple
                ).forEach { (label, color) ->
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.12f))
                            .border(0.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(56.dp))

            // ── Progress bar ───────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(if (barVisible) 1f else 0f)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(SvColors.BgHighlight)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(barProgress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(SvColors.Blue, SvColors.Teal)
                                )
                            )
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Инициализация хранилища...",
                    fontSize = 11.sp,
                    color = SvColors.TextMuted,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // ── Version in corner ──────────────────────────────────────────
        Text(
            "v1.0",
            fontSize = 10.sp,
            color = SvColors.TextMuted,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        )
    }
}
