package chapters.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import chapters.network.pojo.User
import chapters.ui.utils.GlideLoadCircle
import com.tempry.R
import kotlinx.android.synthetic.main.view_user2.view.*


class ViewUser2(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var user: User? = null
    private var function: ((Boolean, User) -> Unit)? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.view_user2, this)
        cbFollow.setOnClickListener {
            function?.invoke(cbFollow.isChecked,user!! )
        }

    }

    fun bind(user: User?, isCheck: Boolean = false) {
        user?.let {
            this.user = user
            GlideLoadCircle(ivLogo, user.smallPhoto)
            tvName.text = user.name
            cbFollow.isChecked = isCheck
        }
    }


    fun checkListener(function: (Boolean, User) -> Unit) {
        this.function = function
    }

}