package chapters.ui.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import chapters.extension.changeColorDrawable
import chapters.extension.getString
import chapters.extension.hide
import chapters.extension.show
import com.tempry.MultiSelectionSpinner
import com.tempry.R
import kotlinx.android.synthetic.main.view_single_select.view.*


class ViewSingleSelect(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private var list: List<String>? = null
    private var listener: MultiSelectionSpinner.OnSingleItemSelected? = null
    private var removeFirst = false
    var name = ""

    init {
        LayoutInflater.from(context).inflate(R.layout.view_single_select, this)
        val attrsArray = context?.obtainStyledAttributes(attrs, R.styleable.ViewSingleSelect)
        val title = attrsArray?.getString(R.styleable.ViewSingleSelect_titleSelectSingle)
        val drawable = attrsArray?.getResourceId(R.styleable.ViewSingleSelect_iconSelectSingle, 0)
        val hide = attrsArray?.getBoolean(R.styleable.ViewSingleSelect_iconHideSingle, false)
        if (drawable != 0)
            ivIcon.setImageDrawable(ContextCompat.getDrawable(context, drawable ?: R.drawable.ic_calendar))
        tvTitle.text = title
        spinner.setSingleChoice(true)
        initListener()
        if (hide!!) {
            ivIcon.hide()
            rl.hide()
        } else {
            ivIcon.show()
            rl.show()
        }

        spinner.setItems(listOf("Everyone", "Only people I follow"))
    }


    fun getBoolean(): Int {
        val name=spinner.selectedItemsAsString
        return if (name == "Everyone") return 0 else 1
    }

    private fun initListener() {
        spinner.setListener(object : MultiSelectionSpinner.OnSingleItemSelected {
            override fun selectedSingleIndex(index: Int) {
                //selectId = 0
                //listener?.selectedSingleIndex(getIdSelect())
                settingIcon()
            }

            override fun selectedSingleString(name: String?) {
                this@ViewSingleSelect.name = name ?: ""
                listener?.selectedSingleString(name)
            }
        })
    }


    fun setListener(listener: MultiSelectionSpinner.OnSingleItemSelected) {
        this.listener = listener
    }

    fun getSpinner(): MultiSelectionSpinner = spinner

    fun getIcon(): AppCompatImageView = ivIcon


    private fun settingIcon() {
        if (removeFirst) {
            ivIcon.changeColorDrawable(R.color.colorAccent)
        } else {
            if (name != getString(R.string.nothing)) {
                ivIcon.changeColorDrawable(R.color.colorAccent)
            } else {
                ivIcon.changeColorDrawable(R.color.grey_icon)
            }
        }
    }

    fun setPos(follower: Int) {
        val pos = follower
        spinner.setSelection(pos)
    }
}