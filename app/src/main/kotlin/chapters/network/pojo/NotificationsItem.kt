package chapters.network.pojo

import chapters.extension.SMALL_PHOTO
import com.google.gson.annotations.SerializedName


class NotificationsItem {

    @SerializedName("ID")
    val id = "1"
    @SerializedName("classID")
    var classId = ""
    @SerializedName("postID")
    val postId=""
    @SerializedName("userID")
    var userId=""
    var name=""
    var version=""
    val seen=""
    val seenContent=""
    var date=""
    val postSenderName=""
    val temp=""
    val postAttachment=""
    var content=""
    val photo
    get() = SMALL_PHOTO.format(userId.toInt(),version.toInt())
}