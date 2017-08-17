package chapters.ui.view

import android.content.Context
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import chapters.database.Chat
import chapters.database.ChatMessage
import chapters.extension.hide
import chapters.extension.show
import chapters.ui.utils.GlideLoadCircle
import com.tempry.R
import kotlinx.android.synthetic.main.view_chat.view.*

class ViewChat @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_chat, this)
    }

    fun setInfo(chat: Chat?) {
        chat?.let {
            GlideLoadCircle(ivLogo, chat.photo)
            val mes = chat.message.last()
            tvMessage.text = mes.text
            tvName.text = chat.name
            tvDate.text = chat.time
            setUnreadCount(chat)
            setIconSend(mes)
        }

    }

    private fun setUnreadCount(chat: Chat) {
        if (chat.countUnread > 0) {
            tvCountUnread.show()
            tvCountUnread.text = chat.countUnread.toString()
        } else {
            tvCountUnread.hide()
        }
    }

    private fun setIconSend(mes: ChatMessage) {
        if (mes.isMy) {
            ivCheck.show()
            if (mes.isSend) {
                ivCheck.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_check_all_orange))
            } else {
                ivCheck.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_check_orange))
            }
        } else {
            ivCheck.hide()
        }
    }
}