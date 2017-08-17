package chapters.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.extension.ID
import chapters.extension.USER
import chapters.extension.init
import chapters.main.profile.mvp.FollowingPresenter
import chapters.main.profile.mvp.MvpFollowing
import chapters.network.pojo.User
import chapters.ui.adapter.UserAdapter
import com.tempry.R
import kotlinx.android.synthetic.main.activity_following.*
import kotlinx.android.synthetic.main.app_bar_main.view.*


class FollowersActivity : FollowingActivity(){

//    var user:User?=null
    companion object {
        fun launch(context: Context,user:User?) {
            val intent = Intent(context, FollowersActivity::class.java)
            intent.putExtra("isMy", false)
            intent.putExtra(USER,user)
            context.startActivity(intent)
        }

        fun launch(context: Context,userId:Int?) {
            val intent = Intent(context, FollowersActivity::class.java)
            intent.putExtra("isMy", true)
            intent.putExtra(ID,userId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        user= intent.getSerializableExtra(USER) as User?
    }

    override fun initView() {
        rvUser.init(this)
        adapter = UserAdapter(this,true)
        rvUser.adapter = adapter
    }

    override fun settingToolbar() {
        val isMy = intent.getBooleanExtra("isMy", false)
        initToolBar(viewToolbar.toolbar, arrow = true)
        if (isMy) {
            viewToolbar.setTitle("Your followers")
        } else {
            viewToolbar.setTitle(user?.name+" followers")

        }
    }

    override fun makeRequest() {
        Log.d("OOLL_F",user.toString())
        presenter.getUserFollowers(userId = user?.validId?:intent.getIntExtra(ID,0))
    }
}