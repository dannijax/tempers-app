package chapters.ui.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chapters.main.profile.ProfileActivity
import chapters.network.pojo.TagItem
import chapters.network.pojo.User
import chapters.ui.utils.GlideLoadCircle
import com.tempry.R


class TagPostAdapter(val context: Context) : RecyclerView.Adapter<TagPostAdapter.ViewHolderPostTag>() {

    var list: ArrayList<User> = arrayListOf()
    private val infalter=LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderPostTag {
        return ViewHolderPostTag(infalter.inflate(R.layout.item_tag_post,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolderPostTag?, position: Int) {

        holder?.bind(list[position])
    }


    inner class ViewHolderPostTag(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName=view.findViewById(R.id.tvName) as AppCompatTextView
        private val ivPhoto=view.findViewById(R.id.ivPhoto) as AppCompatImageView
        private var user: User? = null

        init {
            view.setOnClickListener {
                ProfileActivity.launch(context,user?.validId)
            }
        }


        fun bind(tagItem: User) {
            this.user=tagItem
            GlideLoadCircle(ivPhoto,tagItem.smallPhoto)
            tvName.text=tagItem.name
        }

    }
}