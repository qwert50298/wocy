package com.unicom.autoship;

public interface BasePresenter<View> {
    void attchView(View view);
    void detachView();
}
