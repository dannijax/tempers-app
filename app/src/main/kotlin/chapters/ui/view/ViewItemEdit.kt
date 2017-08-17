package chapters.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import chapters.extension.*
import com.tempry.R
import kotlinx.android.synthetic.main.view_item_edittext.view.*

open class ViewItemEdit(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private var errorText: String = ""
    private var ignoreChange = false
    private var first = true

    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_edittext, this)
        val attrsArray = context?.obtainStyledAttributes(attrs, R.styleable.ViewItemEdit)
        val hint = attrsArray?.getString(R.styleable.ViewItemEdit_hintItem)
        val drawable = attrsArray?.getResourceId(R.styleable.ViewItemEdit_iconItem, 0)
        val password = attrsArray!!.getBoolean(R.styleable.ViewItemEdit_isPassword, false)
        val phone = attrsArray.getBoolean(R.styleable.ViewItemEdit_phone, false)
        val enabled = attrsArray.getBoolean(R.styleable.ViewItemEdit_enableEnter, true)
        val onlySee = attrsArray.getBoolean(R.styleable.ViewItemEdit_onlySee, false)
        ignoreChange = attrsArray.getBoolean(R.styleable.ViewItemEdit_ignoreChangeColor, false)
        errorText = attrsArray.getString(R.styleable.ViewItemEdit_errorText) ?: ""
        if (drawable != 0) {
            ivIcon.setImageDrawable(ContextCompat.getDrawable(context, drawable ?: 0))
        } else {
            ivIcon.hide()
        }
        //changeColorDrawable(et.text.isNotEmpty())

        il.hint = hint
        settingPassword(attrsArray, password)
        setPhone(phone)
        setEnable(enabled, onlySee)
        et.setBackgroundResource(R.color.transparent)
        et.maxLines=1
    }

    private fun setEnable(enable: Boolean, onlySee: Boolean) {
        et.isCursorVisible = enable
        et.isFocusable = enable
        view.isFocusable = !enable
        if (!enable) {
            view.show()
            et.isFocusable = enable
            //viewBottom.show()
        } else {
            view.hide()
            viewBottom.hide()
        }
        if (onlySee) {
            //et.background = null
        }
    }

    private fun settingPassword(attrsArray: TypedArray, password: Boolean) {
        if (!password) {
            et.inputType = InputType.TYPE_CLASS_TEXT
            il.isPasswordVisibilityToggleEnabled = password
        }

    }

    private fun setPhone(phone: Boolean) {
        if (phone) {
            et.setText("+380")
            et.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_PHONE
            //et.phoneEnter()
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(13)
            et.filters = fArray
        }
    }

    fun setHint(strId: Int) {
        il.hint = context.getString(strId)

    }

    fun view(): View {
        return view
    }

    fun getEditText(): TextView {
        return et
    }

    fun setBlackText(str: String?, update: Boolean = true) {
        et.setTextColor(ContextCompat.getColor(context, R.color.black))
        et.setText(str, update)
    }

    fun showIcon(show: Boolean = true) {
        if (show) ivIcon.invis() else ivIcon.hide()
    }

    fun setHint(hint: String?) {
        il.hint = hint
    }

    fun setBlackText(resId: Int?, update: Boolean = true) {
        et.setTextColor(ContextCompat.getColor(context, R.color.black))
        et.setText(context.getString(resId ?: 0), update)
    }

    fun changeColorDrawable(it: Boolean) {
        if (!ignoreChange && ivIcon.visibility == View.VISIBLE) {
            if (it) {
                ivIcon.changeColorDrawable(R.color.orange)
            } else {
                ivIcon.changeColorDrawable(R.color.grey_icon)
            }
        }
        if (errorText.isNotEmpty() && !first) {
            if (it) {
                il.isErrorEnabled = false
            } else {
                il.error = errorText
            }
        }
        first = false

    }

    fun selection() {
        val pos = if (et.text.isEmpty()) 0 else et.text.length - 1
        et.setSelection(pos)
    }


}