package com.unicom.autoship.ui;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;


import com.unicom.autoship.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BottomBar extends LinearLayoutCompat implements OnClickListener {

    public static final int HOME_ITEM = 0X01;
    public static final int BOAT_ITEM = 0X02;
    public static final int CONTACT_ITEM = 0X03;
    public static final int MINE_ITEM = 0X04;
    //public static final int WHERE_ITEM = 0X05;
    public static int SELECTED_ITEM;

    @Override
    public void onClick(View view) {
        if (null == bottomBarClickListener
                || view.isSelected()) return;
        clearItemStatus();
        switch (view.getId()) {
            case R.id.tv_home:
                SELECTED_ITEM = HOME_ITEM;
                break;
            case R.id.tv_boat:
                SELECTED_ITEM = BOAT_ITEM;
                break;
            case R.id.tv_contact:
                SELECTED_ITEM = CONTACT_ITEM;
                break;
            //case R.id.tv_mall:
            //    SELECTED_ITEM = MALL_ITEM;
             //   break;
            case R.id.tv_mine:
                SELECTED_ITEM = MINE_ITEM;
                break;
            default:
                throw new UnsupportedOperationException("wrong item");
        }
        bottomBarClickListener.onBottomBarClicked(SELECTED_ITEM);
        view.setSelected(true);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HOME_ITEM, BOAT_ITEM, CONTACT_ITEM, MINE_ITEM})
    @interface checkItem { }

    BottomBarClickListener bottomBarClickListener;
    AppCompatTextView mTvHome;
    AppCompatTextView mTvSmart;
    AppCompatTextView mWhere;
    //AppCompatTextView mTvMall;
    AppCompatTextView mTvMine;

    public BottomBar(Context context) {
        this(context, null);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.bottombar, this);

        mTvHome = (AppCompatTextView) rootView.findViewById(R.id.tv_home);
        mTvSmart = (AppCompatTextView) rootView.findViewById(R.id.tv_boat);
        mWhere = (AppCompatTextView) rootView.findViewById(R.id.tv_contact);
        //mTvMall = (AppCompatTextView) rootView.findViewById(R.id.tv_mall);
        mTvMine = (AppCompatTextView) rootView.findViewById(R.id.tv_mine);

        mTvHome.setOnClickListener(this);
        mTvSmart.setOnClickListener(this);
        mWhere.setOnClickListener(this);
        //mTvMall.setOnClickListener(this);
        mTvMine.setOnClickListener(this);

        mTvHome.setTag(HOME_ITEM);
        mTvSmart.setTag(BOAT_ITEM);
        mWhere.setTag(CONTACT_ITEM);
        //mTvMall.setTag(MALL_ITEM);
        mTvMine.setTag(MINE_ITEM);
    }

    public interface BottomBarClickListener {
        void onBottomBarClicked(@checkItem int item);
    }

    public void setOnBottomBarClickListener(BottomBarClickListener listener) {
        this.bottomBarClickListener = listener;
    }

    public void clearItemStatus() {
        mTvHome.setSelected(false);
        mTvSmart.setSelected(false);
        mWhere.setSelected(false);
       // mTvMall.setSelected(false);
        mTvMine.setSelected(false);
    }

//    public void setActiveItem (@checkItem int item) {
//        switch (item) {
//            case HOME_ITEM:
//                mTvHome.setSelected(true);
//                SELECTED_ITEM = HOME_ITEM;
//                break;
//            case SMART_ITEM:
//                mTvSmart.setSelected(true);
//                break;
//            case WHERE_ITEM:
//                mWhere.setSelected(true);
//                break;
//            case MALL_ITEM:
//                mTvMall.setSelected(true);
//                break;
//            case MINE_ITEM:
//                mTvMine.setSelected(true);
//                break;
//        }
//    }
}
