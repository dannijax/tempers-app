package chapters.main.reg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import chapters.base.BaseActivity
import chapters.extension.init
import chapters.main.main.MainActivity
import chapters.network.pojo.User
import chapters.ui.adapter.FollowingAdapter
import com.tempry.R
import kotlinx.android.synthetic.main.activity_follow_people.*

class FollowPeopleActivity : BaseActivity() {

    private var adapter: FollowingAdapter? = null
    override fun setLayoutRes(): Int {
        return R.layout.activity_follow_people
    }

    companion object {
        fun launch(context: Context, listFollowing: ArrayList<User>) {
            val intent = Intent(context, FollowPeopleActivity::class.java)
            intent.putExtra("following", listFollowing)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    private fun initView() {
        tvSkip.setOnClickListener {
            MainActivity.launch(this)
        }
        initRv()
    }

    private fun initRv() {
        adapter = FollowingAdapter(this, {
            if (it) MainActivity.launch(this)
        })
        rvFollowing.init(this)
        rvFollowing.adapter = adapter
        val list=intent.getSerializableExtra("following") as ArrayList<User>
        adapter?.list= list
        //adapter?.list= intent.getSerializableExtra("following") as ArrayList<User>?
    }
}