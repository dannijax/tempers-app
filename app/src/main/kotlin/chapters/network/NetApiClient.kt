package chapters.network

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import chapters.database.*
import chapters.database.pojo.address.AddressItem
import chapters.database.pojo.address.AddressResponse
import chapters.extension.*
import chapters.instruments.VideoUtils
import chapters.network.pojo.*
import chapters.network.pojo.Post.UserPost
import chapters.network.utils.applySchedulers
import chapters.utils.location.RxLocation
import com.google.android.gms.maps.model.LatLng
import com.hbb20.CountryCodePicker
import com.tempry.BuildConfig
import com.tempry.R
import filehelpers.FileClassUtils
import filehelpers.Sha512
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import rx.Observable
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


object NetApiClient {

    private val netApiGoogle: NetApi = ServiceGenerator("http://maps.googleapis.com").netApi

    private val netApi: NetApi = ServiceGenerator(BuildConfig.ENDPOINT).netApi

    fun getAddressFromLatLng(latLng: LatLng?): Observable<AddressItem?> {
        val locale = Locale.getDefault().country.toLowerCase()
        return netApiGoogle.getAddressFromLatLng("" + latLng?.latitude + "," + latLng?.longitude, lang = locale)
                .doOnNext {
                    it.results?.firstOrNull()?.geometry?.location?.lng = latLng?.longitude ?: 0.0
                    it.results?.firstOrNull()?.geometry?.location?.lat = latLng?.latitude ?: 0.0
                }
                .map { it.results?.firstOrNull() }
                .compose(applySchedulers<AddressItem?>())
    }

    fun getLatLngFromName(text: String?): Observable<AddressItem?> {
        val locale = Locale.getDefault().country.toLowerCase()
        return netApiGoogle.getLatLngFromAddress(text)
                .compose(applySchedulers<AddressResponse>())
                .map { it.results?.firstOrNull() }
    }


    fun checkToken(): Observable<Boolean> {
        return netApi.checkToken()
                .compose(applySchedulers<ResponceSuccess>())
                .map {
                    it.response != "negative"
                }
    }

    fun registration(map: RegistrationBody, c: Context): Observable<Boolean> {
        return netApi.registration(map)
                .compose(applySchedulers<ResponceSuccess>())
                .map {
                    if (it.response.isNotEmpty()) {
                        c.showSnackBar(c.getString(R.string.error_reg).format(it.response))
                    }
                    SharedSetting.setUserToken(it.token)
                    SharedSetting.setUserId(it.userId)
                    it.token.isNotEmpty()
                }
                .map { if (!it) Exception("reg_fail") }
                .flatMap { getMyUserInfo() }
    }

    fun registerOneSignal(id: String?): Observable<ResponceSuccess> {

        return netApi.registerOneSignal(id)
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun editProfile(name: String, lastname: String, birthday: String,
                    description: String, uri: Uri?, c: Context): Observable<ResponceSuccess> {

        val user = UserProvider.getMyProfile()!!
        val map = RegistrationBody()
        map.email = user.email
        map.username = user.userName
        map.password = null
        map.firstname = name
        map.lastname = lastname
        map.birthday = birthday
        map.description = description
        map.name = map.firstname + " " + map.lastname
        map.phone.code = user.phoneCode
        map.phone.countryCode = user.phoneCountryCode
        map.phone.number = user.phoneNumber

        if (uri != null) {
            val bitmap = createBitmap(c, uri)
            val file = FileClassUtils.getFile(c, bitmap)

            val requestFile =
                    RequestBody.create(
                            MediaType.parse("image/*"),
                            file
                    )
            val part = MultipartBody.Part.createFormData("imageFile", "photo",
                    requestFile
            )

            return netApi.editProfile(map)
                    .flatMap { netApi.uploadPhotoUser(part) }
                    .onErrorReturn { ResponceSuccess() }
                    .compose(applySchedulers<ResponceSuccess>())

        } else {
            //map.put("shouldEditImage", false)
            return netApi.editProfile(map)
                    .onErrorReturn { ResponceSuccess() }
                    .compose(applySchedulers<ResponceSuccess>())
        }

    }

    fun editProfilePhone(newPhone: String, picker: CountryCodePicker): Observable<ResponceSuccess> {
        val user = UserProvider.getMyProfile()!!
        val map = RegistrationBody()
        map.email = user.email
        map.username = user.userName
        map.password = null
        map.firstname = user.firstName
        map.lastname = user.lastName
        map.birthday = user.birthday
        map.description = user.description
        map.name = map.firstname + " " + map.lastname
        map.phone.code = picker.selectedCountryCodeWithPlus
        map.phone.countryCode = picker.selectedCountryNameCode
        map.phone.number = newPhone
        return netApi.editProfile(map)
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun editProfileEmail(newEmail: String): Observable<ResponceSuccess> {
        val user = UserProvider.getMyProfile()!!
        val map = RegistrationBody()
        map.email = newEmail
        map.username = user.userName
        map.password = null
        map.firstname = user.firstName
        map.lastname = user.lastName
        map.birthday = user.birthday
        map.description = user.description
        map.name = map.firstname + " " + map.lastname
        map.phone.code = user.phoneCode
        map.phone.countryCode = user.phoneCountryCode
        map.phone.number = user.phoneNumber

        return netApi.editProfile(map)
                .compose(applySchedulers<ResponceSuccess>())

    }

    fun login(map: HashMap<String, Any>, c: Activity): Observable<Boolean>? {
        map.put("devicePlatformID", "2")
        map.put("deviceLanguage", Locale.getDefault().language)
        return netApi.login(map)
                .compose(applySchedulers<ResponceSuccess>())
                .map {
                    if (it.response.isNotEmpty()) {
                        if (it.response == "wrong") {
                            c.showSnackBar(R.string.wrong_login)
                        } else if (it.response == "ban") {
                            c.showDialogBan(it.body?.daysLeft)
                        } else {
                            c.showSnackBar(c.getString(R.string.error_log).format(it.response))
                        }
                    }
                    SharedSetting.setUserToken(it.token)
                    SharedSetting.setUserId(it.userId)
                    it.token.isNotEmpty()
                }
                .filter { it == true }
                .flatMap { getMyUserInfo() }
    }

    fun resetPassword(email: String, birthday: String): Observable<ResponceSuccess> {
        return netApi.resetPassword(email, birthday)
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun getMyUserInfo(): Observable<Boolean>? {
        return netApi.getMyUserInfo()
                .doOnNext {
                    it.userId = SharedSetting.getUserId()
                    UserProvider.saveMyUser(it)
                }
                .compose(applySchedulers<UserRealm>())
                .map { it.fullName.isNotEmpty() }

    }


    fun getUserInfo(userId: Int): Observable<User>? {
        return netApi.getUserInfo(userId)
                .compose(applySchedulers<User>())
    }

    fun sendMyContacts(list: ArrayList<String>): Observable<ArrayList<User>> {
        return netApi.sendMyContacts(PhoneList(list))
                .compose(applySchedulers<ArrayList<User>>())

    }

    /*Подписаться */
    fun subscribeToPeople(list: ArrayList<Int>?): Observable<ResponceSuccess>? {
        return netApi.subscribeToPeople(list = list)
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun getMyPostHome(lastId: Int = 0): Observable<ArrayList<PostItem>> {

        val c = Calendar.getInstance()
        val today = Calendar.getInstance()
        return netApi.getPostHome(lastId = lastId)
                .doOnNext {
                    Log.d("LOAD_POST_HOME", Thread.currentThread().name)
                    it.forEach { post ->
                        post.timeLeft = post.date.formatTimeLeft(c, today) ?: ""
                    }
                }
                .compose(applySchedulers<ArrayList<PostItem>>())
    }

    fun getPostProfile(lastId: Int = 0, userId: Int): Observable<ArrayList<PostItem>> {

        val c = Calendar.getInstance()
        val today = Calendar.getInstance()
        return netApi.getPostProfile(lastId = lastId, selfId = userId, limit = 50)
                .doOnNext {
                    it.forEach { post ->
                        post.timeLeft = post.date.formatTimeLeft(c, today) ?: ""
                    }
                }
                .compose(applySchedulers<ArrayList<PostItem>>())
    }

    fun getMyPostHomeWithDistance(lastId: Int = 0, long: Double, lat: Double, range: Int): Observable<ArrayList<PostItem>> {
        return netApi.getPostHomeWithDistance(lastId = lastId, long = long, lat = lat, range = range)
                .compose(applySchedulers<ArrayList<PostItem>>())
    }

    fun getFollowersList(lastId: Int = 0, selfId: Int = SharedSetting.getUserId()): Observable<ArrayList<User>> {

        return netApi.getFollowerList(lastId = lastId, selfId = selfId, userId = selfId)
                .compose(applySchedulers<ArrayList<User>>())
    }

    fun getFollowingList(lastId: Int = 0, selfId: Int = SharedSetting.getUserId()): Observable<ArrayList<User>> {

        return netApi.getFollowingList(lastId = lastId, selfId = selfId, userId = selfId)
                .compose(applySchedulers<ArrayList<User>>())
    }

    fun starPost(postId: String, isStar: Boolean, senderId: Int): Observable<ResponseBody> {
        if (isStar) {
            return netApi.starPost(postId, senderId)
                    .compose(applySchedulers<ResponseBody>())
        }

        return netApi.unStarPost(postId)
                .compose(applySchedulers<ResponseBody>())
    }

    fun repostPost(postId: String): Observable<ResponceSuccess> {
        return netApi.repostPost(postId)
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun followUser(userId: String, follow: Boolean = true): Observable<ResponceSuccess> {
        if (follow) {
            return netApi.followUser(userId)
                    .compose(applySchedulers<ResponceSuccess>())
        }
        return netApi.unFollowUser(userId)
                .compose(applySchedulers<ResponceSuccess>())

    }

    fun search(content: String = "ed", lastId: Int = 0): Observable<ArrayList<User>> {
        return netApi.search(content, lastId)
                .compose(applySchedulers<ArrayList<User>>())
    }

    fun reportPost(postId: String?, reason: String, message: String): Observable<ResponceSuccess> {
        return netApi.reportPost(postId, reason, message)
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun getInBox(): Observable<ArrayList<ChatPojo>> {
        return netApi.getInboxList()
                .doOnNext {
                    Log.d("LOAD_IN_BOX_", Thread.currentThread().name)
                    it.forEach {
                        it.time = it.date.toHour()
                        it.headerDate = it.date.toHeaderDate()
                        it.timeStamp = formatFullDate.parse(it.date).time
                        it.date = it.date.toFeeBackDate()
                    }
                    ChatProvider.saveNewMessage(it)
                }
                .compose(applySchedulers<ArrayList<ChatPojo>>())
    }

    fun getMessageFromUser(id: String?): Observable<ArrayList<ChatPojo>> {
        return netApi.getMessageFromUser(id)
                .doOnNext {
                    it.forEach {
                        it.time = it.date.toHour()
                        it.headerDate = it.date.toHeaderDate()
                        it.timeStamp = formatFullDate.parse(it.date).time
                        it.date = it.date.toFeeBackDate()
                    }
                    ChatProvider.saveNewMessage(it, true)
                }
                .compose(applySchedulers<ArrayList<ChatPojo>>())
    }

    fun sendMessage(idUser: String?, mes: String, user: User?, s: Boolean): Observable<ResponceSuccess> {
        val size = if (s) 1 else 0
        return netApi.sendMessageToUser(idUser, mes, isStart = size)
                .doOnNext {
                    if (it.response != "user-not-followed") {
                        val message = ChatMessage().apply {
                            id = it.messageID.toString()
                            chatId = idUser ?: ""
                            text = mes
                            time = it.timestamp.toHour()
                            seen = true
                            dateHeader = it.timestamp.toHeaderDate()
                            isMy = true
                            isSend = true
                            timeStamp = formatFullDate.parse(it.timestamp).time
                        }
                        ChatProvider.saveNewMessage(message, user)
                    }

                }
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun getSettings(): Observable<SettingPojo> {
        return netApi.getSettings()
                .compose(applySchedulers<SettingPojo>())
    }

    fun getSettingsPrivacy(): Observable<SettingPojo> {
        return netApi.getSettingsPrivacy()
                .compose(applySchedulers<SettingPojo>())
    }

    fun setSettingsPrivacy(first: Int, two: Int): Observable<SettingPojo> {
        val s = SendSettingPojo.SendPrivatePojo()
        s.class1 = first
        s.class2 = two
        return netApi.setSettingPrivacy(s)
                .compose(applySchedulers<SettingPojo>())
    }

    fun setNewSettings(send: SendSettingPojo): Observable<ResponseBody> {
        return netApi.setNewSettings(send)
                .compose(applySchedulers<ResponseBody>())
    }

    fun getNotification(lastId: Int? = 0): Observable<ArrayList<NotificationsItem>> {
        return netApi.getNotification(id = lastId)
                .doOnNext {
                    it.forEach {
                        try {
                            it.date = it.date.toFeeBackDate()
                            it.content = it.content.replace("%@ ", "", true)
                            it.content = it.content[0].toUpperCase() + it.content.subSequence(1, it.content.length).toString()
                        } catch(e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                .compose(applySchedulers<ArrayList<NotificationsItem>>())
    }

    fun changePassword(oldPass: String, newPass: String): Observable<ResponceSuccess> {
        val jo = ChangePasswordPojoMain()
        jo.password.current = Sha512.getHashCodeFromString(oldPass)
        jo.password.new = Sha512.getHashCodeFromString(newPass)
        return netApi.changePassword(jo)
                .compose(applySchedulers<ResponceSuccess>())
    }


    fun changeEmail(oldEmail: String, newEmail: String): Observable<ResponceSuccess> {
        val jo = ChangePasswordPojoMain()
        jo.password.current = oldEmail
        jo.password.new = newEmail
        return netApi.changeEmail(jo)
                .compose(applySchedulers<ResponceSuccess>())
    }


    fun changePhone(oldPass: String, newPass: String): Observable<ResponceSuccess> {
        val jo = ChangePasswordPojoMain()
        jo.password.current = Sha512.getHashCodeFromString(oldPass)
        jo.password.new = Sha512.getHashCodeFromString(newPass)
        return netApi.changePhone(jo)
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun terminateAccount(pass: String): Observable<ResponceSuccess> {
        return netApi.terminateAccount(pass)
                .compose(applySchedulers<ResponceSuccess>())
    }

    fun createPost(content: String, video: Boolean?, uri: Uri?, c: Context, bitmap: Bitmap?,
                   listId: List<UserPost>): Observable<ResponceSuccess> {
        val map = HashMap<String, RequestBody>()
        val latLng = RxLocation.subLoc.value
        map.put("content", createRequestBody(content))
        map.put("longitude", createRequestBody(latLng?.longitude.toString()))
        map.put("latitude", createRequestBody(latLng?.latitude.toString()))
        if (uri != null) {
            val filePhoto: File
            val requestFilePhoto: RequestBody
            val partPhoto: MultipartBody.Part
            if (video ?: false) {
                if (listId.isEmpty()) {
                    map.put("attachment", createRequestBody("video,"))
                } else {
                    map.put("attachment", createRequestBody("tag,video,"))
                    map.put("tagList", createRequestBody(listId.toJson()))
                }
                filePhoto = FileClassUtils.getFile(c, bitmap)
                requestFilePhoto =
                        RequestBody.create(
                                MediaType.parse("image/*"),
                                filePhoto
                        )
                partPhoto = MultipartBody.Part.createFormData("imageFile", "photo",
                        requestFilePhoto
                )
                return VideoUtils.createFile(FileClassUtils.getRealPathFromURI(c, uri))
                        .map {
                            val o = RequestBody.create(
                                    MediaType.parse("video/*"),
                                    it
                            )
                            MultipartBody.Part.createFormData("videoFile", "video",
                                    o
                            )
                        }
                        .doOnNext { Log.d("CREATE_POST_VIDEO_"," ok ") }
                        .flatMap { netApi.createPost(map, partPhoto, it) }
                        .compose(applySchedulers<ResponceSuccess>())
            } else {
                if (listId.isEmpty()) {
                    map.put("attachment", createRequestBody("image,"))

                } else {
                    map.put("attachment", createRequestBody("tag,image,"))
                    map.put("tagList", createRequestBody(listId.toJson()))

                }
                val bitmap = createBitmap(c, uri)
                filePhoto = FileClassUtils.getFile(c, bitmap)
                requestFilePhoto =
                        RequestBody.create(
                                MediaType.parse("image/*"),
                                filePhoto
                        )
                partPhoto = MultipartBody.Part.createFormData("imageFile", "photo",
                        requestFilePhoto
                )
                return netApi.createPost(map, partPhoto)
                        .compose(applySchedulers<ResponceSuccess>())
            }


        } else {
            if (listId.isEmpty()) {
                map.put("attachment", createRequestBody(""))
            } else {
                map.put("tagList", createRequestBody(listId.toJson()))
                map.put("attachment", createRequestBody("tag,"))
            }
            return netApi.createPost(map)
                    .compose(applySchedulers<ResponceSuccess>())
        }

    }

    fun createRequestBody(s: String): RequestBody {
        return RequestBody.create(
                MediaType.parse("multipart/form-data"), s)
    }


}