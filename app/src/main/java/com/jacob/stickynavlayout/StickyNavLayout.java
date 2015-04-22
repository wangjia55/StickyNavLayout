package com.jacob.stickynavlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by jacob-wj on 2015/4/22.
 */
public class StickyNavLayout extends LinearLayout {

    public StickyNavLayout(Context context) {
        this(context,null);
    }

    public StickyNavLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StickyNavLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
