package com.ddastudio.hifivefootball_android.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.utils.RxBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 9. 4..
 * Created      -       onAttatch()
 *              -       oncreate()
 *              -       onCreateView()
 *              -       onActivityCreated()
 * Started      -       onStart()
 * Resumed      -       onResume()
 * Paused       -       onPause()
 * Stopped      -       onStop()
 * Destroyed    -       onDestroyView()
 *              -       onDestroy()
 *              -       onDetach()
 */

public class BaseFragment extends Fragment implements BaseContract.View {

    protected RxBus _rxBus;
    protected Unbinder _unbinder;
    CompositeDisposable _disposables;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _rxBus = App.getInstance().bus();
        _disposables = new CompositeDisposable();
        _disposables.add(
                _rxBus.asFlowable()
                        .subscribe( event -> onEvent(event)));
    }

    @Override
    public void onDestroyView() {

        if ( _disposables != null ) {
            _disposables.clear();
        }

        if ( _unbinder != null ) {
            _unbinder.unbind();
        }

        super.onDestroyView();
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
