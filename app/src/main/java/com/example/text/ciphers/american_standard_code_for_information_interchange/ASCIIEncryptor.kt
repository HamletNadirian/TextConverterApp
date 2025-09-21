package nadirian.hamlet.android.encdecapp.model.american_standard_code_for_information_interchange

object ASCIIEncryptor {
    fun processASCII(text: String, isEncryption: Boolean): String {
        return if (isEncryption) {
            ASCIIEncryptor.stringToACII(text)
        } else {
            ASCIIEncryptor.asciiToString(text)
        }
    }
       fun asciiToString(str: String):String {
        var num = 0
        var resultString = ""
        var codeZeroOfBytes = '0'.code
        for (element in str) {
            num = num * 10 + (element.code - codeZeroOfBytes)
            if (num in 32..122) {
                val ch = num.toChar()
                num = 0
                resultString+=ch
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