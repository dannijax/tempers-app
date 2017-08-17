package chapters.main.main.fragments

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import chapters.base.BaseFragment
import chapters.base.NetRequest
import chapters.database.SharedSetting
import chapters.database.UserProvider
import chapters.database.UserRealm
import chapters.extension.addDividerDecoration
import chapters.extension.hide
import chapters.extension.init
import chapters.extension.show
import chapters.main.main.NewPostActivity
import chapters.main.main.fragments.mvp.PostPresenter
import chapters.main.main.fragments.mvp.MvpPost
import chapters.main.profile.EditProfileActivity
import chapters.main.profile.FollowersActivity
import chapters.main.profile.FollowingActivity
import chapters.main.profile.PhotoActivity
import chapters.network.NetApiClient
import chapters.network.pojo.PostItem
import chapters.ui.adapter.PostAdapter
import chapters.ui.utils.GlideLoadCircle
import com.tempry.R
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment(), NetRequest, MvpPost {

    private var presenter: PostPresenter? = null
    private var adapter: PostAdapter? = null

    override fun setLayoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun afterCreateView() {
        super.afterCreateView()
        setHasOptionsMenu(true)
        initView()
        presenter = PostPresenter(this)

        makeRequest()
    }

    private fun initUserInfo() {
        val user = UserProvider.getMyProfile()
        Log.d("PROFILE___"," "+user.toString())
        viewFollowers.setCount(user?.followers ?: 0)
        viewFollowing.setCount(user?.following ?: 123)
        viewStar.setCount(user?.stars ?: 5)
        viewPost.setCount(user?.post ?: 80)
        tvName.text = "${user?.firstName} ${user?.lastName}"
        GlideLoadCircle(ivProfile, user?.photo)
        if (user?.description?.isNotEmpty() ?: false) {
            tvBio.show()
            tvBio.text = user?.description
        } else {
            tvBio.hide()
        }
        initClick(user)
    }

    private fun initClick(user: UserRealm?) {
        ivProfile.setOnClickListener {
            PhotoActivity.launch(activity, user?.photo, null)
        }
        viewFollowers.setOnClickListener {
            FollowersActivity.launch(activity, SharedSetting.getUserId())
        }
        viewFollowing.setOnClickListener {
            FollowingActivity.launch(activity, SharedSetting.getUserId())
        }
        fabAddPost.setOnClickListener {
            NewPostActivity.launch(activity, presenter?.firstPost)
        }
    }

    private fun initView() {
        rvPost.init(activity)
        adapter = PostAdapter(activity, function = {video,playbackState->

        })
        rvPost.addDividerDecoration()
        rvPost.adapter = adapter
        rvPost.isNestedScrollingEnabled=false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_profile, menu)
        menu?.findItem(R.id.action_edit)?.setOnMenuItemClickListener {
            EditProfileActivity.launch(activity)
            false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        makeRequest()
    }

    override fun makeRequest() {
        adapter?.list?.clear()
        presenter?.loadPostProfile(SharedSetting.getUserId())
        NetApiClient.getMyUserInfo()
                ?.subscribe({
                    initUserInfo()
                }, { it.printStackTrace() })
    }

    override fun onError(it: Throwable?) {

    }

    override fun emptyList(empty: Boolean) {
        if (empty) {
            llEmpty.show()
        }  else  {
            rvPost.show()
            llEmpty.hide()
        }
    }

    override fun addNewPost(list: ArrayList<PostItem>) {
        if(adapter?.list?.isEmpty()?:false){
            adapter?.list= arrayListOf()
        }
        adapter?.list?.addAll(list)
        adapter?.notifyDataSetChanged()

    }

    override fun loadNewPostFromLoc(list: ArrayList<PostItem>) {
    }

}