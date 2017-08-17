package chapters.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chapters.database.Chat
import chapters.main.chat.ChatActivity
import chapters.ui.view.ViewChat
import com.tempry.R
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Created by VoVa on 30.04.2017.
 */
class ChatAdapter(val list: RealmResults<Chat>, val context: Context) : RealmRecyclerViewAdapter<Chat, ChatAdapter.ViewHolderChat>(list, true) {

    private val inflater = LayoutInflater.from(context)

    override fun onBindViewHolder(holder: ViewHolderChat?, position: Int) {
        holder?.bind(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderChat {

        return ViewHolderChat(inflater.inflate(R.layout.item_chat, parent, false))
    }

    inner class ViewHolderChat(view: View) : RecyclerView.ViewHolder(view) {

        private var chat: Chat? = null
        private val viewChat = view.findViewById(R.id.viewChat) as ViewChat

        init {
            viewChat.setOnClickListener {
                ChatActivity.launch(context, chat?.chatId ?: "1")
            }
        }

        fun bind(chat: Chat?) {
            this.chat = chat
            viewChat.setInfo(chat)
        }
    }
}