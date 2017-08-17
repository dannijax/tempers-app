package chapters.network.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ResponceSuccess : Serializable {
    @SerializedName("userID")
    val userId = 0
    val response = ""
    val email = ""
    val token = ""
    var messageID = 0
    @SerializedName("date")
    val timestamp = ""
    val body: ResponceBody? = null
}

class ResponceBody {
    val daysLeft = "2"

}