package chapters.main.main.fragments.mvp

import android.util.Log
import chapters.database.SharedSetting
import chapters.network.NetApiClient
import chapters.network.pojo.PostItem
import chapters.utils.location.RxLocation
import com.google.android.gms.maps.model.LatLng
import rx.Subscription


class PostPresenter(val mvpPost: MvpPost) {

    var subs: Subscription? = null
    var lastPosId = ""
    private var lat: LatLng? = null
    var firstPost: PostItem? = null

    init {
        lat=RxLocation.subLoc.value
    }

    fun loadFirstPost() {
        subs = NetApiClient.getMyPostHome()
                .subscribe({
                    firstPost = it.firstOrNull { it.userSender?.validId == SharedSetting.getUserId() }
                    mvpPost.addNewPost(it)
                    mvpPost.emptyList(it.isEmpty())
                }, {
                    mvpPost.onError(it)
                    it.printStackTrace() })
    }


    fun loadFirstPostWithLoc(range: Int = 0) {
        val lat = RxLocation.subLoc.value
        subs = NetApiClient.getMyPostHomeWithDistance(long = lat?.longitude ?: 0.0,
                lat = lat?.latitude ?: 0.0, range = range)
                .subscribe({
                    mvpPost.addNewPost(it)
                    mvpPost.emptyList(it.isEmpty())
                }, {
                    mvpPost.onError(it)
                    it.printStackTrace()
                })
    }

    fun loadPaginationPostWithDistance(id: String?, range: Int) {
        subs = NetApiClient.getMyPostHomeWithDistance(lastId = id?.toInt() ?: 0, long = lat?.longitude ?: 0.0,
                lat = lat?.latitude ?: 0.0, range = range)
                .subscribe({ mvpPost.addNewPost(it) }, { mvpPost.onError(it) })
    }

    fun loadPaginationPost(id: String?) {
        subs = NetApiClient.getMyPostHome(id?.toInt() ?: 0)
                .subscribe({ mvpPost.addNewPost(it) }, { mvpPost.onError(it) })
    }

    fun loadPostProfile(userId: Int, lastId: Int = 0) {
        Log.d("LOAD_POST_FF_", " 11")
        subs?.unsubscribe()
        subs = NetApiClient.getPostProfile(userId = userId)
                .subscribe({
                    Log.d("LOAD_POST_FF_", " 22")
                    firstPost = it.firstOrNull() { it.userSender?.validId == SharedSetting.getUserId() }
                    mvpPost.addNewPost(it)
                    mvpPost.emptyList(it.isEmpty())
                }, {
                    Log.d("LOAD_POST_FF_", " 33")
                    it.printStackTrace()
                    mvpPost.onError(it) })
    }

    fun loadPaginationPostProfile(userId: Int, lastId: Int = 0) {
        subs = NetApiClient.getPostProfile(userId = userId, lastId = lastId)
                .subscribe({
                    mvpPost.addNewPost(it)
                }, { mvpPost.onError(it) })
    }


    fun setLoc(latLng: LatLng?) {
        this.lat = latLng

    }
}