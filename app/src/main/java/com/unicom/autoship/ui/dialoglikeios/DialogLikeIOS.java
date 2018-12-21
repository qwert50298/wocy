package com.unicom.autoship.ui.dialoglikeios;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.unicom.autoship.R;


public class DialogLikeIOS extends Dialog {

    public DialogLikeIOS(Context context,
                         String content,
                         String negativeText,
                         @NonNull String positiveText) {
        super(context);
        setCanceledOnTouchOutside(false);
        int resId;
        if (null == negativeText) {
            resId = R.layout.layout_for_ios_alert_dialog;
        } else {
            resId = R.layout.layout_for_ios_choice_dialog;
        }
        setContentView(resId);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((TextView)findViewById(R.id.tv_content)).setText(content);
        if (null != negativeText) {
            TextView tvNegativeButton = (TextView)findViewById(R.id.tv_negative_button);
            tvNegativeButton.setText(negativeText);
        }
        TextView tvPositive = (TextView)findViewById(R.id.tv_positive_button);

        tvPositive.setText(positiveText);
    }

    public void  setPositiveOnClickListener(DialogClickListener onClickListener){
        if (null != onClickListener) {
            onClickListener.setDialog(this);
            findViewById(R.id.tv_positive_button).setOnClickListener(onClickListener);
        } else {
            DialogClickListener dialogClickListener = new DialogClickListener();
            dialogClickListener.setDialog(this);
            findViewById(R.id.tv_positive_button).setOnClickListener(dialogClickListener);
        }
    }

    public void setNegativeOnClickListener(DialogClickListener onClickListener){
        if (null != onClickListener) {
            onClickListener.setDialog(this);
            findViewById(R.id.tv_negative_button).setOnClickListener(onClickListener);
        } else {
            DialogClickListener dialogClickListener = new DialogClickListener();
            dialogClickListener.setDialog(this);
            findViewById(R.id.tv_negative_button).setOnClickListener(dialogClickListener);
        }
    }
}
