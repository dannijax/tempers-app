package chapters.network.pojo

import com.google.gson.Gson


class PrivacyList {
    val privacyList:HashMap<String,Int> = HashMap()
    override fun toString(): String {
        return Gson().toJson(this)
    }


}