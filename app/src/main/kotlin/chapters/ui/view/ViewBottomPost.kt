package chapters.ui.view

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
import chapters.base.NetRequest
import chapters.database.SharedSetting
import chapters.extension.*
import chapters.main.profile.ReportActivity
import chapters.network.NetApiClient
import chapters.network.pojo.Post.Interaction
import chapters.network.pojo.Post.InteractionItem
import chapters.network.pojo.PostItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.jakewharton.rxbinding.view.RxView
import com.tempry.R
import filehelpers.FileClassUtils
import kotlinx.android.synthetic.main.view_bottom_post.view.*
import rx.Subscription
import java.util.concurrent.TimeUnit


class ViewBottomPost @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), NetRequest {

    private var sub: Subscription? = null
    private var idPost: String = ""
    private var retemp: InteractionItem? = null
    private var intercation: Interaction? = null
    private var star = true
    private var func: (() -> Unit)? = null
    private var video: Boolean? = null
    private var image: Boolean? = null
    private var itemPost: PostItem? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_bottom_post, this)
        ivMore.setOnClickListener {

            if (video ?: false) {
                it.showPopMenu(context.resources.getStringArray(R.array.bottomVideo), {
                    if (it == 0) {
                        ReportActivity.launch(context, itemPost)
                    } else if (it == 1) {
                        func?.invoke()
                    } else {
                        FileClassUtils.saveVideo(itemPost?.video, context as Activity, "Video_" + itemPost?.id)
                    }
                })
            } else if (image ?: false) {
                it.showPopMenu(context.resources.getStringArray(R.array.bottomImage), {
                    if (it == 0) {
                        ReportActivity.launch(context, itemPost)
                    } else if (it == 1) {
                        func?.invoke()
                    } else {
                        Glide.with(context)
                                .load(itemPost?.image)
                                .asBitmap()
                                .into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                                        FileClassUtils.saveToGallery(context as Activity, resource)
                                    }
                                })

                        //FileClassUtils.saveToGallery(context)
                    }
                })
            } else {
                it.showPopMenu(context.resources.getStringArray(R.array.bottom), {
                    if (it == 0) {
                        ReportActivity.launch(context, itemPost)
                    } else {
                        func?.invoke()
                    }
                })
            }

        }
        llShare.setOnClickListener {
            star = false
            val anim = ScaleAnimation(1f, 1.1f, 1f, 1.1f)
            anim.interpolator = LinearInterpolator()
            anim.duration = 500
            llShare.startAnimation(anim)
            ivShare.changeColorDrawable(R.color.orange)
            makeRequest()
        }
    }

    fun bind(item: PostItem?) {
        this.itemPost = item
        this.idPost = item?.id?:""
        this.retemp = item?.interaction?.retemp
        this.intercation = item?.interaction
        this.image = item?.isImage
        this.video = item?.isVideo
        val isMy = item?.userSender?.validId == SharedSetting.getUserId()
        setUpShare(isMy || retemp?.self == "1")
        //setRetemp(interaction?.retemp?.self=="1")
        tvStar.text = intercation?.stars?.value
        btnStars.isChecked = intercation?.stars?.self == "1"

        RxView.clicks(btnStars)
                .doOnNext { star = true }
                .doOnNext {
                    var oldCount = intercation?.stars?.value?.toInt() ?: 0
                    if (btnStars.isChecked) oldCount++ else oldCount=intercation?.stars?.value?.toInt() ?: 0
                    tvStar.text = oldCount.toString()
                }
                .debounce(750, TimeUnit.MILLISECONDS)
                .switchMap { NetApiClient.starPost(idPost, btnStars.isChecked,
                        itemPost?.userSender?.validId?:0) }
                .subscribe({
                    var oldCount = intercation?.stars?.value?.toInt() ?: 0
                    if (btnStars.isChecked) oldCount++ else oldCount--
                    intercation?.stars?.value = oldCount.toString()
                    tvStar.text = intercation?.stars?.value
                },{it.printStackTrace()})
        if (isMy) ivMore.invis() else ivMore.show()
    }

    private fun setUpShare(hide: Boolean) {
        if (hide) {
            llShare.hide()
        } else {
            llShare.show()
        }
    }

    override fun makeRequest() {
        val id: String
            if (retemp?.of != "0") {
                id = retemp?.of ?: ""
            } else {
                id = idPost
            }
            sub = NetApiClient.repostPost(id)
                    .subscribe({}, { it.printStackTrace() })
        }

    fun unFollow(function: () -> Unit) {
        this.func = function
    }
}