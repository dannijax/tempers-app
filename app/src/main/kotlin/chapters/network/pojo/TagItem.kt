package chapters.network.pojo

import chapters.extension.SMALL_PHOTO
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TagItem:Serializable {

    @SerializedName("userID")
    var userId = -1
    private val ID = -1
    val version = 0
    var validId = 0
        get() {
            if (userId > -1) return userId
            return ID
        }
    val name = ""
    var smallPhoto = ""
        get() = SMALL_PHOTO.format(validId, version)
}