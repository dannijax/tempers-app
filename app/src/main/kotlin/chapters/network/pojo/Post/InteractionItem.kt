package chapters.network.pojo.Post

import chapters.network.pojo.User
import java.io.Serializable


class InteractionItem : Serializable {

    var value = ""
    val of = ""
    val self = ""
    val by: User? = null
}
