package com.securevault.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.securevault.data.repository.Entry
import com.securevault.ui.theme.SvColors
import com.securevault.ui.theme.categoryColor
import com.securevault.ui.theme.glassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultListScreen(
    onAdd: () -> Unit, onEdit: (Long) -> Unit, onLock: () -> Unit,
    favOnly: Boolean = false, vm: VaultViewModel = hiltViewModel()
) {
    val entries      by (if (favOnly) vm.favorites else vm.entries).collectAsState()
    val query        by vm.query.collectAsState()
    val categories   by vm.categories.collectAsState()
    val profiles     by vm.profiles.collectAsState()
    val selectedCat  by vm.category.collectAsState()
    val selectedProf by vm.profile.collectAsState()
    val count        by vm.count.collectAsState()
    val expiredCount by vm.expiredCount.collectAsState()
    val showExpired  by vm.showExpiredOnly.collectAsState()
    var toDelete     by remember { mutableStateOf<Entry?>(null) }

    toDelete?.let { e ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            containerColor = SvColors.BgCard,
            title = { Text("Удалить запись?", color = SvColors.TextPrimary) },
            text  = { Text("«${e.title}» будет удалена без возможности восстановления.", color = SvColors.TextSecond) },
            confirmButton = { TextButton({ vm.delete(e.id); toDelete = null }) {
                Text("Удалить", color = SvColors.Coral) } },
            dismissButton = { TextButton({ toDelete = null }) { Text("Отмена", color = SvColors.TextSecond) } }
        )
    }

    Scaffold(
        containerColor = SvColors.BgDeep,
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(SvColors.BgCard, SvColors.BgDeep))
                    )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (favOnly) Icons.Default.Star else Icons.Default.VpnKey,
                        null, Modifier.size(22.dp), tint = SvColors.Blue
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (favOnly) "Избранное" else "Пароли",
                        fontWeight = FontWeight.Bold, fontSize = 20.sp,
                        color = SvColors.TextPrimary, modifier = Modifier.weight(1f)
                    )
                    // Expired badge
                    if (!favOnly && expiredCount > 0) {
                        IconButton({ vm.setShowExpiredOnly(!showExpired) }) {
                            BadgedBox(badge = {
                                Badge(containerColor = SvColors.Coral) {
                                    Text("$expiredCount", fontSize = 9.sp)
                                }
                            }) {
                                Icon(
                                    Icons.Default.Warning, null, Modifier.size(22.dp),
                                    tint = if (showExpired) SvColors.Coral else SvColors.Gold
                                )
                            }
                        }
                    }
                    // Count pill
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SvColors.BgHighlight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text("$count", fontSize = 12.sp, color = SvColors.TextSecond, fontWeight = FontWeight.Medium) }
                    Spacer(Modifier.width(8.dp))
                    IconButton({ vm.lock(); onLock() }) {
                        Icon(Icons.Default.Lock, null, Modifier.size(22.dp), tint = SvColors.TextSecond)
                    }
                }
            }
        },
        floatingActionButton = {
            if (!favOnly) {
                Box(
                    Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF3A6EE0), Color(0xFF5B8FF9))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onAdd,
                        containerColor = Color.Transparent,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier.fillMaxSize()
                    ) { Icon(Icons.Default.Add, null, tint = Color.White) }
                }
            }
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            // ── Filters ────────────────────────────────────────────────
            if (!favOnly) {
                Spacer(Modifier.height(8.dp))
                // Search bar
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SvColors.BgElevated)
                        .border(1.dp, SvColors.Border, RoundedCornerShape(14.dp))
                ) {
                    OutlinedTextField(
                        value = query, onValueChange = vm::setQuery,
                        placeholder = { Text("Поиск по названию...", color = SvColors.TextMuted, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(20.dp), tint = SvColors.TextSecond) },
                        trailingIcon = { if (query.isNotEmpty()) IconButton({ vm.setQuery("") }) {
                            Icon(Icons.Default.Close, null, Modifier.size(18.dp), tint = SvColors.TextSecond) } },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = SvColors.TextPrimary,
                            unfocusedTextColor = SvColors.TextPrimary,
                        )
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Profile filter
                if (profiles.size > 1 || selectedProf != null) {
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(null to "Все", "Личное" to "👤 Личное", "Рабочее" to "💼 Рабочее").forEach { (val_, label) ->
                            val selected = selectedProf == val_
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (selected)
                                            Brush.horizontalGradient(listOf(SvColors.Blue.copy(alpha = 0.3f), SvColors.Teal.copy(alpha = 0.15f)))
                                        else Brush.horizontalGradient(listOf(SvColors.BgElevated, SvColors.BgElevated))
                                    )
                                    .border(1.dp,
                                        if (selected) SvColors.Blue.copy(alpha = 0.6f) else SvColors.Border,
                                        RoundedCornerShape(20.dp))
                                    .combinedClickable(onClick = { vm.setProfile(if (selected && val_ != null) null else val_) })
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(label, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selected) SvColors.Blue else SvColors.TextSecond)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Category chips
                if (categories.isNotEmpty()) {
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val chips = listOf(null to "Все") + categories.map { it to it }
                        chips.forEach { (val_, label) ->
                            val selected = selectedCat == val_
                            val accent = if (val_ != null) categoryColor(val_) else SvColors.Blue
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (selected) accent.copy(alpha = 0.18f)
                                        else SvColors.BgElevated
                                    )
                                    .border(1.dp,
                                        if (selected) accent.copy(alpha = 0.6f) else SvColors.Border,
                                        RoundedCornerShape(20.dp))
                                    .combinedClickable(onClick = { vm.setCategory(if (selected && val_ != null) null else val_) })
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selected) accent else SvColors.TextSecond)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                // Expired banner
                if (showExpired && expiredCount > 0) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SvColors.CoralSoft)
                            .border(1.dp, SvColors.Coral.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, Modifier.size(18.dp), tint = SvColors.Coral)
                            Spacer(Modifier.width(8.dp))
                            Text("Просрочены: $expiredCount паролей", fontSize = 13.sp,
                                color = SvColors.Coral, modifier = Modifier.weight(1f))
                            TextButton({ vm.setShowExpiredOnly(false) }) {
                                Text("Все", color = SvColors.TextSecond, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // ── List ───────────────────────────────────────────────────
            if (entries.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(
                            Modifier.size(80.dp).clip(CircleShape)
                                .background(SvColors.BgElevated)
                                .border(1.dp, SvColors.Border, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (favOnly) Icons.Default.StarBorder else Icons.Default.VpnKey,
                                null, Modifier.size(36.dp), tint = SvColors.TextMuted
                            )
                        }
                        Text(
                            when {
                                favOnly -> "Нет избранных"
                                query.isNotEmpty() -> "Ничего не найдено"
                                showExpired -> "Всё в порядке — просрочений нет"
                                else -> "Пусто. Нажмите + чтобы добавить"
                            },
                            color = SvColors.TextSecond, fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entries, key = { it.id }) { e ->
                        EntryCard(e, { onEdit(e.id) }, { vm.toggleFavorite(e) }, { toDelete = e })
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntryCard(e: Entry, onClick: () -> Unit, onFav: () -> Unit, onDelete: () -> Unit) {
    var menu by remember { mutableStateOf(false) }
    val accent = categoryColor(e.category)
    val isExpired = e.isPasswordExpired
    val daysLeft = e.daysUntilExpiry

    Box(
        Modifier
            .fillMaxWidth()
            .glassCard(cornerRadius = 16.dp)
            .combinedClickable(onClick = onClick, onLongClick = { menu = true })
    ) {
        // Left accent bar
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .background(
                    if (isExpired) SvColors.Coral
                    else Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.3f)))
                )
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(accent.copy(alpha = 0.25f), accent.copy(alpha = 0.08f))
                        )
                    )
                    .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    e.title.take(1).uppercase(),
                    fontWeight = FontWeight.Bold, fontSize = 20.sp, color = accent
                )
            }

            Spacer(Modifier.width(14.dp))

            // Content
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(e.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                        color = SvColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false))
                    if (e.profile == "Рабочее") {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SvColors.TealSoft)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) { Text("Работа", fontSize = 9.sp, color = SvColors.Teal, fontWeight = FontWeight.Medium) }
                    }
                }
                if (e.username.isNotEmpty()) {
                    Text(e.username, fontSize = 12.sp, color = SvColors.TextSecond,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accent.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) { Text(e.category, fontSize = 10.sp, color = accent, fontWeight = FontWeight.Medium) }

                    when {
                        isExpired -> Box(
                            Modifier.clip(RoundedCornerShape(4.dp)).background(SvColors.CoralSoft)
                                .border(0.5.dp, SvColors.Coral.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) { Text("Смените!", fontSize = 10.sp, color = SvColors.Coral, fontWeight = FontWeight.Bold) }
                        daysLeft in 1..14 -> Box(
                            Modifier.clip(RoundedCornerShape(4.dp)).background(SvColors.GoldSoft)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) { Text("${daysLeft}д.", fontSize = 10.sp, color = SvColors.Gold) }
                    }
                }
                // Emoji hint
                val emojis = HintVisualizer.toEmojis(e.hintKeywords)
                if (emojis.isNotEmpty()) {
                    Text(emojis, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // Right side
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onFav, Modifier.size(36.dp)) {
                    Icon(
                        if (e.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        null, Modifier.size(18.dp),
                        tint = if (e.isFavorite) SvColors.Gold else SvColors.TextMuted
                    )
                }
                DropdownMenu(menu, { menu = false },
                    containerColor = SvColors.BgElevated) {
                    DropdownMenuItem(
                        text = { Text("Удалить", color = SvColors.Coral) },
                        onClick = { menu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = SvColors.Coral) }
                    )
                }
            }
        }
    }
}
