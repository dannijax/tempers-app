package chapters.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import chapters.extension.hide
import chapters.extension.show
import chapters.network.pojo.Post.InteractionItem
import chapters.network.pojo.User
import chapters.ui.utils.GlideLoadCircle
import com.tempry.R
import kotlinx.android.synthetic.main.view_user_post.view.*


class ViewUserPost(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_post, this)
    }

    fun bindView(user: User?, date: String?, viewItem: InteractionItem?, user2: User? = null) {
        GlideLoadCircle(ivLogo, user?.smallPhoto)
        tvName.text = user?.name
        tvDate.text = date
        tvViews.text = "${viewItem?.value} views"
        if (user2 == null) {
            llRepost.hide()
        } else {
            GlideLoadCircle(ivRepost, user2.smallPhoto)
            tvNameRepost.text = user2.name
            llRepost.show()
        }
    }

    fun clickUser(function: OnClickListener) {
        rl.setOnClickListener {
            function.onClick(rl)
        }
        llRepost.setOnClickListener {
            function.onClick(llRepost)
        }
    }


}