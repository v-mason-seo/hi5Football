package com.ddastudio.hifivefootball_android.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.data.event.SelectedItemEvent;
import com.ddastudio.hifivefootball_android.utils.RxBus;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 9. 4..
 */

public class BaseActivity extends AppCompatActivity
    implements BaseContract.View {

    protected RxBus _rxBus;
    protected CompositeDisposable _disposables;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _disposables = new CompositeDisposable();
        _rxBus = App.getInstance().bus();
        _disposables.add(_rxBus.asFlowable()
                //.compose(bindToLifecycle())
                .subscribe(event -> onEvent(event))
        );

    }

    @Override
    protected void onDestroy() {
        if ( _disposables != null ) {
            _disposables.clear();
        }
        super.onDestroy();
    }

    protected void onEvent(Object event) {

    }

    public void showLoading() {

    }

    public void hideLoading() {

    }

    public void showMessage(String message) {

    }

    public void showErrorMessage(String errMessage ) {

    }
}
