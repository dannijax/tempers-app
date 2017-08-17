package chapters.main.main.fragments

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import chapters.base.BaseFragment
import chapters.base.NetRequest
import chapters.extension.addDividerDecoration
import chapters.extension.binding.scrollEnd
import chapters.extension.init
import chapters.network.NetApiClient
import chapters.network.pojo.NotificationsItem
import chapters.ui.adapter.NotificationAdapter
import chapters.utils.eventbus.RxBus
import com.tempry.R
import kotlinx.android.synthetic.main.fragment_notif.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class NotificationsFragment : BaseFragment(), NetRequest {

    private lateinit var adapter: NotificationAdapter
    private var first = true
    private var subs: Subscription? = null
    override fun setLayoutRes(): Int {
        return R.layout.fragment_notif
    }

    override fun afterCreateView() {
        super.afterCreateView()
        setHasOptionsMenu(true)
        initRv()
        makeRequest()
        subs = RxBus
                .observeEvent(NotificationsItem::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.list.add(0, it)
                    adapter.notifyItemInserted(0)
                }

    }

    private fun initRv() {
        rvNotif.init(activity)
        rvNotif.addDividerDecoration()
        adapter = NotificationAdapter(activity)
        rvNotif.adapter = adapter
        rvNotif.scrollEnd()
                .distinctUntilChanged()
                .subscribe { makeRequest() }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_empty, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun makeRequest() {
        if (first) {
            first = false
            NetApiClient.getNotification()
                    .subscribe({
                        adapter.list = it
                        adapter.notifyDataSetChanged()
                    }, {
                        it.printStackTrace()
                    })
        } else {
            val id = adapter.list.lastOrNull()?.id ?: "1"
            NetApiClient.getNotification(id.toInt())
                    .subscribe({
                        adapter.list.addAll(it)
                        adapter.notifyDataSetChanged()
                    }, {
                        it.printStackTrace()
                    })
        }

    }

    override fun onDestroy() {
        subs?.unsubscribe()
        super.onDestroy()
    }
}