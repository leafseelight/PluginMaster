package com.leaf.plugin.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class AbsListView extends ListView {

    public AbsListView(Context context) {
        super(context);
    }

    public AbsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AbsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
