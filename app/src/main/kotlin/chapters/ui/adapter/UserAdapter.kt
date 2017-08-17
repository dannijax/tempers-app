package chapters.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chapters.database.SharedSetting
import chapters.main.profile.ProfileActivity
import chapters.network.pojo.User
import chapters.ui.view.ViewUser
import com.tempry.R
import kotlinx.android.synthetic.main.view_user.view.*

class UserAdapter(val context: Context,val followers:Boolean=false) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var list: ArrayList<User>? = null
    private val inflater = LayoutInflater.from(context)

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(list?.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_user, parent, false))

    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val viewUser = view.findViewById(R.id.viewUser) as ViewUser
        private var user: User? = null

        init {
            viewUser.setOnClickListener {
                if(SharedSetting.getUserId()!=user?.validId)
                ProfileActivity.launch(context,user?.validId,user?.isFollowed?:0)
            }
        }

        fun bind(user: User?) {
            this.user=user
            viewUser.bind(user,followers)
        }


    }
}