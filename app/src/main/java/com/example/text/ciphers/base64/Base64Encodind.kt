package nadirian.hamlet.android.encdecapp.model.base64

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

object Base64Encoding {


    @RequiresApi(Build.VERSION_CODES.O)
    fun encode(originalString: String): String {
        return Base64.getEncoder().encodeToString(originalString.toByteArray())
    }
    fun decode(encodedString:String):String{
        return String(Base64.getDecoder().decode(encodedString))
    }
}