package com.ddastudio.hifivefootball_android.ui.reaction.b;

import android.view.animation.Interpolator;

/**
 * Created by hongmac on 2018. 3. 22..
 */

public class JellyInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float t) {
        return (float)(Math.min(1.0, Math.sin(28 * t - 6.16) / (5 * t - 1.1)));
    }
}
