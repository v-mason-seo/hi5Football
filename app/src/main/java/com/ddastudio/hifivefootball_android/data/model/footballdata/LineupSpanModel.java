package com.ddastudio.hifivefootball_android.data.model.footballdata;

/**
 * Created by hongmac on 2018. 1. 17..
 */

public class LineupSpanModel {

    int startPosition;
    int endPosition;
    int length;

    public LineupSpanModel() {
        this.startPosition = 0;
        this.endPosition = 0;
        this.length = 0;
    }

    public LineupSpanModel(int startPosition, int endPosition, int length) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.length = length;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
