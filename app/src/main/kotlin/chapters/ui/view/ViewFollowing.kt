package chapters.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import chapters.extension.getString
import chapters.network.pojo.User
import chapters.ui.utils.GlideLoadCircle
import com.bumptech.glide.Glide
import com.tempry.R
import kotlinx.android.synthetic.main.view_following.view.*


class ViewFollowing(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var user: User? = null
    private var func: (() -> Unit?)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_following, this)
        btnAdd.setOnClickListener {
            func?.invoke()
        }
    }

    fun setFollowingInfo(user: User?) {
        this.user = user
        tvName.text = user?.name
        tvStars.text = getString(R.string.count_st).format(user?.stars)
        tvPost.text = getString(R.string.count_post).format(user?.posts)
        GlideLoadCircle(ivPhoto,user?.smallPhoto)
    }


    fun setOnClickAddButton(function: () -> Unit) {
        this.func = function
    }
}