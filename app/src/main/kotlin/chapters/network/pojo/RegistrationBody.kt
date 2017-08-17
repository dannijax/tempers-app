package chapters.network.pojo

import chapters.extension.toJson


class RegistrationBody {

    var email: String? = null
    var username: String? = null
    var password: String? = null
    var name: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var birthday: String? = null
    var phone: PhoneBean=PhoneBean()
    var devicePlatformID: String? = "2"
    var deviceLanguage: String? = "en"
    var description=""

    class PhoneBean {
        var code: String? = null
        var countryCode: String? = null
        var number: String? = null
    }


    override fun toString(): String {
        return this.toJson()
    }
}