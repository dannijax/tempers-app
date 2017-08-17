package chapters.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.extension.ID
import chapters.extension.init
import chapters.network.NetApiClient
import chapters.network.pojo.PostItem
import chapters.ui.adapter.PostAdapter
import com.tempry.R
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_report.*


class ReportActivity : BaseActivity(), NetRequest {


    private var post: PostItem? = null
    private var adapter: PostAdapter? = null
    private var list = arrayOf("Spam", "Abuse", "Offensive", "Other")
    private var lastPos = 0


    override fun setLayoutRes(): Int {
        return R.layout.activity_report
    }

    companion object {
        fun launch(context: Context, idPost: PostItem?) {
            val intent = Intent(context, ReportActivity::class.java)
            intent.putExtra(ID, idPost)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
//        Toro.attach(this)
        initToolBar(viewToolbar.toolbar, arrow = true)
        btnReport.setOnClickListener {
            makeRequest()
        }

        //jcVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640")
    }


    private fun initView() {
        post = intent.getSerializableExtra(ID) as PostItem?
        rvPost.init(this)
        rvPost.isNestedScrollingEnabled = false
        etReason.setSelection(etReason.text.length)
        etMessage.requestFocus()
        adapter = PostAdapter(this, true) { video,playbackState->

        }
        adapter?.list = arrayListOf(post!!)
        rvPost.adapter = adapter

        view.setOnClickListener {
            showDialog()
        }
//        playerManager?.setVideoSizeChangedListener(object :MediaPlayer.OnVideoSizeChangedListener(){
//            override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
//
//            }
//
//        })

    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
                .setTitle("Choice reason")
                .setSingleChoiceItems(list, lastPos) { dialog, which ->
                    lastPos = which
                    etReason.setText(list.get(lastPos))
                    etReason.setSelection(etReason.text.length)
                    dialog.dismiss()

                }
                .setCancelable(false)
                .create().show()
    }


    override fun makeRequest() {
        NetApiClient.reportPost(intent.getStringExtra(ID),
                etReason.text.toString(), etMessage.text.toString())
                .doOnUnsubscribe { finish() }
                .subscribe({
                    Toast.makeText(this, "Report has been send", Toast.LENGTH_SHORT).show()
                }, { NetworkUtils.netError(it, this, this) })

    }

    override fun finish() {
        super.finish()
    }
}