package com.ddastudio.hifivefootball_android.data.model.arena;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by hongmac on 2017. 11. 15..
 */

public class UpDownAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        int count = (int) value;

        return value == (float)count ? String.valueOf(count) : "";
    }
}
