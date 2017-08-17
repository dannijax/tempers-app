package chapters.network.pojo

import chapters.extension.LARGE_PHOTO
import chapters.extension.MEDDIUM_PHOTO
import chapters.extension.ORIGINAL_PHOTO
import chapters.extension.SMALL_PHOTO
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class User : Serializable {

    @SerializedName("userID")
    var userId = -1
    private val ID = -1
    val name = "Test"
    val version = 0
    val posts = 2
    val stars = 4
    var followers = 0
    var following = 0
    var isFollowed = 0
    var description = ""
    var validId = 0
        get() {
            if (userId > -1) return userId
            return ID
        }
    var originalPhoto = ""
        get() = ORIGINAL_PHOTO.format(validId, version)
    var largePhoto = ""
        get() = LARGE_PHOTO.format(validId, version)
    var meddiumPhoto = ""
        get() = MEDDIUM_PHOTO.format(validId, version)
    var smallPhoto = ""
        get() = SMALL_PHOTO.format(validId, version)

    override fun toString(): String {
        return "User(userId=$userId, ID=$ID, name='$name', version=$version, posts=$posts, stars=$stars, followers=$followers, following=$following, isFollowed=$isFollowed, description='$description')"
    }



}
