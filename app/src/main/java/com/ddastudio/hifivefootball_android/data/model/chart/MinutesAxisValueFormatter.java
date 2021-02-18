package com.ddastudio.hifivefootball_android.data.model.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by hongmac on 2017. 11. 15..
 */

public class MinutesAxisValueFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        int minute = (int)value;

        return String.valueOf(minute) + "'";
    }
}
