package chapters.main.main.fragments

import android.view.Menu
import android.view.MenuInflater
import chapters.base.BaseFragment
import chapters.base.NetRequest
import chapters.database.ChatProvider
import chapters.extension.addDividerDecoration
import chapters.extension.init
import chapters.network.NetApiClient
import chapters.ui.adapter.ChatAdapter
import com.tempry.R
import kotlinx.android.synthetic.main.fragment_inbox.*
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit

class InboxFragment : BaseFragment(), NetRequest {

    private var subs: Subscription? = null

    override fun setLayoutRes(): Int {
        return R.layout.fragment_inbox
    }

    override fun afterCreateView() {
        super.afterCreateView()
        setHasOptionsMenu(true)
        initView()
    }

    private fun initView() {
        rvInbox.init(activity)
        rvInbox.addDividerDecoration()
        rvInbox.adapter = ChatAdapter(ChatProvider.getChatList(), activity)
    }
    override fun onResume() {
        super.onResume()
        makeRequest()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_empty, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun makeRequest() {
        subs = Observable.interval(0, 10000, TimeUnit.MILLISECONDS)
        .flatMap { NetApiClient.getInBox() }
                .subscribe({}, { it.printStackTrace() })
    }

    override fun onDestroy() {
        subs?.unsubscribe()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        subs?.unsubscribe()
    }
}