package chapters.network.pojo

import chapters.extension.POST_IMAGE
import chapters.extension.POST_PLACEHOLDER
import chapters.extension.POST_VIDEO
import chapters.network.pojo.Post.Interaction
import chapters.network.pojo.Post.PostId
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PostItem : Serializable {


    @SerializedName("content")
    val text = ""
    private val attachment = ""
    val date = ""
    var timeLeft = ""
    private val post: PostId? = null
    var id = ""
        get() {
            return post?.ID ?: ""
        }

    var isImage = true
        get() {
            return attachment == "image," || attachment == "tag,image,"
        }

    var isVideo = true
        get() {
            return attachment == "video," || attachment == "tag,video,"
        }

    var isTag = true
        get() {
            return attachment == "tag,"
        }


    var image = ""
        get() {
            if (interaction?.retemp?.of != "0") {
                return POST_IMAGE.format(interaction?.retemp?.of)

            }
            return POST_IMAGE.format(id)
        }


    var video = ""
        get() {
            if (interaction?.retemp?.of != "0") {
                return POST_VIDEO.format(interaction?.retemp?.of)

            }
            return POST_VIDEO.format(id)
        }

    var imagePlaceHolder = ""
        get() {
            if (interaction?.retemp?.of != "0") {
                return POST_PLACEHOLDER.format(interaction?.retemp?.of)

            }
            return POST_PLACEHOLDER.format(id)
        }

    @SerializedName("sender")
    val userSender: User? = null

    val interaction: Interaction? = null

    override fun toString(): String {
        return "PostItem(text='$text', attachment='$attachment', date='$date', post=$post, userSender=$userSender)"
    }

}