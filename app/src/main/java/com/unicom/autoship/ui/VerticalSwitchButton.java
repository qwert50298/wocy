package com.unicom.autoship.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VerticalSwitchButton extends View{

    public VerticalSwitchButton(Context context) {
        super(context);
    }

    public VerticalSwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {

        return true;
    }
}
