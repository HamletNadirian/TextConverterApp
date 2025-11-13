package nadirian.hamlet.android.encdecapp.model.string_to_hex

import org.apache.commons.codec.binary.Hex
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern


object StringToHex {

    fun processHex(text: String, isEncryption: Boolean): String {
        return if (isEncryption) {
            text.toByteArray().joinToString("") { "%02x".format(it) }
        } else {
            convertHexToStringSafe(text)
        }
    }

    fun convertStringToHex(str: String): String {
        if (str.isEmpty()) return ""
        val chars: CharArray = Hex.encodeHex(str.toByteArray(StandardCharsets.UTF_8))
        return String(chars)
    }

    private val HEXADECIMAL_PATTERN: Pattern = Pattern.compile("\\p{XDigit}+")

    private fun isHexadecimal(input: String): Boolean {
        if (input.isEmpty()) return false
        val cleanInput = input.replace("\\s".toRegex(), "")
        return HEXADECIMAL_PATTERN.matcher(cleanInput).matches()
    }

    fun convertHexToString(input: String?): String {
        if (input.isNullOrBlank()) return ""

        val hex = input.replace("\\s".toRegex(), "")
        return if (isHexadecimal(hex)) {
            try {
                val bytes = Hex.decodeHex(hex.toCharArray())
                String(bytes, StandardCharsets.UTF_8)
            } catch (e: Exception) {
                "" // Возвращаем пустую строку вместо сообщения об ошибке
            }
        } else {
            "" // Возвращаем пустую строку вместо сообщения об ошибке
        }
    }

    fun convertHexToStringSafe(input: String?): String {
        if (input.isNullOrBlank()) return ""

        try {
            // Удаляем все пробелы и не-hex символы
            val cleanHex = input.replace("\\s".toRegex(), "")
                .filter { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }

            if (cleanHex.isEmpty()) return ""

            // Проверяем, что длина четная (hex пары)
            val validHex = if (cleanHex.length % 2 != 0) {
                cleanHex.dropLast(1) // Удаляем последний непарный символ
            } else {
                cleanHex
            }

            if (validHex.isEmpty()) return ""

            // Разбиваем на пары и конвертируем только валидные
            val result = StringBuilder()
            var index = 0

            while (index < validHex.length) {
                if (index + 2 <= validHex.length) {
                    val hexPair = validHex.substring(index, index + 2)
                    try {
                        val charCode = hexPair.toInt(16)
                        result.append(charCode.toChar())
                    } catch (e: Exception) {
                        // Пропускаем некорректную пару
                    }
                    index += 2
                } else {
                    break
                }
            }

            return result.toString()
        } catch (e: Exception) {
            return ""
        }
    }

    // Старая версия для обратной совместимости
    fun convertHexToStringStrict(input: String?): String {
        if (input.isNullOrBlank()) return ""

        val hex = input.replace("\\s".toRegex(), "")
        return if (isHexadecimal(hex) && hex.length % 2 == 0) {
            try {
                val bytes = Hex.decodeHex(hex.toCharArray())
                String(bytes, StandardCharsets.UTF_8)
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
    }
}