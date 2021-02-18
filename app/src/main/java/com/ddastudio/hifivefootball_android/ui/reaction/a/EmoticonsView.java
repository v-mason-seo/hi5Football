package com.ddastudio.hifivefootball_android.ui.reaction.a;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.ddastudio.hifivefootball_android.R;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by hongmac on 2018. 2. 8..
 */

public class EmoticonsView extends View {

    private Paint mPaint;
    private Path mAnimPath;
    private Matrix mMatrix;
    //private Bitmap mLike48, mLove48, mHaha48, mWow48, mSad48, mAngry48;
    private Bitmap mS0, mS1, mS2, mS3, mS4, mS5, mS6, mS7, mS8, mS9, mS10, mS11, mS12, mS13, mS14, mS15;

    private ArrayList<LiveEmoticon> mLiveEmpticions = new ArrayList<>();
    private final int X_COORDINATE_STEP = 8;
    private final int Y_COORDINATE_OFFSET = 100;
    private final int Y_COORDINATE_RANGE = 200;
    private int mScreenWidth;

    public EmoticonsView(Context context) {
        super(context);
    }

    public EmoticonsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmoticonsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*-----------------------------------------------------*/

    public void initView(Activity activity) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;

        mPaint = new Paint();
        mAnimPath = new Path();
        mMatrix = new Matrix();
        Resources res = getResources();

        mS0 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_0);
        mS1 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_1);
        mS2 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_2);
        mS3 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_3);
        mS4 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_4);
        mS5 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_5);
        mS6 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_6);
        mS7 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_7);
        mS11 = BitmapFactory.decodeResource(res, R.drawable.ic_s_1_11);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mAnimPath, mPaint);
        drawAllLiveEmoticons(canvas);
    }

    private void drawAllLiveEmoticons(Canvas canvas) {
        ListIterator<LiveEmoticon> iterator = mLiveEmpticions.listIterator();
        while (iterator.hasNext()) {

            Object object = iterator.next();
            LiveEmoticon liveEmoticon = (LiveEmoticon)object;
            //Integer xCoordinate = liveEmoticon.getxCoordinate() - ( X_COORDINATE_STEP * (liveEmoticon.isDirectionLeftToRight() ? -1 : 1) );
            Integer xCoordinate = liveEmoticon.getxCoordinate() - X_COORDINATE_STEP;
            Integer yCoordinate = liveEmoticon.getyCoordinate();
            liveEmoticon.setxCoordinate(xCoordinate);

            if ( xCoordinate > 0 ) {
                mMatrix.reset();
                mMatrix.postTranslate(xCoordinate, yCoordinate);
                resizeImageSizeBasedOnXCoordinate(canvas, liveEmoticon);
                invalidate();
            } else {
                iterator.remove();
            }

//            if ( liveEmoticon.isDirectionLeftToRight()) {
//
//                if ( xCoordinate < mScreenWidth ) {
//                    mMatrix.reset();
//                    mMatrix.postTranslate(xCoordinate, yCoordinate);
//                    resizeImageSizeBasedOnXCoordinate(canvas, liveEmoticon);
//                    invalidate();
//                } else {
//                    iterator.remove();
//                }
//
//            } else {
//
//                if ( xCoordinate > 0 ) {
//                    mMatrix.reset();
//                    mMatrix.postTranslate(xCoordinate, yCoordinate);
//                    resizeImageSizeBasedOnXCoordinate(canvas, liveEmoticon);
//                    invalidate();
//                } else {
//                    iterator.remove();
//                }
//            }
        }
    }

    private void resizeImageSizeBasedOnXCoordinate(Canvas canvas, LiveEmoticon liveEmoticon) {

        if ( liveEmoticon == null ) {
            return;
        }

        int xCoodinate = liveEmoticon.getxCoordinate();
        Bitmap bitmap48 = null;
        Bitmap scaled = null;

        Emoticons emoticons = liveEmoticon.getEmoticons();
        if ( emoticons == null ) {
            return;
        }

        switch (emoticons) {
            case S0:
                bitmap48 = mS0;
                break;
            case S1:
                bitmap48 = mS1;
                break;
            case S2:
                bitmap48 = mS2;
                break;
            case S3:
                bitmap48 = mS3;
                break;
            case S4:
                bitmap48 = mS4;
                break;
            case S5:
                bitmap48 = mS5;
                break;
            case S6:
                bitmap48 = mS6;
                break;
            case S7:
                bitmap48 = mS7;
                break;
            case S8:
                bitmap48 = mS8;
                break;
            case S9:
                bitmap48 = mS9;
                break;
            case S10:
                bitmap48 = mS10;
                break;
            case S11:
                bitmap48 = mS11;
                break;
        }

        if ( xCoodinate > mScreenWidth / 2 ) {
            canvas.drawBitmap(bitmap48, mMatrix, null);
        } else if (xCoodinate > mScreenWidth / 4 ) {
            scaled = Bitmap.createScaledBitmap(bitmap48, 3 * bitmap48.getWidth() / 4, 3 * bitmap48.getHeight() / 4, false);
            canvas.drawBitmap(scaled, mMatrix, null);
        } else {
            scaled = Bitmap.createScaledBitmap(bitmap48, bitmap48.getWidth() / 2, bitmap48.getHeight() / 2, false);
            canvas.drawBitmap(scaled, mMatrix, null);
        }

        //canvas.drawBitmap(bitmap48, mMatrix, null);
    }

    public void addView(Emoticons emoticons, boolean leftToRight) {
        //int startXCoordinate = leftToRight ? 0 : mScreenWidth;
        int startXCoordinate = mScreenWidth;
        int startYCoordinate = new Random().nextInt(Y_COORDINATE_RANGE) + Y_COORDINATE_OFFSET;
        LiveEmoticon liveEmoticon = new LiveEmoticon(emoticons, startXCoordinate, startYCoordinate, leftToRight);
        mLiveEmpticions.add(liveEmoticon);
        invalidate();
    }
}
