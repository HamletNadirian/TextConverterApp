package nadirian.hamlet.android.encdecapp.model.base64

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Base64

object Base64Encoding {

    @RequiresApi(Build.VERSION_CODES.O)
    fun encode(originalString: String): String {
        return Base64.getEncoder().encodeToString(originalString.toByteArray())
    }

    @SuppressLint("NewApi")
    fun decode(encodedString:String):String{
        return try {
            String(Base64.getDecoder().decode(encodedString))
        } catch (e: Exception) {
            "Некорректный Base64 формат"
        }
    }
}