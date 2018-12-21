package com.unicom.autoship.ui;

import android.content.Context;
import android.util.AttributeSet;

/**
 * TODO: document your custom view class.
 */
public class RatioImageView extends android.support.v7.widget.AppCompatImageView {

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int)(width*(8.0/15)), MeasureSpec.EXACTLY));
    }
}
