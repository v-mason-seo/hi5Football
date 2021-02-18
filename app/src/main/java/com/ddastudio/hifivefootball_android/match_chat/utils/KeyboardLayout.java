package com.ddastudio.hifivefootball_android.match_chat.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ddastudio.hifivefootball_android.R;

public class KeyboardLayout extends LinearLayout {

    private KeyboardHelper keyboardHelper;
    private View emojiKeyboard;
    private EditText input;
    private View emojiToggleView;
    private View sendButton;

    public KeyboardLayout(Context context) {
        super(context);
        this.init(context);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(Context context) {
        this.setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_keyboard_simple, this, true);
        emojiKeyboard = this.findViewById(R.id.emoji_keyboard);
        input = (EditText) this.findViewById(R.id.text_input);
        emojiToggleView = this.findViewById(R.id.emoji_btn);
        sendButton = this.findViewById(R.id.send_btn);
    }

    public void setup(Activity activity) {
        this.setup(activity, null);
    }

    public void setup(Activity activity, RecyclerView listView) {
        keyboardHelper = KeyboardHelper.setup(activity, input, emojiKeyboard, emojiToggleView, listView);
    }

    public void setup(Activity activity, RecyclerView listView, View bottomView) {
        keyboardHelper = KeyboardHelper.setup(activity, input, emojiKeyboard, emojiToggleView, listView, bottomView);
    }

    public EditText getEditText() {
        return input;
    }

    public View getSendButton() {
        return sendButton;
    }

    public boolean onBackPressed() {
        return keyboardHelper.onBackPressed();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (keyboardHelper == null) {
            throw new IllegalArgumentException("Please invoke setup method!");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
