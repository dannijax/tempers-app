package chapters.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.database.SharedSetting
import chapters.extension.ID
import chapters.extension.USER
import chapters.extension.hide
import chapters.extension.init
import chapters.main.profile.mvp.FollowingPresenter
import chapters.main.profile.mvp.MvpFollowing
import chapters.network.pojo.User
import chapters.ui.adapter.UserAdapter
import com.tempry.R
import kotlinx.android.synthetic.main.activity_following.*
import kotlinx.android.synthetic.main.app_bar_main.view.*


open class FollowingActivity : BaseActivity(), NetRequest, MvpFollowing {

    protected val presenter = FollowingPresenter(this)
    var adapter: UserAdapter? = null
    var user: User? = null

    override fun setLayoutRes(): Int {
        return R.layout.activity_following

    }

    companion object {
        fun launch(context: Context, user: User?) {
            val intent = Intent(context, FollowingActivity::class.java)
            intent.putExtra("isMy", false)
            intent.putExtra(USER, user)
            context.startActivity(intent)
        }

        fun launch(context: Context, userId: Int?) {
            val intent = Intent(context, FollowingActivity::class.java)
            intent.putExtra("isMy", true)
            intent.putExtra(ID, userId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = intent.getSerializableExtra(USER) as User?
        settingToolbar()
        initView()
        //makeRequest()
    }

    open fun initView() {
        rvUser.init(this)
        adapter = UserAdapter(this,true)
        rvUser.adapter = adapter
    }

    open fun settingToolbar() {
        val isMy = intent.getBooleanExtra("isMy", false)
        initToolBar(viewToolbar.toolbar, arrow = true)
        if (isMy) {
            viewToolbar.setTitle("Your following")
        } else {
            viewToolbar.setTitle(user?.name + " following")

        }
    }

    override fun onResume() {
        super.onResume()
        makeRequest()
    }

    override fun makeRequest() {
        presenter.getUserFollowing(userId = user?.validId?:intent.getIntExtra(ID,0))
    }

    override fun onError(it: Throwable?) {
        it?.printStackTrace()
        pbLoad.hide()
    }

    override fun setAdapter(list: ArrayList<User>) {
        pbLoad.hide()
        adapter?.list = list
        adapter?.notifyDataSetChanged()
    }
}