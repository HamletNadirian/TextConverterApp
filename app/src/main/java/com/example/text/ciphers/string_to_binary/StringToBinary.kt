package nadirian.hamlet.android.encdecapp.model.string_to_binary

import java.util.stream.Collectors

object StringToBinary {
    fun convertStringToBinary(input: String): String? {
        if (input.isEmpty()) return ""

        val result = StringBuilder()
        val chars = input.toCharArray()
        for (aChar in chars) {
            result.append(
                String.format("%8s", Integer.toBinaryString(aChar.code))
                    .replace(" ".toRegex(), "0") // zero pads
            )
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
        if (input.isEmpty()) return ""

        // Удаляем все пробелы, чтобы получить чистую бинарную строку
        val cleanBinary = input.replace(" ", "")

        // Если после удаления пробелов строка пустая
        if (cleanBinary.isEmpty()) return ""

        val result = StringBuilder()
        var index = 0

        while (index < cleanBinary.length) {
            // Проверяем, что осталось минимум 8 символов
            if (index + 8 <= cleanBinary.length) {
                val byteString = cleanBinary.substring(index, index + 8)

                // Проверяем, что это валидная бинарная строка (только 0 и 1)
                if (byteString.matches(Regex("[01]+"))) {
                    try {
                        val charCode = byteString.toInt(2)
                        result.append(charCode.toChar())
                    } catch (e: Exception) {
                        // Пропускаем некорректный байт
                    }
                }
                // Если байт невалидный, просто пропускаем его
                index += 8
            } else {
                // Если осталось меньше 8 символов - пропускаем хвост
                break
            }
        }

        return result.toString()
    }

    fun processBinary(text: String, isEncryption: Boolean): String {
        return try {
            if (isEncryption) {
                convertStringToBinary(text) ?: ""
            } else {
                binaryToString(text)
            }
        } catch (e: Exception) {
            ""
        }
    }
}