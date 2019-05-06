package com.i3ni.geeknum1.view


import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by Mustang on 19/4/30.
 *
 */
class NoSrcollViewPage : ViewPager {

    private var isCanScroll = false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setScanScroll(isCanScroll: Boolean) {
        this.isCanScroll = isCanScroll
    }

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, y)
    }

    override fun onTouchEvent(arg0: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        return if (isCanScroll) {
            super.onTouchEvent(arg0)
        } else {
            false
        }

    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item)
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        return if (isCanScroll) {
            super.onInterceptTouchEvent(arg0)
        } else {
            false
        }
    }
}


