package chapters.database.pojo.address

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*


class AddressItem :Serializable {

    @SerializedName("address_components")
    val addressComponents: ArrayList<AddressComponents>? = null
    val geometry: AddressGeometry? = null
    @SerializedName("formatted_address")
    var fullAddress = ""

    fun getAddress():String?{
        return addressComponents?.getOrNull(1)?.shortName+" "+addressComponents?.firstOrNull()?.shortName
    }

    fun getCity():String?{
        return addressComponents?.getOrNull(3)?.shortName
    }
}