package chapters.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chapters.network.pojo.User
import chapters.ui.view.ViewUser2
import com.tempry.R

class TagAdapter(val context: Context, val function: (Boolean) -> Unit) : RecyclerView.Adapter<TagAdapter.ViewHolderTag>() {

    var list: ArrayList<User>? = null
    var map = HashMap<User, Boolean>()
    private val inflater = LayoutInflater.from(context)

    override fun onBindViewHolder(holder: ViewHolderTag?, position: Int) {
        holder?.bind(list?.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderTag {
        return ViewHolderTag(inflater.inflate(R.layout.item_tag, parent, false))

    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class ViewHolderTag(view: View) : RecyclerView.ViewHolder(view) {

        private val viewUser = view.findViewById(R.id.viewTag) as ViewUser2
        private var user: User? = null

        init {
            viewUser.checkListener { check, user ->
                map.put(user, check)
                function.invoke(map.filterValues { it }.isNotEmpty())

            }
        }

        fun bind(user: User?) {
            this.user = user
            viewUser.bind(user, map.get(user) ?: false)
        }


    }
}