package nadirian.hamlet.android.encdecapp.model.string_to_hex

import org.apache.commons.codec.binary.Hex
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

object StringToHex {

     fun processHex(text: String, isEncryption: Boolean): String {
        return if (isEncryption) {
            text.toByteArray().joinToString("") { "%02x".format(it) }
        } else {
            text.chunked(2).map { it.toInt(16).toChar() }.joinToString("")
        }
    }


    fun convertStringToHex(str: String): String? {
        val chars: CharArray = Hex.encodeHex(str.toByteArray(StandardCharsets.UTF_8))
        return String(chars)
    }
    private val HEXADECIMAL_PATTERN: Pattern = Pattern.compile("\\p{XDigit}+")

    private fun isHexadecimal(input: String): Boolean {
        val matcher: Matcher = HEXADECIMAL_PATTERN.matcher(input)
        return matcher.matches()
    }
    fun convertHexToString(input: String?): String {
        if (input.isNullOrBlank()) return "Invalid Hex format"

        val hex = input.replace("\\s".toRegex(), "")
        return if (isHexadecimal(hex)) {
            try {
                val bytes = Hex.decodeHex(hex.toCharArray())
                String(bytes, StandardCharsets.UTF_8)
            } catch (e: Exception) {
                "Invalid Hex format"
            }
        } else {
            "Invalid Hex format"
        }
    }


}