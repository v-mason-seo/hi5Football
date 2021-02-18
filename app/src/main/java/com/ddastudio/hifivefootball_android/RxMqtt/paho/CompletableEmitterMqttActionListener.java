package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import java.util.Objects;

import io.reactivex.CompletableEmitter;

public abstract class CompletableEmitterMqttActionListener extends BaseEmitterMqttActionListener {

    protected final CompletableEmitter emitter;

    public CompletableEmitterMqttActionListener(final CompletableEmitter emitter) {
        this.emitter = Objects.requireNonNull(emitter);
    }

    @Override
    public OnError getOnError() {
        return new OnError() {

            @Override
            public void onError(final Throwable t) {
                CompletableEmitterMqttActionListener.this.emitter.onError(t);
            }
        };
    }

}
