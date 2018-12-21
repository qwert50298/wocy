package com.unicom.autoship.ui.widget;

/**
 * ================================================
 * 作    者：zhoudy
 * 版    本：
 * 创建日期：2018/8/8
 * 描    述:
 * ================================================
 */
public interface OnRangeChangedListener {
    void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser);

    void onStartTrackingTouch(RangeSeekBar view, boolean isLeft);

    void onStopTrackingTouch(RangeSeekBar view, boolean isLeft);
}
