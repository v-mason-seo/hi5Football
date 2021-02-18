package com.ddastudio.hifivefootball_android.ui.listener;

import android.view.View;

/**
 * Created by hongmac on 2017. 9. 5..
 */

public interface OnContentsHeaderClickListener {

    //void onItemClick(View itemView, int position);

    void onAvatarClick(View itemView, int position);

    void onCommentClick(View itemView, int position);

    void onLikeClick(View itemView, int position);

    void onScrapClick(View itemView, int position);
}
