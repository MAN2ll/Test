package com.securevault.ui.theme

import android.isAtLeastS
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════════════════
// DARK PALETTE — Deep Space
// ═══════════════════════════════════════════════════════════════════════════
object SvColors {
    val BgDeep       = Color(0xFF020610)
    val BgCard       = Color(0xFF0A1225)
    val BgElevated   = Color(0xFF101A30)
    val BgHighlight  = Color(0xFF16244A)
    val Blue         = Color(0xFF4F8EFF)
    val BlueSoft     = Color(0xFF1E3A7A)
    val Teal         = Color(0xFF00D4B4)
    val TealSoft     = Color(0xFF003A32)
    val Gold         = Color(0xFFFFB74D)
    val GoldSoft     = Color(0xFF3D2800)
    val Coral        = Color(0xFFFF5C6A)
    val CoralSoft    = Color(0xFF3D0010)
    val Purple       = Color(0xFF9C6FFF)
    val TextPrimary  = Color(0xFFF0F4FF)
    val TextSecond   = Color(0xFF8BA0CC)
    val TextMuted    = Color(0xFF3D5080)
    val Border       = Color(0xFF1C2E56)
    val BorderSoft   = Color(0xFF111F40)
}

// ═══════════════════════════════════════════════════════════════════════════
// LIGHT PALETTE — Arctic Blue
// ═══════════════════════════════════════════════════════════════════════════
object SvLightColors {
    val BgDeep       = Color(0xFFF4F7FF)
    val BgCard       = Color(0xFFFFFFFF)
    val BgElevated   = Color(0xFFEBF0FF)
    val BgHighlight  = Color(0xFFDDE6FF)
    val Blue         = Color(0xFF2B5FD9)
    val BlueSoft     = Color(0xFFD0DEFF)
    val Teal         = Color(0xFF008C7A)
    val TealSoft     = Color(0xFFCCFFF8)
    val Gold         = Color(0xFFB86000)
    val GoldSoft     = Color(0xFFFFE0B2)
    val Coral        = Color(0xFFC62828)
    val CoralSoft    = Color(0xFFFFCDD2)
    val Purple       = Color(0xFF6B3FCC)
    val TextPrimary  = Color(0xFF0D1426)
    val TextSecond   = Color(0xFF3D5280)
    val TextMuted    = Color(0xFF8BA0CC)
    val Border       = Color(0xFFC8D6F8)
    val BorderSoft   = Color(0xFFDDE6FF)
}

// ═══════════════════════════════════════════════════════════════════════════
// Color schemes
// ═══════════════════════════════════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    primary              = SvColors.Blue,
    onPrimary            = Color.White,
    primaryContainer     = SvColors.BlueSoft,
    onPrimaryContainer   = SvColors.Blue,
    secondary            = SvColors.Teal,
    onSecondary          = Color.Black,
    secondaryContainer   = SvColors.TealSoft,
    onSecondaryContainer = SvColors.Teal,
    tertiary             = SvColors.Gold,
    onTertiary           = Color.Black,
    tertiaryContainer    = SvColors.GoldSoft,
    onTertiaryContainer  = SvColors.Gold,
    error                = SvColors.Coral,
    onError              = Color.White,
    errorContainer       = SvColors.CoralSoft,
    onErrorContainer     = SvColors.Coral,
    background           = SvColors.BgDeep,
    onBackground         = SvColors.TextPrimary,
    surface              = SvColors.BgCard,
    onSurface            = SvColors.TextPrimary,
    surfaceVariant       = SvColors.BgElevated,
    onSurfaceVariant     = SvColors.TextSecond,
    outline              = SvColors.Border,
    outlineVariant       = SvColors.BorderSoft,
    scrim                = Color(0xCC000000)
)

private val LightColorScheme = lightColorScheme(
    primary              = SvLightColors.Blue,
    onPrimary            = Color.White,
    primaryContainer     = SvLightColors.BlueSoft,
    onPrimaryContainer   = SvLightColors.Blue,
    secondary            = SvLightColors.Teal,
    onSecondary          = Color.White,
    secondaryContainer   = SvLightColors.TealSoft,
    onSecondaryContainer = SvLightColors.Teal,
    tertiary             = SvLightColors.Gold,
    onTertiary           = Color.White,
    tertiaryContainer    = SvLightColors.GoldSoft,
    onTertiaryContainer  = SvLightColors.Gold,
    error                = SvLightColors.Coral,
    onError              = Color.White,
    errorContainer       = SvLightColors.CoralSoft,
    onErrorContainer     = SvLightColors.Coral,
    background           = SvLightColors.BgDeep,
    onBackground         = SvLightColors.TextPrimary,
    surface              = SvLightColors.BgCard,
    onSurface            = SvLightColors.TextPrimary,
    surfaceVariant       = SvLightColors.BgElevated,
    onSurfaceVariant     = SvLightColors.TextSecond,
    outline              = SvLightColors.Border,
    outlineVariant       = SvLightColors.BorderSoft,
    scrim                = Color(0x66000000)
)

// ═══════════════════════════════════════════════════════════════════════════
// Typography (shared)
// ═══════════════════════════════════════════════════════════════════════════
private val SvTypography = Typography(
    displayLarge   = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 32.sp, letterSpacing = (-0.5).sp),
    displayMedium  = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 24.sp),
    headlineLarge  = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 22.sp, letterSpacing = (-0.3).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleLarge     = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleMedium    = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 14.sp, letterSpacing = 0.1.sp),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 15.sp),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 13.sp),
    bodySmall      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 11.sp, letterSpacing = 0.3.sp),
    labelLarge     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 13.sp, letterSpacing = 0.5.sp),
    labelMedium    = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 11.sp, letterSpacing = 0.4.sp),
    labelSmall     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 9.sp,  letterSpacing = 0.5.sp)
)

// ═══════════════════════════════════════════════════════════════════════════
// Theme composable — animated crossfade on mode change
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun SecureVaultTheme(content: @Composable () -> Unit) {
    val mode by ThemeManager.mode.collectAsState()
    val systemDark = isSystemInDarkTheme()

    val isDark = when (mode) {
        ThemeMode.DARK   -> true
        ThemeMode.LIGHT  -> false
        ThemeMode.SYSTEM -> systemDark
    }

    Crossfade(
        targetState = isDark,
        animationSpec = tween(durationMillis = 400),
        label = "themeCrossfade"
    ) { dark ->
        MaterialTheme(
            colorScheme = if (dark) DarkColorScheme else LightColorScheme,
            typography  = SvTypography,
            content     = content
        )
    }
}
