package chapters.network.pojo

import com.google.gson.Gson
import java.io.Serializable


class PhonePojo :Serializable{
    var code = ""
    var countryCode = ""
    var number = ""
    override fun toString(): String {
        return Gson().toJson(this)
    }

}