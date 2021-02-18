package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import java.util.Objects;

import io.reactivex.FlowableEmitter;

public abstract class FlowableEmitterMqttActionListener<T> extends BaseEmitterMqttActionListener {

    protected final FlowableEmitter<? super T> emitter;

    public FlowableEmitterMqttActionListener(final FlowableEmitter<? super T> emitter) {
        this.emitter = Objects.requireNonNull(emitter);
    }

    @Override
    public OnError getOnError() {
        return new OnError() {

            @Override
            public void onError(final Throwable t) {
                FlowableEmitterMqttActionListener.this.emitter.onError(t);
            }
        };
    }

}
