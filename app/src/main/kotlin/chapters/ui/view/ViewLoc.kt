package chapters.ui.view

import android.content.Context
import android.support.v7.widget.AppCompatDrawableManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import chapters.extension.showPopMenu
import com.tempry.R
import kotlinx.android.synthetic.main.view_loc.view.*


class ViewLoc @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loc, this)
        initButtonIcon()
    }

    fun initButtonIcon() {
        val distance = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_distance)
        val loc = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_loc_small)
        btnCity.setCompoundDrawablesRelativeWithIntrinsicBounds(loc, null, null, null)
        btnDistance.setCompoundDrawablesRelativeWithIntrinsicBounds(distance, null, null, null)
        btnDistance.setText("10 km")

    }

    fun initClick(finCity: () -> Unit, funDistance: (Int) -> Unit) {
        btnCity.setOnClickListener {
            finCity.invoke()
        }

        btnDistance.setOnClickListener {
            btnDistance.showPopMenu(resources.getStringArray(R.array.distance), {
                when (it){
                    0->{
                        btnDistance.setText("10 km")
                        funDistance.invoke(10)}
                    1->{
                        btnDistance.setText("25 km")
                        funDistance.invoke(25)}
                    2->{
                        btnDistance.setText("50 km")
                        funDistance.invoke(50)}
                    3->{
                        btnDistance.setText("100 km")
                        funDistance.invoke(100)}
                }
            })

        }
    }

    fun setCityName(name: CharSequence?) {
        btnCity.text = name
    }


}