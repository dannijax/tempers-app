package chapters.ui.adapter

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import chapters.database.ChatMessage
import chapters.extension.hide
import chapters.extension.show
import com.tempry.R
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class MessageAdapter(val list: RealmResults<ChatMessage>, context: Context)
    : RealmRecyclerViewAdapter<ChatMessage, MessageAdapter.ViewHolderMessage>(list, true) {

    private val inflater = LayoutInflater.from(context)
    private val map = HashMap<String, Int>()

    init {
        map.clear()
        list.forEachIndexed { index, chatMessage ->
            Log.d("MESSAGE_ADAPTER_", " " + chatMessage.dateHeader + " time stamp ")
            if (!map.contains(chatMessage.dateHeader)) {
                map.put(chatMessage.dateHeader, index)
            }
        }
        Realm.getDefaultInstance().addChangeListener {
            map.clear()
            list.forEachIndexed { index, chatMessage ->
                Log.d("MESSAGE_ADAPTER_", " " + chatMessage.dateHeader + " time stamp ")
                if (!map.contains(chatMessage.dateHeader)) {
                    map.put(chatMessage.dateHeader, index)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderMessage {

        return ViewHolderMessage(inflater.inflate(R.layout.item_message, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolderMessage?, position: Int) {
        holder?.bind(list[position])
    }

    inner class ViewHolderMessage(view: View) : RecyclerView.ViewHolder(view) {

        private val tvMessage = view.findViewById(R.id.tvMessage) as AppCompatTextView
        private val cvMy = view.findViewById(R.id.cvMy) as CardView
        private val tvMyTime = view.findViewById(R.id.tvMyTime) as AppCompatTextView
        private val llPlayer = view.findViewById(R.id.llPlayer) as RelativeLayout
        private val tvMessagePlayer = view.findViewById(R.id.tvMessagePlayer) as AppCompatTextView
        private val tvPlayerTime = view.findViewById(R.id.tvPlayerTime) as AppCompatTextView
        private val tvDate=view.findViewById(R.id.tvDate) as AppCompatTextView

        fun bind(mes: ChatMessage?) {
            if (mes?.isMy ?: false) {
                llPlayer.hide()
                cvMy.show()
                tvMessage.text = mes?.text
                tvMyTime.text = mes?.time
            } else {
                llPlayer.show()
                cvMy.hide()
                tvMessagePlayer.text = mes?.text
                tvPlayerTime.text = mes?.time
            }
            setData(mes?.dateHeader)
        }

        fun setData(dateHeader: String?) {
            if(map[dateHeader] ==adapterPosition){
                tvDate.text=dateHeader
                tvDate.show()
            }else{
                tvDate.hide()
            }
        }
    }
}