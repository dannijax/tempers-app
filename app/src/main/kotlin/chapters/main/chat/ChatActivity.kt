package chapters.main.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.database.ChatProvider
import chapters.extension.*
import chapters.main.profile.ProfileActivity
import chapters.network.NetApiClient
import chapters.network.pojo.User
import chapters.ui.adapter.MessageAdapter
import com.jakewharton.rxbinding.widget.RxTextView
import com.tempry.R
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit

class ChatActivity : BaseActivity(), NetRequest {

    private var userId: String? = null
    private var subs: Subscription? = null
    private var messageAdapter: MessageAdapter? = null
    private var user: User? = null
    override fun setLayoutRes(): Int {
        return R.layout.activity_chat
    }

    companion object {
        fun launch(context: Context, userId: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(ID, userId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = intent.getSerializableExtra(ID) as String?
        initRx()
        initView()
        initRv()
    }

    private fun initView() {
        initToolBar(viewToolbar.toolbar, arrow = true)
    }

    private fun initRv() {
        rvChat.init(this, stackEnd = true)
        messageAdapter = MessageAdapter(ChatProvider.getUserMessage(userId), this)
        rvChat.adapter = messageAdapter
        val manager = rvChat.layoutManager as LinearLayoutManager
        messageAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val friendlyMessageCount = messageAdapter?.itemCount
                val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 || positionStart >= friendlyMessageCount!! - 1 && lastVisiblePosition == positionStart - 1) {
                    rvChat.scrollToPosition(positionStart)
                }
            }
        })

    }

    private fun initUser(it: User?) {
        this.user = it
        viewToolbar.setTitle(it?.name ?: "")
    }

    private fun initRx() {
        RxTextView.textChanges(etMessage)
                .map { it.isNotEmpty() }
                .subscribe {
                    btnSend.isEnabled = it
                    if (it) {
                        btnSend.changeColorDrawable(R.color.orange)
                    } else {
                        btnSend.changeColorDrawable(R.color.grey_icon)
                    }
                }

        btnSend.setOnClickListener {
            NetApiClient.sendMessage(userId, etMessage.text.toString(), user,messageAdapter?.list?.isNotEmpty()?:false)
                    .doOnSubscribe {
                        etMessage.text.clear()
                        pbLoad.show()
                    }
                    .doOnUnsubscribe { pbLoad.hide() }
                    .subscribe({
                        if (it.response == "user-not-followed") {
                            val dialog = AlertDialog.Builder(this)
                                    .setTitle("Whoops")
                                    .setMessage("Sorry, this person has to follow you in order for you to be able to send messages.")
                                    .setPositiveButton("Ok",{d1,d2->
                                        d1.dismiss()
                                    })
                                    .create()
                            dialog.setOnShowListener {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
                            }
                            dialog.show()
                        }
                        // rvChat.scrollToPosition(messageAdapter!!.itemCount+1)
                    }, { it.printStackTrace() })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        menu?.findItem(R.id.action_profile)?.setOnMenuItemClickListener {
            ProfileActivity.launch(this, userId?.toInt(), user?.isFollowed?:0)
            false
        }
        menu?.findItem(R.id.action_delete)?.setOnMenuItemClickListener {
            finish()
            ChatProvider.deleteChatWithUser(userId)
            false
        }
        return super.onCreateOptionsMenu(menu)

    }

    override fun makeRequest() {
        NetApiClient.getUserInfo(userId?.toInt() ?: 0)
                ?.subscribe({ initUser(it) }, { NetworkUtils.netError(it, this, this) })
        subs = Observable.interval(0, 2000, TimeUnit.MILLISECONDS)
                .flatMap { NetApiClient.getMessageFromUser(userId) }
                .subscribe({}, { it.printStackTrace() })
    }

    override fun onPause() {
        subs?.unsubscribe()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        makeRequest()

    }

    override fun finish() {
        subs?.unsubscribe()
        messageAdapter?.list?.removeAllChangeListeners()
        super.finish()
    }
}