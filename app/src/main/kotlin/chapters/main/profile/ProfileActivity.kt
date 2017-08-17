package chapters.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.extension.*
import chapters.main.chat.ChatActivity
import chapters.main.main.fragments.mvp.MvpPost
import chapters.main.main.fragments.mvp.PostPresenter
import chapters.network.NetApiClient
import chapters.network.pojo.PostItem
import chapters.network.pojo.User
import chapters.ui.adapter.PostAdapter
import chapters.ui.utils.GlideLoadCircle
import com.tempry.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.app_bar_main.view.*

class ProfileActivity : BaseActivity(), NetRequest, MvpPost {

    private var user: User? = null
    private var presenter: PostPresenter? = null
    private var adapter: PostAdapter? = null
    private var isFollowed = 0

    override fun setLayoutRes(): Int {
        return R.layout.activity_profile
    }

    companion object {
        fun launch(context: Context, id: Int? = 0, isFollowed: Int = 0) {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(ID, id)
            intent.putExtra("follow", isFollowed)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = PostPresenter(this)
        isFollowed = intent.getIntExtra("follow", 0)
        initView()
        makeRequest()

    }

    private fun initView() {
        initToolBar(viewToolbar.toolbar, arrow = true)
        adapter = PostAdapter(this,false,{
            video,playbackState->

        })
        rvPost.init(this)
        rvPost.addDividerDecoration()
        rvPost.adapter = adapter
        rvPost.isNestedScrollingEnabled = false
    }

    private fun initClick() {
        viewFollowers.setOnClickListener {
            FollowersActivity.launch(this, user)
        }

        viewFollowing.setOnClickListener {
            FollowingActivity.launch(this, user)
        }

        btnMessage.setOnClickListener {
            ChatActivity.launch(this, user?.userId.toString())
        }

        btnSubsribe.setOnClickListener {
            NetApiClient.followUser(user?.validId.toString(), true)
                    .doOnUnsubscribe {
                        btnUnSubsribe.show()
                        btnSubsribe.hide()
                    }
                    .subscribe({
                        isFollowed = 1
                    }, { it.printStackTrace() })
        }

        btnUnSubsribe.setOnClickListener {
            NetApiClient.followUser(user?.validId.toString(), false)
                    .doOnUnsubscribe {
                        btnSubsribe.show()
                        btnUnSubsribe.hide()
                    }
                    .subscribe({
                        isFollowed = 0
                    }, { it.printStackTrace() })
        }
    }

    private fun initUser(it: User?) {
        it?.userId = intent.getIntExtra(ID, 0)
        this.user = it
        GlideLoadCircle(ivProfile, it?.smallPhoto)
        ivProfile.setOnClickListener { l ->
            PhotoActivity.launch(this, it?.largePhoto, null)
        }
        tvName.text = it?.name
        viewFollowers.setCount(it?.followers ?: 0)
        viewFollowing.setCount(it?.following ?: 0)
        viewStar.setCount(it?.stars ?: 0)
        viewPost.setCount(it?.posts ?: 0)
        if (it?.description?.isNotEmpty() ?: false) {
            tvBio.show()
            tvBio.text = it?.description
        } else {
            tvBio.hide()
        }
        if (isFollowed == 1 || it?.isFollowed == 1) {
            btnUnSubsribe.show()
            btnSubsribe.hide()
        } else {
            btnUnSubsribe.hide()
            btnSubsribe.show()
        }
        initClick()
    }

    override fun makeRequest() {

        NetApiClient.getUserInfo(intent.getIntExtra(ID, 0))
                ?.doOnUnsubscribe {
                    presenter?.loadPostProfile(intent.getIntExtra(ID, 0))
                }
                ?.subscribe({ initUser(it) }, { NetworkUtils.netError(it, this, this) })
    }

    override fun onError(it: Throwable?) {

    }

    override fun emptyList(empty: Boolean) {
        if (empty) {
            llEmpty.show()
        } else {
            rvPost.show()
            llEmpty.hide()
        }
    }

    override fun addNewPost(list: ArrayList<PostItem>) {
        if (adapter?.list?.isEmpty() ?: false) {
            adapter?.list = arrayListOf()
        }
        adapter?.list?.addAll(list)
        adapter?.notifyDataSetChanged()
    }

    override fun loadNewPostFromLoc(list: ArrayList<PostItem>) {
    }

}