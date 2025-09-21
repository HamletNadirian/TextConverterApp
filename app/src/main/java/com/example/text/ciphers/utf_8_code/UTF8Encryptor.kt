package nadirian.hamlet.android.encdecapp.model.utf_8_code

object UTF8Encryptor {
    private val charset = Charsets.UTF_8

     fun stringToUTF8(string: String): String {
        val byteArray = string.toByteArray(charset)
        return (byteArray).joinToString()
    }

    fun decodeUTF8ToString(string: String): String {
        val splitString = string.split(",").map { it.trim() }
        val byteList = ArrayList<Byte>()
        for (s in splitString) {
            val b = s.toByteOrNull()
            if (b == null) {

                return ""
            }
            byteList.add(b)
        }
        val byteArray = byteList.toByteArray()
        return String(byteArray, charset)  // Правильно декодируем
    }



}