package com.securevault.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.securevault.ui.theme.SvColors

@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    onBiometricRequest: () -> Unit,
    vm: LockViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    var pwd by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPwd by remember { mutableStateOf(false) }
    val isSetup = vm.isSetupDone

    // Pulsing logo animation
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f, targetValue = 1.06f, label = "scale",
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse)
    )
    val glowAlpha by pulseAnim.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f, label = "glow",
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse)
    )

    LaunchedEffect(state) { if (state is LockState.Success) onUnlocked() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF020610), Color(0xFF060D22))))
    ) {
        // Background glow orb
        Box(
            Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        listOf(SvColors.Blue.copy(alpha = glowAlpha * 0.25f), Color.Transparent)
                    ), CircleShape
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo ──────────────────────────────────────────────────────
            Box(
                Modifier
                    .scale(pulseScale)
                    .size(90.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(SvColors.Blue.copy(alpha = glowAlpha), Color.Transparent)
                            ),
                            radius = size.minDimension * 0.85f
                        )
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(SvColors.BlueSoft, Color(0xFF0A1F4E)),
                            start = Offset(0f, 0f),
                            end   = Offset(Float.MAX_VALUE, Float.MAX_VALUE)
                        )
                    )
                    .border(1.5.dp, Brush.linearGradient(
                        listOf(SvColors.Blue.copy(alpha = 0.7f), SvColors.Teal.copy(alpha = 0.3f))
                    ), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Shield, null, Modifier.size(40.dp), tint = SvColors.Blue)
            }

            Spacer(Modifier.height(20.dp))
            Text("Secure Vault", fontWeight = FontWeight.Bold, fontSize = 30.sp,
                color = SvColors.TextPrimary, letterSpacing = (-0.5).sp)
            Text(if (isSetup) "Введите мастер-пароль" else "Создайте мастер-пароль",
                fontSize = 13.sp, color = SvColors.TextSecond, modifier = Modifier.padding(top = 4.dp))

            Spacer(Modifier.height(36.dp))

            // ── Lockout / Wipe states ──────────────────────────────────
            when (val s = state) {
                is LockState.Wiped -> {
                    WipeCard()
                    return@Column
                }
                is LockState.LockedOut -> {
                    LockoutCard(s)
                    return@Column
                }
                else -> {}
            }

            // ── Input fields ──────────────────────────────────────────
            SvTextField(
                value = pwd, onValueChange = { pwd = it; vm.clearError() },
                label = "Мастер-пароль",
                icon = Icons.Default.Lock,
                isPassword = true, showPassword = showPwd,
                onTogglePassword = { showPwd = !showPwd }
            )

            if (!isSetup) {
                Spacer(Modifier.height(10.dp))
                SvTextField(
                    value = confirm, onValueChange = { confirm = it; vm.clearError() },
                    label = "Подтвердите пароль",
                    icon = Icons.Default.LockOpen,
                    isPassword = true, showPassword = showPwd,
                    onTogglePassword = { showPwd = !showPwd }
                )
            }

            // ── Error / warning ───────────────────────────────────────
            if (state is LockState.Error) {
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SvColors.CoralSoft)
                        .border(1.dp, SvColors.Coral.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text((state as LockState.Error).msg, color = SvColors.Coral,
                        fontSize = 13.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                }
            }

            if (isSetup && vm.failedAttempts >= 3) {
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SvColors.GoldSoft)
                        .border(1.dp, SvColors.Gold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text("⚠ Осталось попыток до удаления: ${10 - vm.failedAttempts}",
                        color = SvColors.Gold, fontSize = 12.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Primary button ────────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF3A6EE0), Color(0xFF5B8FF9))
                        )
                    )
                    .border(1.dp, SvColors.Blue.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { if (isSetup) vm.unlock(pwd) else vm.setup(pwd, confirm) },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent),
                    enabled = state !is LockState.Loading && state !is LockState.LockedOut,
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    if (state is LockState.Loading)
                        CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                    else
                        Text(if (isSetup) "Войти" else "Создать хранилище",
                            fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            if (isSetup && vm.isBiometricEnabled) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onBiometricRequest,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SvColors.Teal),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            listOf(SvColors.Teal.copy(alpha = 0.6f), SvColors.Blue.copy(alpha = 0.4f))
                        )
                    )
                ) {
                    Icon(Icons.Default.Fingerprint, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Биометрия", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(28.dp))
            if (!isSetup) {
                SecurityBadges()
            }
        }
    }
}

@Composable
private fun SvTextField(
    value: String, onValueChange: (String) -> Unit,
    label: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false, showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation()
                               else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password)
                         else KeyboardOptions.Default,
        leadingIcon = { Icon(icon, null, Modifier.size(20.dp), tint = SvColors.TextSecond) },
        trailingIcon = if (isPassword && onTogglePassword != null) ({
            IconButton(onTogglePassword) {
                Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    null, tint = SvColors.TextSecond)
            }
        }) else null,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor    = SvColors.Blue,
            unfocusedBorderColor  = SvColors.Border,
            focusedContainerColor = SvColors.BgHighlight,
            unfocusedContainerColor = SvColors.BgElevated,
            focusedTextColor = SvColors.TextPrimary,
            unfocusedTextColor = SvColors.TextPrimary,
            focusedLabelColor = SvColors.Blue,
            unfocusedLabelColor = SvColors.TextSecond,
            cursorColor = SvColors.Blue
        )
    )
}

@Composable
private fun WipeCard() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SvColors.CoralSoft)
            .border(1.dp, SvColors.Coral.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.DeleteForever, null, Modifier.size(48.dp), tint = SvColors.Coral)
        Text("Хранилище уничтожено", fontWeight = FontWeight.Bold, color = SvColors.Coral, fontSize = 18.sp)
        Text("Все данные удалены после 10 неверных попыток.",
            color = SvColors.TextSecond, fontSize = 13.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun LockoutCard(s: LockState.LockedOut) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SvColors.GoldSoft)
            .border(1.dp, SvColors.Gold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.Timer, null, Modifier.size(40.dp), tint = SvColors.Gold)
        Text("Слишком много попыток", fontWeight = FontWeight.Bold, color = SvColors.Gold)
        Text("${s.secondsLeft}", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = SvColors.Gold)
        Text("секунд до следующей попытки", color = SvColors.TextSecond, fontSize = 12.sp)
        Text("Осталось попыток до удаления: ${s.attemptsLeft}",
            color = SvColors.Coral, fontSize = 12.sp)
    }
}

@Composable
private fun SecurityBadges() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("AES-256", "Keystore", "PBKDF2").forEachIndexed { i, badge ->
            if (i > 0) Text("·", color = SvColors.TextMuted, modifier = Modifier.padding(horizontal = 6.dp), fontSize = 10.sp)
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SvColors.BlueSoft.copy(alpha = 0.4f))
                    .border(0.5.dp, SvColors.Border, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(badge, fontSize = 10.sp, color = SvColors.Blue, fontWeight = FontWeight.Medium)
            }
        }
    }
}
