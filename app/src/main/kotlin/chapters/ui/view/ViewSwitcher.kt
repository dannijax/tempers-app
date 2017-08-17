package chapters.ui.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SwitchCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import chapters.extension.binding.RxSwitchCompat
import chapters.extension.hide
import chapters.extension.show
import com.tempry.R
import kotlinx.android.synthetic.main.view_switcher.view.*
import rx.Observable


class ViewSwitcher(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_switcher, this)
        val atr = context?.obtainStyledAttributes(attrs, R.styleable.ViewSwitcher)
        tvTitle.text = atr?.getString(R.styleable.ViewSwitcher_titleSwitcher)
        val text = atr?.getString(R.styleable.ViewSwitcher_infoSwitcher)
        tvText.text = atr?.getString(R.styleable.ViewSwitcher_infoSwitcher)
        if (text?.isNotEmpty() ?: false) tvText.show() else tvText.hide()
    }


    fun obsSwitch(): Observable<Boolean> {
        return RxSwitchCompat.switchChange(sc)
                .doOnNext {
                    Log.d("SWITCH_D_D"," "+it.toString())
                    if (it) {
                        tvText.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    } else {
                        tvText.setTextColor(ContextCompat.getColor(context, R.color.textColorSmall))
                    }
                }

    }

    fun getSwitch(): SwitchCompat? {
        return sc
    }

    fun off(){
        sc.isChecked=false
        tvText.setTextColor(ContextCompat.getColor(context, R.color.textColorSmall))
    }
}