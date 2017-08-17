package chapters.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import chapters.base.NetRequest
import chapters.extension.hide
import chapters.extension.show
import chapters.network.NetApiClient
import chapters.network.pojo.User
import chapters.ui.utils.GlideLoadCircle
import com.tempry.R
import kotlinx.android.synthetic.main.view_user.view.*


class ViewUser(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), NetRequest {


    private var user: User? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user, this)
        cbFollow.setOnClickListener {
            makeRequest()
        }
    }

    fun bind(user: User?, isFollowers: Boolean = false) {
        user?.let {
            this.user = user
            GlideLoadCircle(ivLogo, user.smallPhoto)
            tvName.text = user.name
            cbFollow.isChecked = user.isFollowed == 1
            tvStar.text = user.stars.toString()
            tvPost.text = user.posts.toString()
        }
        if (isFollowers) cbFollow.hide() else cbFollow.show()
    }

    override fun makeRequest() {

        NetApiClient.followUser(user?.validId.toString(), cbFollow.isChecked)
                .subscribe({
                    Log.d("USER_VIEW_OK_SUBSU", " ok  ")
                    user?.isFollowed = if (cbFollow.isChecked) 1 else 0
                }, { it.printStackTrace() })
    }
}