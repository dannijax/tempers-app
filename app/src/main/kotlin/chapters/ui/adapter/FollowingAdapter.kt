package chapters.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chapters.base.NetRequest
import chapters.database.UserRealm
import chapters.extension.showSnackBar
import chapters.network.NetApiClient
import chapters.network.pojo.User
import chapters.ui.view.ViewFollowing
import com.tempry.R


class FollowingAdapter(val context: Context, val function: (Boolean) -> Unit?) : RecyclerView.Adapter<FollowingAdapter.ViewHolderFollowing>() {

    var list: ArrayList<User>? = null
    private val inflater = LayoutInflater.from(context)

    override fun onBindViewHolder(holder: ViewHolderFollowing?, position: Int) {
        holder?.bind(list?.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderFollowing {
        return ViewHolderFollowing(inflater.inflate(R.layout.item_following, parent, false))
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }


    private fun delete(position: Int?){
        notifyItemRemoved(position ?: 0)
        list?.removeAt(position?:0)
        //notifyItemRangeChanged(position ?: 0, itemCount);
        function.invoke(list?.isEmpty() ?: false)
    }


    inner class ViewHolderFollowing(view: View) : RecyclerView.ViewHolder(view), NetRequest {


        private var user: User? = null
        private val viewFollowing = view.findViewById(R.id.viewFollowing) as ViewFollowing

        init {

            viewFollowing.setOnClickAddButton({
                makeRequest()
            })
        }


        fun bind(user: User?) {
            this.user = user
            viewFollowing.setFollowingInfo(user)
        }

        override fun makeRequest() {
//            NetApiClient.subscribeToPeople(arrayListOf(user?.validId ?: 0))
//                    ?.subscribe({
//                        delete(adapterPosition)
//
//                    }, {
//                        it.printStackTrace()
//                        context.showSnackBar("Error to subscribe")
//                    })
            NetApiClient.followUser(user?.validId.toString(), true)
                    .subscribe({
                        delete(adapterPosition)
                    }, { it.printStackTrace() })
        }
    }


}