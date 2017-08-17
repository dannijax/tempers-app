package com.tempry


class s {


    /**
     * email : r.dubovoi@gmail.com
     * username : Snaketoo
     * password : a1234567
     * name : Roman Dubovoi
     * firstname : Roman
     * lastname : Dubovoi
     * birthday : 29.04.1983
     * phone : {"code":"67","countryCode":"380","number":"2570000"}
     * devicePlatformID : 2
     * deviceLanguage : en
     */

    var email: String? = null
    var username: String? = null
    var password: String? = null
    var name: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var birthday: String? = null
    var phone: PhoneBean? = null
    var devicePlatformID: String? = null
    var deviceLanguage: String? = null

    class PhoneBean {

        var code: String? = null
        var countryCode: String? = null
        var number: String? = null
    }
}
