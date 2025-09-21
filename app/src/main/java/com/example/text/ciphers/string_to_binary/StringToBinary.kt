package nadirian.hamlet.android.encdecapp.model.string_to_binary

import java.util.stream.Collectors


object StringToBinary {
    fun convertStringToBinary(input: String): String? {
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

    fun strToBinary(input: String): String {
        var s = " "
        var index = 0
        var re = Regex("[A-Za-z]+")

        while (index < input.length) {
            val temp = input.substring(index, index + 8)
            if (!temp.matches(re)) {
                val num = temp.toInt(2)
                val letter = num.toChar()
                s = s + letter
                index += 9
            }

        }
        return s
    }

     fun processBinary(text: String, isEncryption: Boolean): String {
        return if (isEncryption) {
            StringToBinary.convertStringToBinary(text) ?: "Conversion failed"
        } else {
            StringToBinary.strToBinary(text)
        }
    }
}