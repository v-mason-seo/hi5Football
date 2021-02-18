package com.ddastudio.hifivefootball_android.ui.base;

/**
 * Created by hongmac on 2017. 9. 4..
 */

public interface BaseContract {

    interface View {
    }

    interface Presenter {
        void attachView(View view);

        void detachView();
    }
}
