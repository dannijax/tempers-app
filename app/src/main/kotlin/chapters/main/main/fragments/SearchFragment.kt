package chapters.main.main.fragments

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import chapters.base.BaseFragment
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.extension.binding.RxSearchView
import chapters.extension.expandView
import chapters.extension.init
import chapters.main.main.MainActivity
import chapters.network.NetApiClient
import chapters.network.pojo.User
import chapters.ui.adapter.UserAdapter
import com.tempry.R
import kotlinx.android.synthetic.main.activity_following.*
import kotlinx.android.synthetic.main.fragment_search.*
import rx.Subscription

class SearchFragment : BaseFragment(), NetRequest {

    private var adapter: UserAdapter? = null
    private var sub: Subscription? = null
    private var first = true
    private var main: MainActivity? = null
    private lateinit var sv: SearchView
    private var list: ArrayList<User>? = null
    private var subSearch: Subscription? = null

    override fun setLayoutRes(): Int {
        return R.layout.fragment_search
    }

    override fun afterCreateView() {
        super.afterCreateView()
        setHasOptionsMenu(true)
        initView()
        main = activity as MainActivity
        main?.viewToolbar?.setTitle("")
    }

    private fun initView() {
        adapter = UserAdapter(activity)
        rvSearch.init(activity)
        rvSearch.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_search, menu)
        val item = menu?.findItem(R.id.action_search)
        sv = MenuItemCompat.getActionView(item) as SearchView

        sv.onActionViewExpanded()
//        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                first = true
//                //makeRequest()
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterList(newText ?: "")
//                return false
//            }
//
//        })
        RxSearchView.textChange(sv)
                .doOnNext { first=true }
                .subscribe { makeRequest() }
        super.onCreateOptionsMenu(menu, inflater)

    }

    private fun setAdapterInfo(list: ArrayList<User>) {
        this.list = list
        adapter?.list = list
        adapter?.notifyDataSetChanged()
    }

    private fun filterList(text: String): ArrayList<User> {
        return ArrayList(list?.filter { it.name.contains(text, true) } ?: listOf())
    }

    override fun makeRequest() {
        if (first) {
            sub = NetApiClient.search(sv.query.toString())
                    .subscribe({
                        first = false
                        setAdapterInfo(it)
                    }, {
                        adapter?.list?.clear()
                        adapter?.notifyDataSetChanged()
                        NetworkUtils.netError(it, this, activity) })
        } else {
            sub = NetApiClient.search(sv.query.toString(), adapter?.list?.last()?.validId?:0)
                    .subscribe({
                        list?.addAll(it)
                        adapter?.list=list
                    }, { NetworkUtils.netError(it, this, activity) })
        }
    }

    override fun onDestroy() {
        sub?.unsubscribe()
        subSearch?.unsubscribe()
        super.onDestroy()
    }
}