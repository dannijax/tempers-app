package chapters.ui.view

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import chapters.extension.getString
import com.tempry.R
import kotlinx.android.synthetic.main.app_bar_main.view.*


class ViewToolbar(context: Context?, attrs: AttributeSet?) : AppBarLayout(context, attrs) {


    init {
        LayoutInflater.from(context).inflate(R.layout.app_bar_main,this)
        val typed=context!!.obtainStyledAttributes(attrs,R.styleable.ViewToolbar)
        val title:String?=typed.getString(R.styleable.ViewToolbar_titleToolbar)
        setTitle(title?:"")
    }

    fun setTitle(resId:Int){
        tvTitle.text=getString(resId)
    }

    fun setTitle(str:String){
        tvTitle.text=str
    }
}