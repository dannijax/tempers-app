package chapters.network.pojo

import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject


class SendSettingPojo {


    val notificationList:HashMap<String,Int> = hashMapOf()


    @SerializedName("1")
    var class1=1
    @SerializedName("2")
    var class2=1
    @SerializedName("3")
    var class3=1
    @SerializedName("4")
    var class4=1
    @SerializedName("5")
    var class5=1
    @SerializedName("6")
    var class6=1
    @SerializedName("7")
    var class7=1
    @SerializedName("8")
    var class8=1
    @SerializedName("9")
    var class9=1


    class SendPrivatePojo{
        @SerializedName("1")
        var class1=1
        @SerializedName("2")
        var class2=1
    }

}