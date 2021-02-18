package com.ddastudio.hifivefootball_android.utils;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by hongmac on 2017. 9. 8..
 */

public class RxBus {


    public RxBus() {
    }

    private final Relay<Object> _bus = PublishRelay.create().toSerialized();

    public void send(Object o) {
        _bus.accept(o);
    }

    public Flowable<Object> asFlowable() {
        return _bus.toFlowable(BackpressureStrategy.LATEST);
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }

    /*public RxBus() {
    }

    private PublishSubject<Object> bus = PublishSubject.create();

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }*/
}
