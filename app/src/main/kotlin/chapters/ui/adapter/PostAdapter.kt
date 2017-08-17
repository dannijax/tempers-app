package chapters.ui.adapter

import android.app.Activity
import android.net.Uri
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chapters.database.SharedSetting
import chapters.extension.*
import chapters.main.profile.PhotoActivity
import chapters.main.profile.ProfileActivity
import chapters.network.NetApiClient
import chapters.network.pojo.PostItem
import chapters.ui.utils.GlideLoadImage
import chapters.ui.view.ViewBottomPost
import chapters.ui.view.ViewUserPost
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.tempry.R
import im.ene.toro.BaseAdapter
import im.ene.toro.PlaybackState
import im.ene.toro.ToroAdapter
import im.ene.toro.exoplayer2.ExoPlayerHelper
import im.ene.toro.exoplayer2.ExoPlayerView
import im.ene.toro.exoplayer2.ExoPlayerViewHolder

class PostAdapter(val context: Activity, val hideBottom: Boolean = false, val function: (String?, PlaybackState?) -> Unit) : BaseAdapter<ToroAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val TYPE_IMAGE = 1
    private val TYPE_VIDEO = 0
    private val TYPE_TEXT = 2
    var playPos = 0
    var userId = SharedSetting.getUserId()
    var list: ArrayList<PostItem>? = arrayListOf()

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun getItem(position: Int): Any? {
        return list?.get(position)
    }

    override fun onViewRecycled(holder: ToroAdapter.ViewHolder?) {
        super.onViewRecycled(holder)
    }

    fun deletePost(idUser: Int) {
        val list2 = list?.filter { it.userSender?.validId == idUser }
        list2?.forEachIndexed { index, postItem ->
            notifyItemRemoved(list?.indexOf(postItem) ?: 0)
            list?.remove(postItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ToroAdapter.ViewHolder {
        if (viewType == TYPE_IMAGE) {
            return ViewHolderPostImage(inflater.inflate(R.layout.item_post_image, parent, false))
        }
        if (viewType == TYPE_VIDEO) {
            return ViewHolderPostVideo(inflater.inflate(R.layout.item_post_video, parent, false))
        }
        return ViewHolderPostText(inflater.inflate(R.layout.item_post_text, parent, false))

    }

    override fun getItemViewType(position: Int): Int {
        if (list?.get(position)?.isImage ?: false) return TYPE_IMAGE
        if (list?.get(position)?.isVideo ?: false) return TYPE_VIDEO
        return TYPE_TEXT
    }

    inner class ViewHolderPostImage(val view: View) : BaseViewHolderPost(view) {
        private val ivPhoto = view.findViewById(R.id.ivPhoto) as AppCompatImageView

        override fun onBind(adapter: RecyclerView.Adapter<*>?, item: Any?) {
            super.onBind(adapter, item)
            Log.d("POST_ADAPTER_", "item " + videoItem?.id + " image " + videoItem?.image)
            GlideLoadImage(ivPhoto, videoItem?.image)
            ivPhoto.setOnClickListener {
                PhotoActivity.launch(context, videoItem?.image, videoItem)
            }
        }
    }

    inner class ViewHolderPostVideo(val view: View) : BaseViewHolderPost(view) {

        private val ivPlaceHolder = view.findViewById(R.id.ivPlaceHolder) as AppCompatImageView
        private val ivCam = view.findViewById(R.id.ivCam)
        private val pbLoad = view.findViewById(R.id.pbLoad)
        private val tvTime = view.findViewById(R.id.tvTime) as AppCompatTextView
        private val ivFull = view.findViewById(R.id.ivFullScreen)

        init {
            ivCam.setOnClickListener {
                ivCam.fadeIn()
                seekTo(0)
                preparePlayer(true)
            }
            ivFull.setOnClickListener {
                val playbackState = PlaybackState(mediaId, duration, playerView.currentPosition)
                function.invoke(videoItem?.video, playbackState)
            }
        }

        override fun onBind(adapter: RecyclerView.Adapter<*>?, item: Any?) {
            super.onBind(adapter, item)
            ivPlaceHolder.show()
            pbLoad.fadeIn()
            GlideLoadImage(ivPlaceHolder, videoItem?.imagePlaceHolder)
        }

        override fun onPlaybackStarted() {
            super.onPlaybackStarted()
            ivCam.fadeIn()
            ivPlaceHolder.invis()
            pbLoad.fadeIn()
        }

        override fun onVideoPrepared() {
            super.onVideoPrepared()
            pbLoad.fadeOut()
        }

        override fun onPlaybackCompleted() {
            super.onPlaybackCompleted()
            ivPlaceHolder.show()
            ivCam.fadeOut()
            tvTime.invis()
            pbLoad.fadeIn()
        }
    }

    inner class ViewHolderPostText(val view: View) : BaseViewHolderPost(view) {

        private val tvDescription = view.findViewById(R.id.tvDescription) as AppCompatTextView

        override fun onBind(adapter: RecyclerView.Adapter<*>?, item: Any?) {
            super.onBind(adapter, item)

        }

    }

    abstract inner class BaseViewHolderPost(view: View) : ExoPlayerViewHolder(view) {

        var videoItem: PostItem? = null
        private lateinit var media: MediaSource
        private val viewUser = view.findViewById(R.id.viewUserPost) as ViewUserPost
        private val viewBottomPost = view.findViewById(R.id.viewBottomPost) as ViewBottomPost
        private val tvDescription = view.findViewById(R.id.tvDescription) as AppCompatTextView
        private val rvTag = view.findViewById(R.id.rvTag) as RecyclerView
        protected var adapterTag: TagPostAdapter
        protected val videoView: ExoPlayerView = view.findViewById(R.id.videoView) as ExoPlayerView

        init {

            viewUser.clickUser(View.OnClickListener {
                if (it.id == R.id.rl) {
                    if (videoItem?.userSender?.validId != SharedSetting.getUserId()) {
                        ProfileActivity.launch(context,
                                videoItem?.userSender?.validId, videoItem?.userSender?.isFollowed ?: 0)
                    } else {

                    }
                } else {
//                    if (videoItem?.userSender?.validId != SharedSetting.getUserId())
//                        ProfileActivity.launch(context,
//                                videoItem?.userSender?.validId, videoItem?.userSender?.isFollowed ?: 0)
                }

            })
            viewBottomPost.unFollow {
                val id = list?.get(adapterPosition)?.userSender?.validId
                NetApiClient.followUser(id.toString(), false)
                        .subscribe({}, { it.printStackTrace() })
                deletePost(id ?: 0)
            }
            adapterTag = TagPostAdapter(context)
            rvTag.init(context, LinearLayoutManager.HORIZONTAL)
            rvTag.adapter = adapterTag
            videoView.setOnClickListener {
                //VideoActivity.launch(context, videoItem?.video, videoView.currentPosition)
            }

        }

        override fun onBind(adapter: RecyclerView.Adapter<*>?, item: Any?) {
            this.videoItem = item as PostItem
            viewBottomPost.bind(videoItem)
            viewUser.bindView(videoItem?.userSender, videoItem?.timeLeft
                    , videoItem?.interaction?.views, videoItem?.interaction?.retemp?.by)
            this.media = ExoPlayerHelper.buildMediaSource(itemView.context, //
                    Uri.parse(videoItem?.video), DefaultDataSourceFactory(itemView.context,
                    Util.getUserAgent(itemView.context, "Toro-Sample")), itemView.handler, null)
            if (videoItem?.text?.isNotEmpty() ?: false) {
                tvDescription.show()
                tvDescription.text = videoItem?.text
            } else {
                tvDescription.hide()
            }
            if (item.interaction?.tag?.isNotEmpty() ?: false) {
                adapterTag.list = item.interaction?.tag ?: arrayListOf()
                adapterTag.notifyDataSetChanged()
            } else {
                adapterTag.list.clear()
                adapterTag.notifyDataSetChanged()
            }

            if (hideBottom) viewBottomPost.hide() else viewBottomPost.show()

        }


        override fun getMediaId(): String? {
            return if (this.videoItem != null) this.videoItem?.video ?: "" + "@" + adapterPosition else null

        }

        override fun getMediaSource(): MediaSource {
            return media
        }

        override fun findVideoView(itemView: View?): ExoPlayerView {
            return itemView?.findViewById(R.id.videoView) as ExoPlayerView
        }
    }
}
