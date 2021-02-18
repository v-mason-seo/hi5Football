package com.ddastudio.hifivefootball_android.ui.widget;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingRecordModel;
import com.ddastudio.hifivefootball_android.data.model.arena.UpDownAxisValueFormatter;
import com.ddastudio.hifivefootball_android.data.model.chart.MinutesAxisValueFormatter;
import com.ddastudio.hifivefootball_android.data.model.footballdata.Forecast;
import com.ddastudio.hifivefootball_android.data.model.footballdata.Weather;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;

/**
 * Created by hongmac on 2017. 11. 10..
 */

public class ForecastView extends LinearLayout  {

    private Paint gradientPaint;
    private int[] currentGradient;

    private TextView playerRatingUpCount;
    private TextView playerRatingDownCount;
    private TextView playerRatingAverage;
    private ImageView localTeamEmblem;
    private ImageView visitorTeamEmblem;
    protected BarChart mChart;

    private ArgbEvaluator evaluator;

    public ForecastView(Context context) {
        super(context);
    }

    public ForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForecastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ForecastView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        evaluator = new ArgbEvaluator();

        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        //setGravity(Gravity.TOP);
        inflate(getContext(), R.layout.abc_view_forecast, this);

        playerRatingUpCount = (TextView) findViewById(R.id.player_rating_up_count);
        playerRatingDownCount = (TextView) findViewById(R.id.player_rating_down_count);
        playerRatingAverage = (TextView) findViewById(R.id.player_rating_average);
        localTeamEmblem = (ImageView) findViewById(R.id.local_team_emblem);
        visitorTeamEmblem = (ImageView) findViewById(R.id.visitor_team_emblem);
        mChart = (BarChart) findViewById(R.id.chart1);

        initChardView();

    }

    public void initChardView() {


        //mChart.setBackgroundColor(Color.WHITE);
        //mChart.setExtraTopOffset(-10f);
        //mChart.setExtraBottomOffset(10f);
        mChart.setExtraLeftOffset(30f);
        mChart.setExtraRightOffset(30f);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        Legend legend = mChart.getLegend();
        legend.setTextColor(CommonUtils.getColor(getContext(), R.color.md_grey_300));

        IAxisValueFormatter xAxisFormatter = new MinutesAxisValueFormatter();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(4);
        xAxis.setTextColor(CommonUtils.getColor(getContext(), R.color.md_grey_300));
        xAxis.setYOffset(10f);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new UpDownAxisValueFormatter();
//
        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(false);
        //left.setSpaceTop(25f);
        //left.setSpaceBottom(25f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);


//        YAxis leftAxis = mChart.getAxisLeft();
//        //leftAxis.setTypeface(mTfLight);
//        leftAxis.setValueFormatter(new LargeValueFormatter());
//        leftAxis.setDrawGridLines(false);
//        leftAxis.setSpaceTop(35f);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);

        mChart.animateY(1000);
//
//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setTypeface(mTfLight);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

//        setData(8, 10);
        //setRatingRecord(items);

//        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart



        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public void setRatingRecord(List<PlayerRatingRecordModel> items) {

        if ( items == null || items.size() == 0) {
            return;
        }

//        float start = 1f;
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();

        for ( int i = 0 ; i < items.size(); i++ ) {
            PlayerRatingRecordModel recordData = items.get(i);
            yVals1.add(new BarEntry(recordData.getTimeSeq(), recordData.getRatingUp()));
            yVals2.add(new BarEntry(recordData.getTimeSeq(), recordData.getRatingDown()*-1));
        }

        BarDataSet set1, set2;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) mChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Up");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS[0]);
            set1.setValueTextColor(ColorTemplate.MATERIAL_COLORS[0]);

            set2 = new BarDataSet(yVals2, "Down");
            set2.setDrawIcons(false);
            set2.setColors(ColorTemplate.MATERIAL_COLORS[2]);
            set2.setValueTextColor(ColorTemplate.MATERIAL_COLORS[2]);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            //BarData data = new BarData(dataSets);
            BarData data = new BarData(set1, set2);
            //data.setValueFormatter(new LargeValueFormatter());
            data.setValueTextSize(9f);
            data.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return value == 0.0f ? "" : String.valueOf((int)value);
                }
            });
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            mChart.setData(data);
            mChart.notifyDataSetChanged();
        }

        mChart.animateY(500);

        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.2f; // x4 DataSet0
        int groupCount = 2;

        //mChart.getBarData().getGroupWidth(groupSpace, barSpace);

        /*// specify the width each bar should have
        mChart.getBarData().setBarWidth(barWidth);


        // restrict the x-axis range
        mChart.getXAxis().setAxisMinimum(0);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        mChart.getXAxis().setAxisMaximum(0 + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        mChart.groupBars(0, groupSpace, barSpace);*/

        //mChart.getBarData().setBarWidth(barWidth);
        //mChart.groupBars(0, groupSpace, barSpace);
    }

    public void setData(int count, float range) {

        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);

            if (Math.random() * 100 < 25) {
                yVals1.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.ic_scrap_border)));
            } else {
                yVals1.add(new BarEntry(i, val));
            }
        }

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year 2017");

            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }

    /*------------------------------------------------------*/

    private void initGradient() {
        float centerX = getWidth() * 0.5f;
        Shader gradient = new LinearGradient(
                centerX, 0, centerX, getHeight(),
                currentGradient, null,
                Shader.TileMode.MIRROR);
        gradientPaint.setShader(gradient);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (currentGradient != null) {
            initGradient();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), gradientPaint);
        super.onDraw(canvas);
    }

//    public void setForecast(Forecast forecast) {
//        Weather weather = forecast.getWeather();
//        currentGradient = weatherToGradient(weather);
//        if (getWidth() != 0 && getHeight() != 0) {
//            initGradient();
//        }
//
//        playerRatingUpCount.setText(weather.getDisplayName());
//        playerRatingDownCount.setText(weather.getDisplayName());
//        playerRatingAverage.setText(forecast.getTemperature());
//
//        Glide.with(getContext()).load(weatherToIcon(weather)).into(localTeamEmblem);
//        Glide.with(getContext())
//                .load(weatherToIcon(weather))
//                .into(visitorTeamEmblem);
//
//        invalidate();
//
//        localTeamEmblem.animate()
//                .scaleX(1f).scaleY(1f)
//                .setInterpolator(new AccelerateDecelerateInterpolator())
//                .setDuration(300)
//                .start();
//
//        visitorTeamEmblem.animate()
//                .scaleX(1f).scaleY(1f)
//                .setInterpolator(new AccelerateDecelerateInterpolator())
//                .setDuration(300)
//                .start();
//    }

    public void setPlayer(PlayerRatingInfoModel player, String localTeamImageUrl, String visitorTeamImageUrl, int position) {

        currentGradient = PositionToGradient(position);
        if (getWidth() != 0 && getHeight() != 0) {
            initGradient();
        }

//        playerRatingUpCount.setText(player.getRatingUpString());
//        playerRatingDownCount.setText(player.getRatingDownString());
//        playerRatingAverage.setText(player.getAvgRatingString());


//        if ( player.getTeamType().equals("local") ) {
//
//            Glide.with(getContext())
//                    .load(localTeamImageUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(localTeamEmblem);
//
//            Glide.with(getContext())
//                    .load(visitorTeamImageUrl)
//                    .bitmapTransform(new GrayscaleTransformation(getContext()))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(visitorTeamEmblem);
//        } else {
//            Glide.with(getContext())
//                    .load(localTeamImageUrl)
//                    .bitmapTransform(new GrayscaleTransformation(getContext()))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(localTeamEmblem);
//
//            Glide.with(getContext())
//                    .load(visitorTeamImageUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(visitorTeamEmblem);
//        }
//
//
//        setRatingRecord(player.getPlayerRatingRecordList());
        mChart.invalidate();
        invalidate();

        playerRatingUpCount.animate()
                .scaleX(1f).scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300)
                .start();

        playerRatingDownCount.animate()
                .scaleX(1f).scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300)
                .start();

        playerRatingAverage.animate()
                .scaleX(1f).scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300)
                .start();
    }

//    public void onScroll(float fraction, Forecast oldF, Forecast newF) {
//        localTeamEmblem.setScaleX(fraction);
//        localTeamEmblem.setScaleY(fraction);
//        currentGradient = mix(fraction,
//                weatherToGradient(newF.getWeather()),
//                weatherToGradient(oldF.getWeather()));
//        initGradient();
//        invalidate();
//    }

    public void onScroll(float fraction, int oldF, int newF) {

        playerRatingUpCount.setScaleX(fraction);
        playerRatingUpCount.setScaleY(fraction);
        playerRatingDownCount.setScaleX(fraction);
        playerRatingDownCount.setScaleY(fraction);

        playerRatingAverage.setScaleX(fraction);
        playerRatingAverage.setScaleY(fraction);

        currentGradient = mix(fraction,
                PositionToGradient(newF),
                PositionToGradient(oldF));
        initGradient();
        invalidate();
    }

    private int[] mix(float fraction, int[] c1, int[] c2) {
        return new int[]{
                (Integer) evaluator.evaluate(fraction, c1[0], c2[0]),
                (Integer) evaluator.evaluate(fraction, c1[1], c2[1]),
                (Integer) evaluator.evaluate(fraction, c1[2], c2[2])
        };
    }

    private int[] PositionToGradient(int position) {

        switch (position % 8) {
            case 0:
                return colors(R.array.gradientPeriodicClouds);
            case 1:
                return colors(R.array.gradientCloudy);
            case 2:
                return colors(R.array.gradientMostlyCloudy);
            case 3:
                return colors(R.array.gradientPartlyCloudy);
            case 4:
                return colors(R.array.gradientClear);
            case 5:
                return colors(R.array.gradient1);
            case 6:
                return colors(R.array.gradient2);
            case 7:
                return colors(R.array.gradient3);
            default:
                //throw new IllegalArgumentException();
                return colors(R.array.gradientPeriodicClouds);
        }
    }

    private int[] weatherToGradient(Weather weather) {
        switch (weather) {
            case PERIODIC_CLOUDS:
                return colors(R.array.gradientPeriodicClouds);
            case CLOUDY:
                return colors(R.array.gradientCloudy);
            case MOSTLY_CLOUDY:
                return colors(R.array.gradientMostlyCloudy);
            case PARTLY_CLOUDY:
                return colors(R.array.gradientPartlyCloudy);
            case CLEAR:
                return colors(R.array.gradientClear);
            default:
                throw new IllegalArgumentException();
        }
    }

    private int weatherToIcon(Weather weather) {
        switch (weather) {
            case PERIODIC_CLOUDS:
                return R.drawable.periodic_clouds;
            case CLOUDY:
                return R.drawable.cloudy;
            case MOSTLY_CLOUDY:
                return R.drawable.mostly_cloudy;
            case PARTLY_CLOUDY:
                return R.drawable.partly_cloudy;
            case CLEAR:
                return R.drawable.clear;
            default:
                throw new IllegalArgumentException();
        }
    }

    private int[] colors(@ArrayRes int res) {
        return getContext().getResources().getIntArray(res);
    }
}
