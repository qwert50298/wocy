package com.unicom.autoship.ui.widget;

/**
 * ================================================
 * 作    者：zhoudy
 * 版    本：
 * 创建日期：2018/8/8
 * 描    述: it works for draw indicator text
 * ================================================
 */
class SeekBarState {
    public String indicatorText;
    public float value; //now progress value
    public boolean isMin;
    public boolean isMax;

    @Override
    public String toString() {
        return "indicatorText: " + indicatorText + " ,isMin: " + isMin + " ,isMax: " + isMax;
    }
}
