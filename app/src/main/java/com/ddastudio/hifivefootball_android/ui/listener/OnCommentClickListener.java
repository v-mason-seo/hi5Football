package com.ddastudio.hifivefootball_android.ui.listener;

import android.view.View;

/**
 * Created by hongmac on 2017. 9. 12..
 */

public interface OnCommentClickListener {

    void onAvatarClick(View itemView, int position);

    void onDelete(View itemView, int position);

    void onEdit(View itemView, int position);

    void onReplay(View itemView, int position);

    void onLike(View itemView, int position);
}
