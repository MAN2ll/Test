package com.securevault.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Category colour mapping ───────────────────────────────────────────────
val categoryColors = mapOf(
    "Общее"   to Color(0xFF4F8EFF),
    "Соцсети" to Color(0xFFFF6B9D),
    "Банки"   to Color(0xFF00D4B4),
    "Работа"  to Color(0xFF9C6FFF),
    "Почта"   to Color(0xFFFFB74D),
    "Другое"  to Color(0xFF8BA0CC)
)
fun categoryColor(cat: String): Color = categoryColors[cat] ?: Color(0xFF4F8EFF)

// ── Dynamic palette helpers (reads from ThemeManager) ───────────────────
val isDarkTheme: Boolean
    @Composable get() {
        val mode by ThemeManager.mode.collectAsState()
        val sys = isSystemInDarkTheme()
        return when (mode) {
            ThemeMode.DARK   -> true
            ThemeMode.LIGHT  -> false
            ThemeMode.SYSTEM -> sys
        }
    }

val svBgDeep      @Composable get() = if (isDarkTheme) SvColors.BgDeep      else SvLightColors.BgDeep
val svBgCard      @Composable get() = if (isDarkTheme) SvColors.BgCard      else SvLightColors.BgCard
val svBgElevated  @Composable get() = if (isDarkTheme) SvColors.BgElevated  else SvLightColors.BgElevated
val svBgHighlight @Composable get() = if (isDarkTheme) SvColors.BgHighlight else SvLightColors.BgHighlight
val svBlue        @Composable get() = if (isDarkTheme) SvColors.Blue        else SvLightColors.Blue
val svBlueSoft    @Composable get() = if (isDarkTheme) SvColors.BlueSoft    else SvLightColors.BlueSoft
val svTeal        @Composable get() = if (isDarkTheme) SvColors.Teal        else SvLightColors.Teal
val svTealSoft    @Composable get() = if (isDarkTheme) SvColors.TealSoft    else SvLightColors.TealSoft
val svGold        @Composable get() = if (isDarkTheme) SvColors.Gold        else SvLightColors.Gold
val svGoldSoft    @Composable get() = if (isDarkTheme) SvColors.GoldSoft    else SvLightColors.GoldSoft
val svCoral       @Composable get() = if (isDarkTheme) SvColors.Coral       else SvLightColors.Coral
val svCoralSoft   @Composable get() = if (isDarkTheme) SvColors.CoralSoft   else SvLightColors.CoralSoft
val svPurple      @Composable get() = if (isDarkTheme) SvColors.Purple      else SvLightColors.Purple
val svTextPrimary @Composable get() = if (isDarkTheme) SvColors.TextPrimary else SvLightColors.TextPrimary
val svTextSecond  @Composable get() = if (isDarkTheme) SvColors.TextSecond  else SvLightColors.TextSecond
val svTextMuted   @Composable get() = if (isDarkTheme) SvColors.TextMuted   else SvLightColors.TextMuted
val svBorder      @Composable get() = if (isDarkTheme) SvColors.Border      else SvLightColors.Border
val svBorderSoft  @Composable get() = if (isDarkTheme) SvColors.BorderSoft  else SvLightColors.BorderSoft

// ── Glassmorphism card modifier ───────────────────────────────────────────
@Composable
fun Modifier.glassCard(cornerRadius: Dp = 16.dp, borderAlpha: Float = 0.18f): Modifier {
    val elevated = svBgElevated
    val border1  = svBorder.copy(alpha = borderAlpha * 2)
    val border2  = svBorderSoft.copy(alpha = borderAlpha)
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(elevated)
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                listOf(border1, border2),
                start = Offset(0f, 0f),
                end   = Offset(Float.MAX_VALUE, Float.MAX_VALUE)
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}

// ── Glow effect ────────────────────────────────────────────────────────────
fun Modifier.glowEffect(glowColor: Color, glowRadius: Dp = 24.dp, alpha: Float = 0.35f): Modifier =
    this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        glowRadius.toPx(), 0f, 0f,
                        android.graphics.Color.argb(
                            (alpha * 255).toInt(),
                            android.graphics.Color.red(glowColor.toArgb()),
                            android.graphics.Color.green(glowColor.toArgb()),
                            android.graphics.Color.blue(glowColor.toArgb())
                        )
                    )
                }
            }
            canvas.drawRoundRect(0f, 0f, size.width, size.height, 40f, 40f, paint)
        }
    }
