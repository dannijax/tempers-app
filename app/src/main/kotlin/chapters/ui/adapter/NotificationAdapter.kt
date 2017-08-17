package chapters.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chapters.main.profile.ProfileActivity
import chapters.network.pojo.NotificationsItem
import chapters.ui.view.ViewNotification
import com.tempry.R

class NotificationAdapter(val context: Context) : RecyclerView.Adapter<NotificationAdapter.ViewHolderNotif>() {

    var list: ArrayList<NotificationsItem> = arrayListOf()
    private val inflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolderNotif?, position: Int) {
        holder?.bind(list?.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderNotif {
        return ViewHolderNotif(inflater.inflate(R.layout.item_notif, parent, false))
    }

    inner class ViewHolderNotif(view: View) : RecyclerView.ViewHolder(view) {

        private val viewNotif = view.findViewById(R.id.viewNotif) as ViewNotification
        private var item: NotificationsItem? = null

        init {
            viewNotif.setOnClickListener {
                ProfileActivity.launch(context, item?.userId?.toInt())
            }
        }

        fun bind(item: NotificationsItem?) {
            this.item = item
            viewNotif.setInfo(item)
        }
    }
}