package chapters.main.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.database.SharedSetting
import chapters.extension.ADD_TAG_POST
import chapters.extension.animHideFast
import chapters.extension.animShowFast
import chapters.extension.init
import chapters.main.profile.mvp.FollowingPresenter
import chapters.main.profile.mvp.MvpFollowing
import chapters.network.pojo.User
import chapters.ui.adapter.TagAdapter
import com.tempry.R
import kotlinx.android.synthetic.main.activity_find_user.*

class FindUserActivity : BaseActivity(), NetRequest, MvpFollowing {

    private val presenter = FollowingPresenter(this)
    private lateinit var adapter: TagAdapter
    private lateinit var list: ArrayList<User>

    override fun setLayoutRes(): Int {
        return R.layout.activity_find_user
    }

    companion object {
        fun launch(context: Activity) {
            val intent = Intent(context, FindUserActivity::class.java)
            context.startActivityForResult(intent, ADD_TAG_POST)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnAddTags.animHideFast()
        initView()
        makeRequest()
    }

    private fun initView() {
        initToolBar(toolbar, arrow = true)
        sv.onActionViewExpanded()
        rvUser.init(this)
        adapter = TagAdapter(this, {
            if (it) btnAddTags.animShowFast() else btnAddTags.animHideFast()

        })
        rvUser.adapter = adapter

        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val newList = ArrayList(list.filter { it.name.contains(newText!!, true) })
                adapter.list = newList
                adapter.notifyDataSetChanged()
                return false
            }

        })

        btnAddTags.setOnClickListener {
            val s = ArrayList(adapter.map.filterValues { it }.keys)
            val intent = Intent()
            intent.putExtra("tagItem", s)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }
    }

    override fun onError(it: Throwable?) {

    }


    override fun setAdapter(list: ArrayList<User>) {
        this.list = list
        adapter.list = list
        adapter.notifyDataSetChanged()
    }

    override fun makeRequest() {
        presenter.getUserFollowing(userId = SharedSetting.getUserId())

    }
}