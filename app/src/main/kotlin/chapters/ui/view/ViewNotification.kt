package chapters.ui.view

import android.content.Context
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import chapters.instruments.EnumClass
import chapters.network.pojo.NotificationsItem
import chapters.ui.utils.GlideLoadCircle
import com.tempry.R
import kotlinx.android.synthetic.main.view_notification.view.*

/**
 * Created by VoVa on 30.04.2017.
 */
class ViewNotification @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_notification,this)
    }


    fun setInfo(item:NotificationsItem?){
        item?.let {
            GlideLoadCircle(ivLogo,item.photo)
            tvInfo.text=item.content
            tvName.text=item.name
            tvDate.text=item.date
            val icon = AppCompatResources.getDrawable(context,EnumClass.fromId(item.classId).image)
            ivIconType.setImageDrawable(icon)
        }

    }
}