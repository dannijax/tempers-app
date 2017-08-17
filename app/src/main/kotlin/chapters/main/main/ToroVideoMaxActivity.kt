package chapters.main.main

import android.app.Activity
import android.content.Intent
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import chapters.base.BaseActivity
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.tempry.R
import im.ene.toro.exoplayer2.ExoPlayerHelper
import kotlinx.android.synthetic.main.activity_toro_max.*

class ToroVideoMaxActivity : BaseActivity() {


    private lateinit var initState:im.ene.toro.PlaybackState
    private var url=""


    override fun setLayoutRes(): Int {
        return R.layout.activity_toro_max
    }

    companion object {

        const val EXTRA_VIDEO_URL = "EXTRA_VIDEO_URL"
        const val EXTRA_PLAYBACK_STATE = "EXTRA_PLAYBACK_STATE"
        const val REQUEST_VIDEO_MAXIMIZED=4


        fun launch(context: Activity, url:String?, playBack: im.ene.toro.PlaybackState?) {
            val intent = Intent(context, ToroVideoMaxActivity::class.java)
            intent.putExtra(EXTRA_VIDEO_URL,url)
            intent.putExtra(EXTRA_PLAYBACK_STATE,playBack)
            context.startActivityForResult(intent,REQUEST_VIDEO_MAXIMIZED)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initState=intent.getParcelableExtra(EXTRA_PLAYBACK_STATE)
        url=intent.getStringExtra(EXTRA_VIDEO_URL)

        val mediaSource = ExoPlayerHelper.buildMediaSource(
                this,
                Uri.parse(url),
                DefaultDataSourceFactory(
                        this,
                        Util.getUserAgent(this, "")
                ),
                exo.handler,
                null
        )

        exo.resumePosition = initState.position
        exo.setMediaSource(mediaSource, true)
    }

    private fun prepareFinishOk() {

        val resultIntent = Intent().apply {
            putExtra("playBack",
                    im.ene.toro.PlaybackState(initState.mediaId, exo.duration, exo.currentPosition)
            )
        }

        setResult(Activity.RESULT_OK, resultIntent)
    }

    override fun onBackPressed() {
        prepareFinishOk()
        super.onBackPressed()
    }
}