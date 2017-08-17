package chapters.database.pojo.address

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class AddressComponents: Serializable {
    @SerializedName("long_name")
    val longName = ""
    @SerializedName("short_name")
    val shortName = ""
}