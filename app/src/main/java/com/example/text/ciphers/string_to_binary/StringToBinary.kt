package nadirian.hamlet.android.encdecapp.model.string_to_binary

import java.util.stream.Collectors


object StringToBinary {
    fun convertStringToBinary(input: String): String? {
        if (input.isEmpty()) return ""

        val result = StringBuilder()
        val chars = input.toCharArray()
        try {
            for (aChar in chars) {
                result.append(
                    String.format("%8s", Integer.toBinaryString(aChar.code))
                        .replace(" ".toRegex(), "0") // zero pads
                )
            }
        } catch (e: Exception) {
            return "Ошибка при конвертации: ${e.message}"
        }
        return prettyBinary(result.toString(), 8, " ")
    }

    fun prettyBinary(binary: String, blockSize: Int, separator: String?): String? {
        val result: MutableList<String> = ArrayList()
        var index = 0
        while (index < binary.length) {
            result.add(binary.substring(index, Math.min(index + blockSize, binary.length)))
            index += blockSize
        }
        return result.stream().collect(Collectors.joining(separator))
    }

    fun binaryToString(input: String): String {
        if (input.isBlank()) return "Ошибка: пустой ввод"
        if (!input.matches(Regex("[01\\s]+"))) return "Ошибка: недопустимые символы (только 0, 1 и пробелы). Например, 01000001"

        val cleanedInput = input.replace(" ", "")
        if (cleanedInput.length % 8 != 0) {
            return "Ошибка: длина двоичной строки должна быть кратна 8"
        }

        val result = StringBuilder()

        try {
            var index = 0
            while (index < cleanedInput.length) {
                val byteStr = cleanedInput.substring(index, index + 8)
                val num = byteStr.toInt(2)
                result.append(num.toChar())
                index += 8
            }
        } catch (e: Exception) {
            return "Ошибка при декодировании: ${e.message}"
        }

        return result.toString()
    }

    fun processBinary(text: String, isEncryption: Boolean): String {
        return if (isEncryption) {
            convertStringToBinary(text) ?: "Conversion failed"
        } else {
            binaryToString(text)
        }
    }
}