package chapters.ui.view

import android.content.Context
import android.support.v7.widget.AppCompatDrawableManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.tempry.R
import kotlinx.android.synthetic.main.view_item_info.view.*


class ViewInfoItem(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_info, this)
        val att = context?.obtainStyledAttributes(attrs, R.styleable.ViewInfoItem)
        tvName.text = att?.getString(R.styleable.ViewInfoItem_nameInfo)
        val drawable = att?.getResourceId(R.styleable.ViewInfoItem_iconInfo, 0) ?: 0
        if (drawable != 0) {
            iv.setImageDrawable(AppCompatDrawableManager.get().getDrawable(context!!, drawable))
        } else {

        }

    }

    fun setCount(count: Int) {
        tvCount.text = count.toString()
    }
}