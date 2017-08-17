package chapters.network

import chapters.database.SharedSetting
import chapters.database.UserRealm
import chapters.database.pojo.address.AddressResponse
import chapters.network.pojo.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable


interface NetApi {


    @GET("maps/api/geocode/json")
    fun getAddressFromLatLng(@Query("latlng") latLng: String, @Query("sensor") sensor: Boolean = false, @Query("language") lang: String = "en"): Observable<AddressResponse>

    @GET("maps/api/geocode/json")
    fun getLatLngFromAddress(@Query("address") address: String?): Observable<AddressResponse>


    @GET("credential/token.php")
    fun checkToken(): Observable<ResponceSuccess>


    @POST("credential/sign-up-next.php")
    fun registration(@Body map: RegistrationBody): Observable<ResponceSuccess>

    @FormUrlEncoded
    @POST("credential/log-in.php")
    fun login(@FieldMap map: HashMap<String, Any>): Observable<ResponceSuccess>

    @FormUrlEncoded
    @POST("credential/register/one-signal.php")
    fun registerOneSignal(@Field("oneSignalUserID") id: String?): Observable<ResponceSuccess>

    @Multipart
    @POST("playground/profile/update-profile-image.php")
    fun uploadPhotoUser(@Part file: MultipartBody.Part): Observable<ResponceSuccess>


    @PUT("playground/profile/update-next.php")
    fun editProfile(@Body map: RegistrationBody): Observable<ResponceSuccess>


    @FormUrlEncoded
    @POST("credential/forgot-password/submit.php")
    fun resetPassword(@Field("email") email: String, @Field("birthday") birthday: String): Observable<ResponceSuccess>


    @GET("playground/profile/retrieve.php")
    fun getMyUserInfo(@Query("userID") id2: Int = SharedSetting.getUserId()): Observable<UserRealm>

    @GET("playground/profile/retrieve.php")
    fun getUserInfo(@Query("userID") id: Int = SharedSetting.getUserId()): Observable<User>


//    @FormUrlEncoded
//    @POST("credential/hello/submit-number-list.php")
//    fun sendMyContacts(@Field("phoneList") list: String, @Field("userID") id: Int = SharedSetting.getUserId()): Observable<ResponseBody>


    @POST("credential/hello/retrieve-welcome-list-next.php")
    fun sendMyContacts(@Body body:PhoneList): Observable<ArrayList<User>>


    @GET("credential/hello/submit-number-list.php")
    fun subscribeToPeople(@Query("peopleList") list: ArrayList<Int>?): Observable<ResponceSuccess>


    @GET("playground/home/retrieve-post-list.php")
    fun getPostHome(@Query("selfID") selfId: Int = SharedSetting.getUserId(), @Query("limit") limit: Int = 10,
                    @Query("lastPostID") lastId: Int = 0): Observable<ArrayList<PostItem>>


    @GET("playground/profile/retrieve-post-list.php")
    fun getPostProfile(@Query("userID") selfId: Int = SharedSetting.getUserId(), @Query("limit") limit: Int = 10,
                       @Query("INT_MAX") lastId: Int = 0): Observable<ArrayList<PostItem>>


    @GET("playground/home/retrieve-post-list.php")
    fun getPostHomeWithDistance(@Query("selfID") selfId: Int = SharedSetting.getUserId(), @Query("limit") limit: Int = 10,
                                @Query("lastPostID") lastId: Int = 0,
                                @Query("latitude") lat: Double,
                                @Query("longitude") long: Double,
                                @Query("range") range: Int = 0): Observable<ArrayList<PostItem>>

    @GET("playground/graph/follower/retrieve-list.php")
    fun getFollowerList(@Query("userID") userId: Int = SharedSetting.getUserId(),
                        @Query("selfID") selfId: Int = SharedSetting.getUserId(), @Query("limit") limit: Int = 1000,
                        @Query("lastEntryID") lastId: Int = 0): Observable<ArrayList<User>>


    @GET("playground/graph/following/retrieve-list.php")
    fun getFollowingList(@Query("userID") userId: Int = SharedSetting.getUserId(),
                         @Query("selfID") selfId: Int = SharedSetting.getUserId(), @Query("limit") limit: Int = 1000,
                         @Query("lastEntryID") lastId: Int = 0): Observable<ArrayList<User>>

    @FormUrlEncoded
    @POST("playground/graph/post/star.php")
    fun starPost(@Field("postID") postId: String,
                 @Field("senderID") senderId: Int = SharedSetting.getUserId()): Observable<ResponseBody>


    @DELETE("playground/graph/post/star.php")
    fun unStarPost(@Query("postID") postId: String
                   , @Query("senderID") senderId: Int = SharedSetting.getUserId()): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("playground/graph/post/retemp.php")
    fun repostPost(@Field("postID") postId: String,
                   @Field("userID") userId: Int = SharedSetting.getUserId()): Observable<ResponceSuccess>


    @FormUrlEncoded
    @POST("playground/graph/follow/alter.php")
    fun followUser(@Field("userID2") id: String,
                   @Field("userID1") id2: Int = SharedSetting.getUserId()): Observable<ResponceSuccess>

    @DELETE("playground/graph/follow/alter.php")
    fun unFollowUser(@Query("userID2") id: String,
                     @Query("userID1") id2: Int = SharedSetting.getUserId()): Observable<ResponceSuccess>


    @GET("playground/graph/search/retrieve-list.php")
    fun search(@Query("content") content: String = "",
               @Query("offset") lastId: Int = 0,
               @Query("limit") limit: Int = 100): Observable<ArrayList<User>>


    @FormUrlEncoded
    @POST("playground/graph/post/report.php")
    fun reportPost(@Field("postID") postId: String?, @Field("reason") reason: String,
                   @Field("message") m: String
                   , @Field("userID") id: Int = SharedSetting.getUserId()): Observable<ResponceSuccess>


    @GET("playground/message/retrieve-list.php")
    fun getInboxList(): Observable<ArrayList<ChatPojo>>

    @GET("playground/message/retrieve-list-conversationalist.php")
    fun getMessageFromUser(@Query("conversationalistID") userId: String?): Observable<ArrayList<ChatPojo>>

    @FormUrlEncoded
    @POST("playground/message/deliver.php")
    fun sendMessageToUser(@Field("conversationalistID") id: String?,
                          @Field("messageContent") text: String,
                          @Field("conversationStarted") isStart: Int = 0,
                          @Field("messageType") type: Int = 1): Observable<ResponceSuccess>


    @GET("playground/settings/retrieve-notification-list.php")
    fun getSettings(): Observable<SettingPojo>

    @GET("playground/settings/retrieve-privacy-list.php")
    fun getSettingsPrivacy(): Observable<SettingPojo>

    @PUT("playground/settings/update-privacy-list-next.php")
    fun setSettingPrivacy(@Body bod: SendSettingPojo.SendPrivatePojo): Observable<SettingPojo>

    @PUT("playground/settings/update-notification-list.php")
    fun setNewSettings(@Body send: SendSettingPojo): Observable<ResponseBody>

    @GET("playground/notification/retrieve-list.php")
    fun getNotification(@Query("deviceLanguage") lang: String = "en", @Query("limit") limit: Int = 50
                        , @Query("lastEntryID") id: Int? = 0): Observable<ArrayList<NotificationsItem>>

    @POST("credential/terminate-account.php")
    fun determinateAccount(@Field("password") pass: String): Observable<ResponseBody>


    @DELETE("credential/terminate-account.php")
    fun terminateAccount(@Query("PASSWORD") pass: String): Observable<ResponceSuccess>

    @PUT("playground/settings/change-password.php")
    fun changePassword(@Body jo: ChangePasswordPojoMain): Observable<ResponceSuccess>

    @PUT("playground/settings/change-password.php")
    fun changeEmail(@Body jo: ChangePasswordPojoMain): Observable<ResponceSuccess>

    @PUT("playground/settings/change-password.php")
    fun changePhone(@Body jo: ChangePasswordPojoMain): Observable<ResponceSuccess>

    @Multipart
    @POST("playground/graph/post/create.php")
    fun createPost(@PartMap map: HashMap<String, RequestBody>): Observable<ResponceSuccess>


    @Multipart
    @POST("playground/graph/post/create.php")
    fun createPost(@PartMap map: HashMap<String, RequestBody>, @Part photo: MultipartBody.Part): Observable<ResponceSuccess>

    @Multipart
    @POST("playground/graph/post/create.php")
    fun createPost(@PartMap map: HashMap<String, RequestBody>, @Part photo: MultipartBody.Part, @Part video: MultipartBody.Part): Observable<ResponceSuccess>
}