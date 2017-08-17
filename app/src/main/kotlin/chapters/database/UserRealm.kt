package chapters.database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class UserRealm : RealmObject() {

    @PrimaryKey
    @SerializedName("userID")
    open var userId = 0
    @SerializedName("username")
    open var userName = ""
    @SerializedName("name")
    open var fullName = ""
    @SerializedName("firstname")
    open var firstName = ""
    @SerializedName("lastname")
    open var lastName = ""
    open var version = 0
    open var email = ""
    open var birthday = ""
    open var phoneCode = ""
    open var phoneCountryCode = ""
    open var phoneNumber = ""
    open var description = ""
    open var stars = 0
    @SerializedName("posts")
    open var post = 0
    open var followers = 0
    open var following = 0
    open var isFollowed = 0
    open var photo = ""
    get() {
        val usId=SharedSetting.getUserId()
        val s= "https://tempry.co/api/data/image/profile/$usId-$version.jpg"
        return s
    }
    override fun toString(): String {
        return "UserRealm(userId=$userId, userName='$userName', fullName='$fullName', firstName='$firstName', lastName='$lastName', version='$version', email='$email', birthday='$birthday', phoneCode='$phoneCode', phoneCountryCode='$phoneCountryCode', phoneNumber='$phoneNumber', description='$description', stars=$stars, post=$post, followers=$followers, following=$following, isFollowed=$isFollowed)"
    }


}