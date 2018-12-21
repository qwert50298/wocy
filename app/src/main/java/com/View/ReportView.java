package com.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.unicom.autoship.R;

public class ReportView extends RelativeLayout {

    private Bitmap firstBitmap;
    private Bitmap secondBitmap;
    private Bitmap thirdBitmap;
    private Bitmap fourthBitmap;
    private Bitmap fifthBitmap;
    private Bitmap defaultBitmap;

    private ImageView firstView;
    private ImageView secondView;
    private ImageView thirdView;
    private ImageView fourthView;
    private ImageView fifthView;
    private ImageView defaultView;

    private int width;
    private int height;

    private boolean isShowing = false;
    private boolean isAnimating = false;

    private OnTabClickListener onTabClickListener;

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public ReportView(Context context, AttributeSet attrs) {
        super(context, attrs);

        defaultBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_qq);

        width = defaultBitmap.getWidth();
        height = defaultBitmap.getHeight();

        LayoutParams params = new LayoutParams(width, height);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        firstBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_qq);
        firstView = new ImageView(context);
        firstView.setImageBitmap(firstBitmap);
        firstView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTabClickListener != null) {
                    onTabClickListener.onTabClick(1);
                    hideMenu();
                }
            }
        });
        addView(firstView, params);

        secondBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_qq);
        secondView = new ImageView(context);
        secondView.setImageBitmap(secondBitmap);
        secondView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTabClickListener != null) {
                    onTabClickListener.onTabClick(2);
                    hideMenu();
                }
            }
        });
        addView(secondView, params);

        thirdBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_qq);
        thirdView = new ImageView(context);
        thirdView.setImageBitmap(thirdBitmap);
        thirdView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTabClickListener != null) {
                    onTabClickListener.onTabClick(3);
                    hideMenu();
                }
            }
        });
        addView(thirdView, params);

        fourthBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_qq);
        fourthView = new ImageView(context);
        fourthView.setImageBitmap(fourthBitmap);
        fourthView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTabClickListener != null) {
                    onTabClickListener.onTabClick(4);
                    hideMenu();
                }
            }
        });
        addView(fourthView, params);

        fifthBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_qq);
        fifthView = new ImageView(context);
        fifthView.setImageBitmap(fifthBitmap);
        fifthView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTabClickListener != null) {
                    onTabClickListener.onTabClick(5);
                    hideMenu();
                }
            }
        });
        addView(fifthView, params);

        defaultView = new ImageView(context);
        defaultView.setImageBitmap(defaultBitmap);
        defaultView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnimating) {
                    return;
                }
                if (!isShowing) {
                    isShowing = true;
                    showMenu();

                } else {
                    isShowing = false;
                    hideMenu();
                }

            }
        });
        addView(defaultView, params);

    }

    protected void showMenu() {
        ObjectAnimator firstAnimator = ObjectAnimator.ofFloat(firstView,
                "translationX", 0, (height + 80) * 5);
        ObjectAnimator secondAnimator = ObjectAnimator.ofFloat(secondView,
                "translationX", 0, (height + 80) * 4);
        ObjectAnimator thirdAnimator = ObjectAnimator.ofFloat(thirdView,
                "translationX", 0, (height + 80) * 3);
        ObjectAnimator fourthAnimator = ObjectAnimator.ofFloat(fourthView,
                "translationX", 0, (height + 80) * 2);
        ObjectAnimator fifthAnimator = ObjectAnimator.ofFloat(fifthView,
                "translationX", 0, (height + 80) * 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.playTogether(firstAnimator, secondAnimator, thirdAnimator, fourthAnimator, fifthAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
            }
        });
        animatorSet.start();
    }

    protected void hideMenu() {
        ObjectAnimator firstAnimator = ObjectAnimator.ofFloat(firstView,
                "translationX", firstView.getTranslationX(), 0);
        ObjectAnimator secondAnimator = ObjectAnimator.ofFloat(secondView,
                "translationX", secondView.getTranslationX(), 0);
        ObjectAnimator thirdAnimator = ObjectAnimator.ofFloat(thirdView,
                "translationX", thirdView.getTranslationX(), 0);
        ObjectAnimator fourthAnimator = ObjectAnimator.ofFloat(fourthView,
                "translationX", fourthView.getTranslationX(), 0);
        ObjectAnimator fifthAnimator = ObjectAnimator.ofFloat(fifthView,
                "translationX", fifthView.getTranslationX(), 0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(firstAnimator, secondAnimator, thirdAnimator, fourthAnimator, fifthAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
            }
        });
        animatorSet.start();
    }

    public interface OnTabClickListener {
        void onTabClick(int currentTab);
    }
}
