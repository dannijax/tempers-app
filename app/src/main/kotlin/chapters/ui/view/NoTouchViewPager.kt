package chapters.ui.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


open class NoTouchViewPager:ViewPager{

    constructor(c:Context) : super(c)

    constructor(c: Context,attributeSet: AttributeSet) : super(c,attributeSet)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

}