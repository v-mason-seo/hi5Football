package com.ddastudio.hifivefootball_android.data.event;

public class StringEvent {

    public static final int VERTICAL_TOP = 1;
    public static final int POSTSLIST_REFRESH = 2;
    public static final int SELETED_GROUP = 3;

    private int mEventType;
    private String mEventData;

    public StringEvent(int eventType, String eventData) {
        this.mEventType = eventType;
        this.mEventData = eventData;
    }

    public int getEventType() {
        return mEventType;
    }

    public String getEventData() {
        return mEventData;
    }
}
