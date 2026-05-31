package com.securevault.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securevault.data.repository.Entry
import com.securevault.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(private val repo: VaultRepository) : ViewModel() {
    private val _entry = MutableStateFlow<Entry?>(null)
    val entry: StateFlow<Entry?> = _entry

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    /** true = пароль был сгенерирован из подсказок (подсвечиваем иначе) */
    private val _hintGeneratedPwd = MutableStateFlow(false)
    val hintGeneratedPwd: StateFlow<Boolean> = _hintGeneratedPwd

    fun load(id: Long) { viewModelScope.launch { _entry.value = repo.getById(id) } }

    fun save(
        id: Long, title: String, profile: String, category: String,
        username: String, password: String, url: String, notes: String,
        hintText: String, hintKeywords: String,
        changeIntervalDays: Int, isFavorite: Boolean,
        genLength: Int, genUpper: Boolean, genDigits: Boolean, genSymbols: Boolean
    ) {
        if (title.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            repo.save(id, title, profile, category, username, password, url, notes,
                hintText, hintKeywords, changeIntervalDays, isFavorite,
                genLength, genUpper, genDigits, genSymbols)
            _saved.value = true
        }
    }

    /** Генерирует случайный пароль по настройкам */
    fun generateRandom(len: Int = 20, upper: Boolean = true,
                       digits: Boolean = true, symbols: Boolean = true): String {
        var chars = "abcdefghijklmnopqrstuvwxyz"
        if (upper)   chars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        if (digits)  chars += "0123456789"
        if (symbols) chars += "!@#\$%^&*()-_=+[]{}|;:,.<>?"
        return (1..len).map { chars.random() }.joinToString("")
    }

    /**
     * Генерирует ДЕТЕРМИНИРОВАННЫЙ пароль из ключевых слов подсказки.
     * Одни и те же слова → всегда один и тот же пароль.
     * Пользователь может восстановить пароль, просто вспомнив слова.
     */
    fun generateFromHints(
        keywords: String,
        extraSeed: String = "",
        len: Int = 20, upper: Boolean = true,
        digits: Boolean = true, symbols: Boolean = true
    ): String {
        _hintGeneratedPwd.value = true
        return HintPasswordGenerator.generate(keywords, extraSeed, len, upper, digits, symbols)
    }

    fun clearHintFlag() { _hintGeneratedPwd.value = false }
}
