package com.securevault.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.securevault.ui.theme.SvColors
import com.securevault.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onResetDone: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val state    by vm.state.collectAsState()
    val theme    by vm.themeMode.collectAsState()
    var bio      by remember { mutableStateOf(vm.isBiometricEnabled) }

    var showChangePwd  by remember { mutableStateOf(false) }
    var curPwd         by remember { mutableStateOf("") }
    var newPwd         by remember { mutableStateOf("") }
    var cfmPwd         by remember { mutableStateOf("") }
    var showPwds       by remember { mutableStateOf(false) }
    var exportUri      by remember { mutableStateOf<Uri?>(null) }
    var importUri      by remember { mutableStateOf<Uri?>(null) }
    var showExportDlg  by remember { mutableStateOf(false) }
    var showImportDlg  by remember { mutableStateOf(false) }
    var exportPwd      by remember { mutableStateOf("") }
    var importPwd      by remember { mutableStateOf("") }
    var showExpPwd     by remember { mutableStateOf(false) }
    var showImpPwd     by remember { mutableStateOf(false) }
    var showMassRotate by remember { mutableStateOf(false) }
    var rotateProfile  by remember { mutableStateOf<String?>(null) }
    var showReset      by remember { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        when (val s = state) {
            is SettingsState.Success -> {
                if (s.msg == "RESET") { onResetDone(); return@LaunchedEffect }
                snackbar.showSnackbar(s.msg); vm.clearState()
                showChangePwd = false; showExportDlg = false; showImportDlg = false
                curPwd = ""; newPwd = ""; cfmPwd = ""; exportPwd = ""; importPwd = ""
            }
            is SettingsState.Error -> { snackbar.showSnackbar(s.msg); vm.clearState() }
            else -> {}
        }
    }

    val encExportPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        uri?.let { exportUri = it; showExportDlg = true }
    }
    val csvExportPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { vm.exportCsv(it) }
    }
    val txtExportPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        uri?.let { vm.exportTxt(it) }
    }
    val importPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { importUri = it; showImportDlg = true }
    }

    // ── Dialogs ───────────────────────────────────────────────────────────
    if (showChangePwd) {
        SvDialog(title = "Изменить пароль", icon = Icons.Default.Lock,
            onDismiss = { showChangePwd = false },
            confirmText = "Изменить", loading = state is SettingsState.Loading,
            onConfirm = { vm.changePassword(curPwd, newPwd, cfmPwd) }) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(Triple(curPwd,  "Текущий",    { v: String -> curPwd = v }),
                    Triple(newPwd,  "Новый",       { v: String -> newPwd = v }),
                    Triple(cfmPwd,  "Подтвердите", { v: String -> cfmPwd = v }))
                    .forEach { (v, label, onChange) ->
                        SvPasswordField(v, label, showPwds,
                            onToggle = if (label == "Текущий") ({ showPwds = !showPwds }) else null,
                            onChange = onChange)
                    }
            }
        }
    }

    if (showExportDlg && exportUri != null) {
        SvDialog(title = "Пароль резервной копии", icon = Icons.Default.EnhancedEncryption,
            onDismiss = { showExportDlg = false; exportPwd = "" },
            confirmText = "Экспортировать", loading = state is SettingsState.Loading,
            onConfirm = { exportUri?.let { vm.exportEncrypted(it, exportPwd) } }) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(SvColors.GoldSoft).padding(12.dp)) {
                    Text("Запомните этот пароль — без него файл не открыть!", color = SvColors.Gold, fontSize = 13.sp)
                }
                SvPasswordField(exportPwd, "Пароль экспорта", showExpPwd,
                    onToggle = { showExpPwd = !showExpPwd }, onChange = { exportPwd = it })
            }
        }
    }

    if (showImportDlg && importUri != null) {
        SvDialog(title = "Пароль файла", icon = Icons.Default.Download,
            onDismiss = { showImportDlg = false; importPwd = "" },
            confirmText = "Импортировать", loading = state is SettingsState.Loading,
            onConfirm = { importUri?.let { vm.import(it, importPwd) } }) {
            SvPasswordField(importPwd, "Пароль файла", showImpPwd,
                onToggle = { showImpPwd = !showImpPwd }, onChange = { importPwd = it })
        }
    }

    if (showMassRotate) {
        SvDialog(title = "Массовая смена паролей", icon = Icons.Default.Autorenew,
            iconTint = SvColors.Gold,
            onDismiss = { showMassRotate = false },
            confirmText = "Обновить пароли",
            onConfirm = { vm.massRotatePasswords(rotateProfile); showMassRotate = false }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(SvColors.GoldSoft).padding(12.dp)) {
                    Text("Новые пароли генерируются по настройкам каждой записи. Старые удаляются!",
                        color = SvColors.Gold, fontSize = 12.sp)
                }
                listOf(null to "Все профили", "Личное" to "Только личные", "Рабочее" to "Только рабочие")
                    .forEach { (v, l) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(rotateProfile == v, { rotateProfile = v })
                            Text(l, color = SvColors.TextPrimary)
                        }
                    }
            }
        }
    }

    if (showReset) {
        SvDialog(title = "Сбросить хранилище?", icon = Icons.Default.DeleteForever,
            iconTint = SvColors.Coral, onDismiss = { showReset = false },
            confirmText = "Удалить всё", confirmColor = SvColors.Coral,
            onConfirm = { showReset = false; vm.reset() }) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(SvColors.CoralSoft).padding(12.dp)) {
                Text("Все пароли будут удалены без возможности восстановления!", color = SvColors.Coral, fontSize = 13.sp)
            }
        }
    }

    Scaffold(
        containerColor = SvColors.BgDeep,
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ─────────────────────────────────────────────────
            Box(Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(SvColors.BgCard, SvColors.BgDeep)))
                .padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                        .background(SvColors.BlueSoft)
                        .border(1.dp, SvColors.Blue.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Settings, null, Modifier.size(20.dp), tint = SvColors.Blue)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Настройки", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = SvColors.TextPrimary)
                        Text("Secure Vault", fontSize = 12.sp, color = SvColors.TextSecond)
                    }
                }
            }

            // ── THEME SELECTOR ─────────────────────────────────────────
            SvSection("Оформление") {
                Column(Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Тема приложения", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = SvColors.TextPrimary)
                    Text("Изменение применяется мгновенно с плавным переходом",
                        fontSize = 12.sp, color = SvColors.TextSecond)
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            ThemePill(
                                mode     = mode,
                                selected = theme == mode,
                                modifier = Modifier.weight(1f),
                                onClick  = { vm.setTheme(mode) }
                            )
                        }
                    }
                }
                HorizontalDivider(color = SvColors.BorderSoft, thickness = 0.5.dp)
                SvSettingRow(
                    icon = Icons.Default.Fingerprint,
                    title = "Биометрия",
                    subtitle = "Отпечаток пальца / Face ID",
                    trailing = {
                        Switch(bio, { bio = it; vm.setBiometric(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor  = SvColors.BgDeep,
                                checkedTrackColor  = SvColors.Blue
                            ))
                    }
                )
            }

            // ── Security ───────────────────────────────────────────────
            SvSection("Безопасность") {
                SvSettingRow(Icons.Default.Lock, "Изменить мастер-пароль", "Сменить пароль входа",
                    onClick = { showChangePwd = true })
            }

            // ── Backup (encrypted) ─────────────────────────────────────
            SvSection("Резервная копия — зашифрованная") {
                SvSettingRow(Icons.Default.EnhancedEncryption, "Экспорт .svault",
                    "AES-256-GCM · только Secure Vault", iconTint = SvColors.Teal,
                    onClick = { encExportPicker.launch("backup_${System.currentTimeMillis()}.svault") })
                SvSettingRow(Icons.Default.Download, "Импорт .svault",
                    "Восстановить из зашифрованного файла", iconTint = SvColors.Teal,
                    onClick = { importPicker.launch(arrayOf("*/*")) })
            }

            // ── Plain export ───────────────────────────────────────────
            SvSection("Экспорт для просмотра") {
                SvSettingRow(Icons.Default.TableChart, "Экспорт CSV",
                    "Для Excel или Google Sheets (незашифрованный!)", iconTint = SvColors.Gold,
                    onClick = { csvExportPicker.launch("passwords_${System.currentTimeMillis()}.csv") })
                SvSettingRow(Icons.Default.TextSnippet, "Экспорт TXT",
                    "Читаемый текстовый формат (незашифрованный!)", iconTint = SvColors.Gold,
                    onClick = { txtExportPicker.launch("passwords_${System.currentTimeMillis()}.txt") })
            }

            // ── Management ─────────────────────────────────────────────
            SvSection("Управление паролями") {
                SvSettingRow(Icons.Default.Autorenew, "Массовая смена паролей",
                    "Регенерировать пароли по группам", iconTint = SvColors.Gold,
                    onClick = { showMassRotate = true })
            }

            // ── Danger zone ────────────────────────────────────────────
            SvSection("Опасная зона") {
                SvSettingRow(Icons.Default.DeleteForever, "Сбросить хранилище",
                    "Удалить все данные", iconTint = SvColors.Coral,
                    titleColor = SvColors.Coral, onClick = { showReset = true })
            }

            // ── About ──────────────────────────────────────────────────
            SvSection("О приложении") {
                SvSettingRow(Icons.Default.Shield, "Защита",
                    "AES-256-GCM · Android Keystore · PBKDF2 · Брутфорс-защита")
                SvSettingRow(Icons.Default.Info, "Версия", "Secure Vault 1.0")
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

// ── Theme pill card ───────────────────────────────────────────────────────────
@Composable
private fun ThemePill(
    mode: ThemeMode,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor  = when (mode) {
        ThemeMode.DARK   -> Color(0xFF0A1225)
        ThemeMode.LIGHT  -> Color(0xFFF4F7FF)
        ThemeMode.SYSTEM -> Color(0xFF181830)
    }
    val fgColor  = when (mode) {
        ThemeMode.DARK   -> Color(0xFFF0F4FF)
        ThemeMode.LIGHT  -> Color(0xFF0D1426)
        ThemeMode.SYSTEM -> SvColors.Blue
    }
    val accentColor = if (selected) SvColors.Blue else SvColors.Border

    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected)
                Brush.verticalGradient(listOf(SvColors.BlueSoft.copy(alpha = 0.5f), SvColors.BgElevated))
            else
                Brush.verticalGradient(listOf(SvColors.BgElevated, SvColors.BgElevated)))
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = accentColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Mini preview of the theme
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .border(0.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { i ->
                        Box(
                            Modifier
                                .width(if (i == 0) 22.dp else 14.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (i == 0) SvColors.Blue.copy(alpha = if (mode == ThemeMode.LIGHT) 0.6f else 0.9f)
                                    else fgColor.copy(alpha = 0.25f)
                                )
                        )
                    }
                }
            }
            Text(mode.emoji, fontSize = 16.sp)
            Text(mode.label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) SvColors.Blue else SvColors.TextSecond)
            if (selected) {
                Box(Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(SvColors.Blue))
            }
        }
    }
}

// ── Shared design components ──────────────────────────────────────────────────

@Composable
fun SvSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(
            title.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold,
            color = SvColors.TextSecond, letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 14.dp)
        )
        Box(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SvColors.BgElevated)
                .border(1.dp, SvColors.Border, RoundedCornerShape(16.dp))
        ) {
            Column(Modifier.fillMaxWidth()) { content() }
        }
    }
}

@Composable
fun SvSettingRow(
    icon: ImageVector, title: String, subtitle: String,
    iconTint: Color = SvColors.Blue,
    titleColor: Color = SvColors.TextPrimary,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() }, enabled = onClick != null,
        color = Color.Transparent, modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center) {
                Icon(icon, null, Modifier.size(18.dp), tint = iconTint)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = SvColors.TextSecond)
            }
            if (trailing != null) trailing()
            else if (onClick != null) Icon(Icons.Default.ChevronRight, null,
                Modifier.size(18.dp), tint = SvColors.TextMuted)
        }
    }
    HorizontalDivider(color = SvColors.BorderSoft, thickness = 0.5.dp,
        modifier = Modifier.padding(start = 66.dp))
}

@Composable
fun SvDialog(
    title: String, icon: ImageVector, iconTint: Color = SvColors.Blue,
    onDismiss: () -> Unit, confirmText: String, loading: Boolean = false,
    confirmColor: Color = SvColors.Blue,
    onConfirm: () -> Unit, content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SvColors.BgCard,
        titleContentColor = SvColors.TextPrimary,
        textContentColor = SvColors.TextSecond,
        icon  = { Icon(icon, null, tint = iconTint) },
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        text  = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { content() } },
        confirmButton = {
            Button(onConfirm, enabled = !loading,
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor),
                shape = RoundedCornerShape(10.dp)) {
                if (loading) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                else Text(confirmText)
            }
        },
        dismissButton = { TextButton(onDismiss) { Text("Отмена", color = SvColors.TextSecond) } }
    )
}

@Composable
fun SvPasswordField(
    value: String, label: String, showPwd: Boolean,
    onToggle: (() -> Unit)?, onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label, fontSize = 13.sp) },
        visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = if (onToggle != null) ({
            IconButton(onToggle) {
                Icon(if (showPwd) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    null, tint = SvColors.TextSecond)
            }
        }) else null,
        modifier = Modifier.fillMaxWidth(), singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = SvColors.Blue, unfocusedBorderColor = SvColors.Border,
            focusedContainerColor = SvColors.BgHighlight, unfocusedContainerColor = SvColors.BgElevated,
            focusedTextColor = SvColors.TextPrimary, unfocusedTextColor = SvColors.TextPrimary,
            focusedLabelColor = SvColors.Blue, unfocusedLabelColor = SvColors.TextSecond,
            cursorColor = SvColors.Blue
        )
    )
}
