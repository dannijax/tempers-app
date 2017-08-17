package chapters.main.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import chapters.base.BaseActivity
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.tempry.R

class VideoActivity : BaseActivity() {

    override fun setLayoutRes(): Int {
        return R.layout.activity_video
    }

    companion object {
        fun launch(context: Context, link: String?, time: Long = 0) {

            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("time", time)
            intent.putExtra("link", link)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        val time = intent.getLongExtra("time", 0)
        val path = intent.getStringExtra("link")



//        videoView.setOnPreparedListener {
//            videoView.start()
//            videoView.seekTo(time.toInt())
//        }
//        videoView.setOnCompletionListener {
//            //videoView.hide()
//        }
//        videoView.setVideoPath(path)

    }
}