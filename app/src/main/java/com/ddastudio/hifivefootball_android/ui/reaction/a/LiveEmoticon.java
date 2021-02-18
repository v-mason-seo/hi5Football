package com.ddastudio.hifivefootball_android.ui.reaction.a;

/**
 * Created by hongmac on 2018. 2. 8..
 */

public class LiveEmoticon {

    private Emoticons emoticons;
    private int xCoordinate;
    private int yCoordinate;
    boolean isDirectionLeftToRight;

        public LiveEmoticon(Emoticons emoticons, int xCoordinate, int yCoordinate, boolean isLeftToRight) {
            this.emoticons = emoticons;
            this.xCoordinate = xCoordinate;
            this.yCoordinate = yCoordinate;
            this.isDirectionLeftToRight = isLeftToRight;
    }

    public Emoticons getEmoticons() {
        return emoticons;
    }

    public void setEmoticons(Emoticons emoticons) {
        this.emoticons = emoticons;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public boolean isDirectionLeftToRight() {
        return isDirectionLeftToRight;
    }
}
