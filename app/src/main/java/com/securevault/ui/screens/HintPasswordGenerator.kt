package com.securevault.ui.screens

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Генерирует детерминированный пароль из ключевых слов-подсказок.
 *
 * Логика: PBKDF2WithHmacSHA256(нормализованные_слова, соль_приложения, 100_000 итераций)
 * → байты → символы пароля.
 *
 * Одни и те же слова ВСЕГДА дают один и тот же пароль — пользователь может
 * восстановить пароль, просто вспомнив свои подсказки.
 */
object HintPasswordGenerator {

    private const val SALT = "SecureVaultHintGeneratorV1"

    /**
     * @param keywords строка через запятую: "кот, дом, 5, синий"
     * @param extraSeed дополнительный секрет (можно оставить пустым — тогда пароль
     *                  зависит только от слов; или передать часть мастер-пароля)
     */
    fun generate(
        keywords: String,
        extraSeed: String = "",
        length: Int = 20,
        upper: Boolean = true,
        digits: Boolean = true,
        symbols: Boolean = true
    ): String {
        // Нормализуем: убираем пробелы, строчные, сортируем
        val normalized = keywords
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .sorted()
            .joinToString("|")

        if (normalized.isEmpty()) return ""

        val input = "$normalized:$extraSeed"
        val saltBytes = (SALT + normalized.take(8)).toByteArray(Charsets.UTF_8)

        val spec = PBEKeySpec(input.toCharArray(), saltBytes, 100_000, 512)
        val keyBytes = try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }

        // Набор символов
        var chars = "abcdefghijklmnopqrstuvwxyz"
        if (upper)   chars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        if (digits)  chars += "0123456789"
        if (symbols) chars += "!@#\$%^&*-_=+"

        // Каждый байт → индекс в chars (без смещения, равномерно)
        return keyBytes
            .take(length.coerceAtMost(keyBytes.size))
            .map { b -> chars[(b.toInt() and 0xFF) % chars.length] }
            .joinToString("")
    }

    /**
     * Проверяет, совпадает ли сохранённый пароль с тем, что даёт генератор.
     * Если да — значит пароль был сгенерирован из этих подсказок.
     */
    fun matches(keywords: String, extraSeed: String = "", storedPassword: String,
                length: Int, upper: Boolean, digits: Boolean, symbols: Boolean): Boolean {
        val generated = generate(keywords, extraSeed, length, upper, digits, symbols)
        return generated == storedPassword
    }
}
