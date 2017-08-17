package chapters.main.profile.mvp

import chapters.database.SharedSetting
import chapters.network.NetApiClient
import rx.Subscription


class FollowingPresenter(val mvp: MvpFollowing) {

    private var subs: Subscription? = null
    fun getUserFollowers(lastId: Int = 0, userId: Int = SharedSetting.getUserId()) {
      subs=  NetApiClient.getFollowersList(lastId, selfId = userId)
                .subscribe({
                    mvp.setAdapter(it)
                }, { mvp.onError(it) })
    }

    fun getUserFollowing(lastId: Int = 0, userId: Int = SharedSetting.getUserId()) {
        subs=  NetApiClient.getFollowingList(lastId, selfId = userId)
                .subscribe({
                    mvp.setAdapter(it)
                }, { mvp.onError(it) })
    }
}