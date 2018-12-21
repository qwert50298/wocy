package com.unicom.autoship.ui.dialoglikeios;

import android.view.View;

public class DialogClickListener implements View.OnClickListener {

    DialogLikeIOS dialogLikeIOS;
    @Override
    public void onClick(View v) {
        if (null != dialogLikeIOS) {
            dialogLikeIOS.dismiss();
        }
    }

    public void setDialog (DialogLikeIOS dialog) {
        this.dialogLikeIOS = dialog;
    }
}
