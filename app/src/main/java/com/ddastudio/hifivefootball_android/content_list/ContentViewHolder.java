package com.ddastudio.hifivefootball_android.content_list;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.R;
import com.google.android.exoplayer2.ui.PlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.helper.ToroPlayerHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

public class ContentViewHolder extends BaseViewHolder implements ToroPlayer {

    @BindView(R.id.player) PlayerView playerView;
    @BindView(R.id.iv_image) ImageView ivMain;

    Uri mUri;
    ToroPlayerHelper helper;

    public ContentViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void setMp4Uri(String url) {
        mUri = Uri.parse(url);
    }

    /*-------------------------------------------------------------------*/

    private ToroPlayer.EventListener toroPlayerEventListener = new EventListener() {
        @Override
        public void onBuffering() {
            // do something
            //Log.i("hong", "[onBuffering] position : " + getAdapterPosition());
        }

        @Override
        public void onPlaying() {
            // do something
            //Log.i("hong", "[onPlaying] position : " + getAdapterPosition());
            //ivMain.setVisibility(View.GONE);
        }

        @Override
        public void onPaused() {
            // do something
            //Log.i("hong", "[onPaused] position : " + getAdapterPosition());
        }

        @Override
        public void onCompleted() {
            // do something
            //Log.i("hong", "[onCompleted] position : " + getAdapterPosition());
        }
    };

    @NonNull
    @Override
    public View getPlayerView() {
        return playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {

        if ( mUri != null ) {
            return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        return null;
    }

    @Override
    public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
        if ( mUri != null ) {
            if (helper == null) {
                helper = new ExoPlayerViewHelper(this, mUri);
                helper.addPlayerEventListener(toroPlayerEventListener);
            }
            helper.initialize(container, playbackInfo);
        }
    }

    @Override
    public void play() {
        if ( mUri != null ) {
            if (helper != null) {
                helper.play();
                //ivMain.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void pause() {
        if ( mUri != null ) {
            if (helper != null) helper.pause();
        }
    }

    @Override
    public boolean isPlaying() {
        return mUri != null && helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        if (helper != null) {
            helper.removePlayerEventListener(toroPlayerEventListener);
            helper.release();
            helper = null;
        }
    }

    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    @Override public String toString() {
        return "ExoPlayer{" + hashCode() + " " + getAdapterPosition() + "}";
    }

    /*-------------------------------------------------------------------*/


}
