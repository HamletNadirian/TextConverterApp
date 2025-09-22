package nadirian.hamlet.android.encdecapp.model.american_standard_code_for_information_interchange

object ASCIIEncryptor {
    fun processASCII(text: String, isEncryption: Boolean): String {
        return if (isEncryption) {
            stringToACII(text)
        } else {
            asciiToString(text)
        }
    }
    fun asciiToString(str: String): String {
        // Разделяем входную строку на массив чисел по пробелам
        val asciiCodes = str.split(" ")
        var resultString = ""

        for (code in asciiCodes) {
            try {
                val num = code.toInt()
                if (num in 32..122) {
                    resultString += num.toChar()
                }
            } catch (e: NumberFormatException) {
                // Игнорируем некорректные числа
                continue
            }
        }

        return resultString
    }
     fun stringToACII(string: String): String {
        var arraylist = ArrayList<Int>()
        for (c in string) {
            arraylist.add(c.code)
        }
        return arraylist.joinToString(" ")
    }
}